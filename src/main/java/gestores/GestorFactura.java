package main.java.gestores;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.WindowConstants;

import main.java.clases.Consumo;
import main.java.clases.DatosResponsableDePago;
import main.java.clases.Factura;
import main.java.clases.Habitacion;
import main.java.clases.ItemConsumo;
import main.java.clases.ItemFactura;
import main.java.clases.ItemOcupacion;
import main.java.clases.Ocupacion;
import main.java.clases.ResponsableDePago;
import main.java.daos.FacturaDAO;
import main.java.daos.HabitacionDAO;
import main.java.daos.OcupacionDAO;
import main.java.daos.ResponsableDePagoDAO;
import main.java.dtos.FacturaDTO;
import main.java.dtos.ItemFacturaImpresionDTO;
import main.java.dtos.ItemFilaDTO;
import main.java.dtos.OcupacionDTO;
import main.java.enums.PosicionFrenteIva;
import main.java.enums.TipoFactura;
import main.java.excepciones.NingunElementoSeleccionadoFacturacionException;
import main.java.excepciones.RecargoNoEstaEnUltimaFacturaException;
import main.java.postgreImpl.FacturaPostgreSQLImpl;
import main.java.postgreImpl.HabitacionPostgreSQLImpl;
import main.java.postgreImpl.OcupacionPostgreSQLImpl;
import main.java.postgreImpl.ResponsableDePagoPostgreSQLImpl;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class GestorFactura {
private static GestorFactura instance;
	
	private FacturaDAO facturaDAO;
	private ResponsableDePagoDAO responsableDAO;
	private HabitacionDAO habitacionDAO;
	private OcupacionDAO ocupacionDAO;
	private Boolean seFacturaronTodosLosItems;
	private Boolean tieneRecargo;
	//private ConsumoDAO consumoDAO;
	
	private Factura ultimaFactura;
	
	private GestorFactura() {
		facturaDAO = new FacturaPostgreSQLImpl();
		responsableDAO = new ResponsableDePagoPostgreSQLImpl();
		habitacionDAO = new HabitacionPostgreSQLImpl();
		ocupacionDAO = new OcupacionPostgreSQLImpl();
	}
	
	public static GestorFactura getInstance() {
		if (instance == null) instance = new GestorFactura();
		
		return instance;
	}
	
	public Integer crearFactura(FacturaDTO facturaDTO, OcupacionDTO ocupacionDTO) throws NingunElementoSeleccionadoFacturacionException,
																					  RecargoNoEstaEnUltimaFacturaException {
		
		seFacturaronTodosLosItems = false;
		tieneRecargo = false;
		
		Ocupacion ocupacion = ocupacionDAO.buscarExtendido(ocupacionDTO.getId());
		List<ItemFactura> listaItemsFactura = crearListaItemsFactura(facturaDTO.getListaItemsFila(), ocupacion);
		
		if(listaItemsFactura.isEmpty()) {
			throw new NingunElementoSeleccionadoFacturacionException();
		}
		
		/* Si selecciono para facturar un recargo pero hay otros items por facturar no se deber�a permitir,
		 ya que los recargos son lo �ltimo que se deben facturar (deben estar en la ultima factura)*/
		if(tieneRecargo && !seFacturaronTodosLosItems) {
			throw new RecargoNoEstaEnUltimaFacturaException();
		}
		
		if(seFacturaronTodosLosItems) {
			ocupacion.setHoraYFechaSalidaReal(ocupacionDTO.getPosibleFechaHoraDeSalida());
			
			if(ocupacionDTO.getPosibleFechaHoraDeSalida().toLocalDate().isBefore(ocupacionDTO.getFechaEgreso())) {
				ocupacion.setEgreso(ocupacionDTO.getPosibleFechaHoraDeSalida().toLocalDate());
			}
				
			ocupacionDAO.guardar(ocupacion);
		}
		
		Habitacion habitacion = ocupacion.getHabitacion();
		
		ResponsableDePago responsablePago = responsableDAO.buscar(facturaDTO.getResponsablePagoDTO().getId());
		
		DatosResponsableDePago datosResponsable = new DatosResponsableDePago(responsablePago);
		
		Factura factura = new Factura(facturaDTO, habitacion, responsablePago, datosResponsable, listaItemsFactura);
		
		return facturaDAO.guardar(factura);
	}
	
	public List<ItemFactura> crearListaItemsFactura(List<ItemFilaDTO> listaItems, Ocupacion ocupacion){
		
		List<ItemFactura> retorno = new ArrayList<ItemFactura>();
		Integer cantCosasAFacturar = 0;
		Integer cantCosasFacturadas = 0;
		Integer cantSeleccionadaEnItem;
		
		for(ItemFilaDTO unItem : listaItems) {
			
			cantCosasAFacturar += unItem.getCantidadMax();
			
			cantSeleccionadaEnItem = unItem.getCantidadSeleccionada();
			
			if(cantSeleccionadaEnItem > 0) {
				
				cantCosasFacturadas += cantSeleccionadaEnItem;
			
				ItemFactura itemFactura;
				
				if(unItem.esItemOcupacion()) {
					
					if(unItem.esRecargo()) {
						tieneRecargo = true;
					}
					
					itemFactura = new ItemOcupacion(unItem, ocupacion);
					
				} else {
					
					Consumo consumo = ocupacion.getConsumos().stream().filter(c -> c.getId() == unItem.getIdAsociado()).findFirst().get();
					
					itemFactura = new ItemConsumo(unItem, consumo);
					
				}
				
				retorno.add(itemFactura);
			}
		}
		
		/* Significa que factur� todos los items que tenia pendiente, la ocupacion deber�a darse por terminada
		 y se deber�a setar la fecha y hora final*/
		if (cantCosasAFacturar == cantCosasFacturadas) {
			seFacturaronTodosLosItems = true;
		}
		
		return retorno;
	}
	
	public void imprimir(Integer idFactura) {
			
		Factura f = facturaDAO.buscarConItems(idFactura);
		
		try {		
			JasperReport report;
			
			List<ItemFacturaImpresionDTO> itemsDTO = new ArrayList<>();			
			
			Map<String, Object> parameters = new HashMap<>();
			
			Double subtotal = f.getMontoTotal()*(100.0/121);
			
			final DecimalFormat df = new DecimalFormat("0.00");
			
			
			
			for (ItemFactura i : f.getItems()) {
				itemsDTO.add(new ItemFacturaImpresionDTO(i));
			}
			
			for (int i=itemsDTO.size(); i<10; i++) {
				itemsDTO.add(new ItemFacturaImpresionDTO());
			}
			
			
			JRBeanCollectionDataSource items = new JRBeanCollectionDataSource(itemsDTO);
			
						
			
			parameters.put("razonSocial", f.getDatosResponsable().getRazonSocial());
			parameters.put("direccion", f.getDatosResponsable().getDireccion().getDireccionDomicilio());
			parameters.put("cuit", f.getDatosResponsable().getCuit());
			parameters.put("numero", f.getNumero());
			parameters.put("localidad", f.getDatosResponsable().getDireccion().getLocalidad().getNombre());
			parameters.put("montoTotal", "$ " + df.format(f.getMontoTotal()));
			parameters.put("items", items);
			parameters.put("fecha", f.getFechaFacturacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString());
			
			if (f.getTipo() == TipoFactura.A) {
				parameters.put("posicionIva", "R.I.");
				parameters.put("montoNeto", "$ " + df.format(subtotal));
				parameters.put("iva", "$ " + df.format(f.getMontoTotal() - subtotal));
				
				report = (JasperReport) JRLoader.loadObject(getClass().getResource("../reportes/reportes/FacturaA.jasper"));
			}
			else {
				parameters.put("posicionIva", "CONSUMIDOR FINAL");
				
				report = (JasperReport) JRLoader.loadObject(getClass().getResource("../reportes/reportes/FacturaB.jasper"));
			}
			
			
			JasperPrint jprint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
			JasperViewer view = new JasperViewer(jprint, false);
			view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			view.setVisible(true);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
