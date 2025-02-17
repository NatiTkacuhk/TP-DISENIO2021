package main.java.interfaces.CU07;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import main.java.dtos.OcupacionDTO;
import main.java.dtos.PasajeroDTO;
import main.java.enums.ColumnaBuscarPasajeros;
import main.java.enums.TipoDocumento;
import main.java.excepciones.PasajeroNoSeleccionadoException;
import main.java.excepciones.ReponsablePagoMenorDeEdadException;
import main.java.gestores.GestorPasajero;
import main.java.interfaces.clasesExtra.ModeloTablaFacturar;
import main.java.interfaces.clasesExtra.RenderParaHeaderTablas;
import main.java.interfaces.clasesExtra.RoundedBorder;
import main.java.interfaces.frames.FramePrincipal;

public class PanelResultadosDeBusquedaFacturarGroupBox extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private JButton facturarANombreDeUnTercero;
	
	private JTable tabla;
	private ModeloTablaFacturar miModelo;
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private Vector filaSeleccionada = null;
	@SuppressWarnings("unused")
	private Integer nroFilaSeleccionada;
	private JScrollPane tableContainer;
	
	private Insets insetTabla = new Insets(15, 100, 15, 100);

	private Font fuenteGroupBox = new Font("SourceSansPro", Font.PLAIN, 18);	
	private Font fuenteBoton = new Font("SourceSansPro", Font.PLAIN, 14);
	
	private RoundedBorder bordeBoton = new RoundedBorder(10, Color.decode("#BDBDBD"));
	
	private FramePrincipal frameActual;
	private FrameFacturarANombreDeUnTercero frameFacturarANombreDeUnTercero;
	
	private Dimension dimensionBoton = new Dimension(250, 33);
	
	private RenderParaHeaderTablas renderTabla;
	
	private OcupacionDTO ocupacion;

	public PanelResultadosDeBusquedaFacturarGroupBox(FramePrincipal frame, PanelFacturar panelFacturar) {
		
		this.frameActual = frame;
		
		this.setBackground(Color.white);
		
		this.setBorder(new TitledBorder (new LineBorder (Color.black, 1), " Resultados de b�squeda", 0, 0, fuenteGroupBox));
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		miModelo = new ModeloTablaFacturar();
		
		//miModelo.cargarPasajeros(ocupacionDTO.getListaPasajerosDTO());
		
		tabla = new JTable(miModelo);
		tableContainer = new JScrollPane(tabla);
		
		renderTabla = new RenderParaHeaderTablas(tabla.getDefaultRenderer(Object.class), false);
		
		tabla.getTableHeader().setDefaultRenderer(renderTabla);
		
		tabla.getTableHeader().setReorderingAllowed(false); //Para que no se muevan las columnas
		
		tabla.setRowSelectionAllowed(true);
		tabla.setColumnSelectionAllowed(false);
		
		tabla.setFocusable(false); //Para que no seleccione una sola columna
		
		tabla.getTableHeader().setResizingAllowed(false);	//Para que no puedas cambiar el tama�o de las columnas
		
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		tabla.setAutoCreateRowSorter(true);	//Para que se ordenen
		
		tabla.setSelectionBackground(Color.decode("#e0e0e0"));
	
		//PARA CENTRAR
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		tabla.setDefaultRenderer(Object.class, centerRenderer);
		
		tabla.setBackground(Color.white);
		tabla.setGridColor(Color.black);
		tabla.setBorder(new LineBorder(Color.BLACK));
		
		//this.add(tableContainer, BorderLayout.CENTER);
		c.fill = GridBagConstraints.BOTH;
		//c.anchor = GridBagConstraints.CENTER;
		c.insets = insetTabla;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		this.add(tableContainer, c);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.gridwidth = 1;
		
		facturarANombreDeUnTercero = new JButton("Facturar a nombre de un tercero");
		facturarANombreDeUnTercero.setEnabled(false);			//Se habilita cuando se aprieta Buscar con campos validos
		facturarANombreDeUnTercero.setMinimumSize(dimensionBoton);
		facturarANombreDeUnTercero.setPreferredSize(dimensionBoton);
		facturarANombreDeUnTercero.setBackground(Color.decode("#E0E0E0"));
		facturarANombreDeUnTercero.setFont(fuenteBoton);
		facturarANombreDeUnTercero.setBorder(bordeBoton);
		facturarANombreDeUnTercero.addActionListener(e -> {
			
			frameActual.setEnabled(false);
			frameFacturarANombreDeUnTercero = new FrameFacturarANombreDeUnTercero(frameActual, panelFacturar); 
			frameFacturarANombreDeUnTercero.ocupacionSeleccionada(ocupacion);
		});
		c.anchor = GridBagConstraints.CENTER;		//c.insets = new Insets(0,60,10,0);
		c.gridy = 1;
		this.add(facturarANombreDeUnTercero, c);
		
		
	}
	
	public PasajeroDTO pasajeroSeleccionado() throws PasajeroNoSeleccionadoException{
	
		if(tabla.getSelectedRow() < 0) {
			
			throw new PasajeroNoSeleccionadoException();
		}				
		
		return this.miModelo.getPasajerosHabitacion().get(tabla.convertRowIndexToModel(tabla.getSelectedRow()));
	}
	
	public void activarTabla(OcupacionDTO ocupacionDTO) {
		
		facturarANombreDeUnTercero.setEnabled(true);
		miModelo.actualizarTabla(ocupacionDTO.getListaPasajerosDTO());
		this.ocupacion = ocupacionDTO;
	}
	
	public void desactivarTabla() {
		
		facturarANombreDeUnTercero.setEnabled(false);
		miModelo.limpiarTabla();
	}
}
