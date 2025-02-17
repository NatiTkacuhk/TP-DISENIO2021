package main.java.interfaces.CU17;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.java.dtos.HabitacionDTO;
import main.java.dtos.OcupacionDTO;
import main.java.dtos.PasajeroDTO;
import main.java.enums.TipoMensaje;
import main.java.excepciones.InputInvalidaException;
import main.java.excepciones.ResponsableMenorException;
import main.java.excepciones.SinResultadosException;
import main.java.gestores.GestorHabitacion;
import main.java.gestores.GestorPasajero;
import main.java.interfaces.CU02.PanelGestionarPasajeroTabla;
import main.java.interfaces.CU05.PanelMostrarEstadoHabitaciones;
import main.java.interfaces.clasesExtra.Mensaje;
import main.java.interfaces.clasesExtra.MensajeAyuda;
import main.java.interfaces.clasesExtra.PanelPermiteMensajes;
import main.java.interfaces.clasesExtra.RoundedBorder;
import main.java.interfaces.frames.FramePrincipal;

public class PanelOcuparHabitacionConPasajeros extends JPanel implements PanelPermiteMensajes{
	
	private static final long serialVersionUID = 1L;
	
	// en este panel estan los botones y los dos otros paneles
	private PanelOcuparHabitacionBusqueda panelOcuparHabitacionBusqueda;
	private PanelOcuparHabitacionTabla panelOcuparHabitacionTabla;
	
	private PanelPasajerosSeleccionadosGroupBox panelPasajerosSeleccionadosGroupBox;
	private PanelInformacionGroupBox panelInformacionGroupBox;

	private FramePrincipal frameActual;
	private PanelMostrarEstadoHabitaciones panelAnterior;
	
	public GestorPasajero gestorPasajero;
	private GestorHabitacion gestorHabitacion;
	
	private String textoMensajeCancelar = "<html><p>�Est� seguro que desea cancelar la operaci�n?</p><html>";
	private Mensaje mensajeCancelar = new Mensaje(1, textoMensajeCancelar, TipoMensaje.CONFIRMACION, "Si", "No");
	
	private String textoMensajeNoExistePasajeroBuscar = "<html><p>No existe ning�n pasajero con los criterios de b�squeda"
														+ " seleccionados. Por favor, revise y vuelva a intentar.</p><html>";
	private Mensaje mensajeNoExistePasajeroBuscar = new Mensaje(2, textoMensajeNoExistePasajeroBuscar, TipoMensaje.CONFIRMACION, "Aceptar", null);
	
	private String textoPasajerosNoSeleccionados = 	"<html><p>Seleccione al menos un ocupante.</p><html>";
	private Mensaje mensajePasajerosNoSeleccionados = new Mensaje(3, textoPasajerosNoSeleccionados, TipoMensaje.ERROR, "Aceptar", null);
	
	private String textoResponsableMenorDeEdad = 	"<html><p>El responsable seleccionado es menor de edad. Por favor, seleccione otro.</p><html>";
	private Mensaje mensajeResponsableMenorDeEdad = new Mensaje(4, textoResponsableMenorDeEdad, TipoMensaje.ERROR, "Aceptar", null);
	
	private String textoMensajeAyuda = "<html> Ocupar Habitaci�n<br/><br/>"
			+ "VER MENSAJE de esta pantalla es poder hacer el check in de un pasajero en una habitaci�n que est� disponible y en un "
			+ "per�odo de fechas que comience desde la fecha de hoy.<br/>"
			+ " Para esto, el sistema brinda un campo de texto catalogado como 'Fecha hasta' en el cual debe introducirse una fecha que contenga la fecha en "
			+ "la que el usuario desea retirarse del hotel (hacer el check out).<br/>"
			+ " Seguido de esto debe apretar 'Buscar' y el sistema brindar� ahora una grilla en la que pueden visualizarse las habitaciones como nombres de las "
			+ "columnas y las fechas como filas. El usuario ahora debe:<br/><br/>"
			+ " - Con el click izquierdo, seleccionar la fecha final del per�odo que desea seleccionar. En caso de seleccionar un per�odo inv�lido (porque la habitaci�n"
			+ " est� ocupada el d�a de hoy o porque selecciona un per�odo en el cual la habitaci�n est� fuera de servicio), el sistema presenta un mensaje de error "
			+ "para que el usuario deba seleccionar otra fecha.<br/><br/>"
			+ " - En caso de que desee deseleccionar un per�odo, el usuario deber� posicionarse sobre el per�odo seleccionado y apretar el click derecho. Luego de esto, "
			+ "se le permitir� seleccionar otro per�odo v�lido.<br/><br/>"
			+ " - Cuando ya tenga seleccionado el per�odo en el que se ocupar� la habitaci�n, presionar� el bot�n 'Siguiente'. En caso de no haber seleccionado ning�n "
			+ "per�odo, se mostrar� un mensaje de error.<br/><br/>"
			+ " - Si el usuario desea cargar otro rango de fechas, podr� modificar el campo 'Fecha hasta' y luego el bot�n 'Buscar' para que la grilla se actualice.</html>";
	private MensajeAyuda mensajeAyuda = new MensajeAyuda(textoMensajeAyuda); 
	
	private JButton buscar;
	private JButton cancelar;
	private JButton ayuda;
	private JButton siguiente;

	private Insets insetPanelBusqueda = new Insets(30,30,5,30);
	private Insets insetBuscar = new Insets(0,0,0,0);
	private Insets insetPanelTabla = new Insets(0,30,0,0);
	private Insets insetPanelPasajerosSeleccionados = new Insets(0,0,0,10);
	private Insets insetPanelInformacion = new Insets(0,0,0,10);
	private Insets insetCancelar = new Insets(0,60,10,0);
	private Insets insetAyuda = new Insets(0,15,10,0);
	private Insets insetSiguiente = new Insets(0,0,10,60);
	
	private Dimension dimensionBoton = new Dimension(90, 33);
	private Dimension dimensionBotonAyuda = new Dimension(130, 33);
	
	private RoundedBorder bordeBoton = new RoundedBorder(10, Color.decode("#BDBDBD"));
	
	private Font fuenteBoton = new Font("SourceSansPro", Font.PLAIN, 14);

	private Integer idHabitacion;
	private LocalDate ingreso;
	private LocalDate egreso;
	
	public PanelOcuparHabitacionConPasajeros(final FramePrincipal frame, PanelMostrarEstadoHabitaciones panelAnterior, HabitacionDTO habitacion, LocalDate fechaDesde, LocalDate fechaHasta) {
				
		this.idHabitacion = habitacion.getId();
		this.ingreso = fechaDesde;
		this.egreso = fechaHasta;
		
		gestorPasajero = GestorPasajero.getInstance();
		gestorHabitacion = GestorHabitacion.getInstance();
		
		this.frameActual = frame;
		this.panelAnterior = panelAnterior;
		
		this.setBackground(Color.WHITE);
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		panelOcuparHabitacionBusqueda = new PanelOcuparHabitacionBusqueda(frameActual);
		c.insets = insetPanelBusqueda;
		c.fill = GridBagConstraints.BOTH; 		c.gridx = 0; c.gridy = 0;	c.gridwidth = 3;
		c.weightx = 0.1; c.weighty = 0.1;			this.add(panelOcuparHabitacionBusqueda, c);
		
		c.weightx = 0.1; c.weighty = 0.0;	
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0,0,0,0);
		
		buscar = new JButton("Buscar");
		buscar.setMinimumSize(dimensionBoton);
		buscar.setPreferredSize(dimensionBoton);
		buscar.setBackground(Color.decode("#E0E0E0"));
		buscar.setFont(fuenteBoton);
		buscar.setBorder(bordeBoton);
		buscar.addActionListener(e -> {
			try{
				panelOcuparHabitacionTabla.desactivarTabla();	//TODO: Limpiar paginacion
				this.panelOcuparHabitacionBusqueda.inputEsValida();
				
				PasajeroDTO filtros = panelOcuparHabitacionBusqueda.getFiltros();
			
				gestorPasajero.validarDatosBusqueda(filtros);
				Integer cantResultados = gestorPasajero.buscarCantidadPasajeros(filtros);
				
				//prueba.buscarResultados(filtros, cantResultados);
				panelOcuparHabitacionTabla.buscarResultados(filtros, cantResultados);
				
			}
			catch (InputInvalidaException exc) {
				// La lista posee "Apellido" o "Nombre" o "Documento", dependiendo en cual debe ponerse el labelError
				this.panelOcuparHabitacionBusqueda.colocarLabelInvalido(exc.getCamposInvalidos());
			}
			catch (SinResultadosException exc) {
				mensajeNoExistePasajeroBuscar.mostrar(getPanel(), frameActual);
			}	
		});
		c.anchor = GridBagConstraints.CENTER;		c.insets = insetBuscar;
		c.gridx = 1; c.gridy = 1;
		this.add(buscar, c);
		
		panelPasajerosSeleccionadosGroupBox = new PanelPasajerosSeleccionadosGroupBox(this, habitacion);	//Los cambie de orden para que al panel de abajo se le pase un panel no null
		c.insets = insetPanelPasajerosSeleccionados;
		c.fill = GridBagConstraints.BOTH; 		c.gridx = 2; c.gridy = 2;
		c.weightx = 0.3; c.weighty = 0.6;			this.add(panelPasajerosSeleccionadosGroupBox, c);	
		c.weightx = 0.1; c.weighty = 0.1;	
		c.fill = GridBagConstraints.NONE;
		
		panelOcuparHabitacionTabla = new PanelOcuparHabitacionTabla(panelPasajerosSeleccionadosGroupBox);	
		c.insets = insetPanelTabla;
		c.fill = GridBagConstraints.BOTH; 		c.gridx = 0; c.gridy = 2;	c.gridwidth = 2;	c.gridheight = 2;
		c.weightx = 0.8; c.weighty = 0.8;			this.add(panelOcuparHabitacionTabla, c);
		c.weightx = 0.1; c.weighty = 0.1;	
		c.gridwidth = 1;	c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		
		panelInformacionGroupBox = new PanelInformacionGroupBox(habitacion);
		c.insets = insetPanelInformacion;
		c.fill = GridBagConstraints.BOTH; 		c.gridx = 2; c.gridy = 3;
		c.weightx = 0.3; c.weighty = 0.2;			this.add(panelInformacionGroupBox, c);
		c.weightx = 0.1; c.weighty = 0.1;	
		c.fill = GridBagConstraints.NONE;
		
		cancelar = new JButton("Cancelar");
		cancelar.setMinimumSize(dimensionBoton);
		cancelar.setPreferredSize(dimensionBoton);
		cancelar.setBackground(Color.decode("#E0E0E0"));
		cancelar.setFont(fuenteBoton);
		cancelar.setBorder(bordeBoton);
		cancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				mensajeCancelar.mostrar(getPanel(), frame);
			}
		});
		c.anchor = GridBagConstraints.WEST;		c.insets = insetCancelar;
		c.gridx = 0; c.gridy = 4;
		this.add(cancelar, c);
		
		ayuda = new JButton("Ayuda");
		ImageIcon imagenAyuda = new ImageIcon("src/main/java/interfaces/clasesExtra/AyudaPrueba.PNG");
		ayuda.setIcon(imagenAyuda);
		ayuda.setHorizontalAlignment(SwingConstants.LEFT);
		ayuda.setMinimumSize(dimensionBotonAyuda);
		ayuda.setPreferredSize(dimensionBotonAyuda);
		ayuda.setBackground(Color.decode("#E0E0E0"));
		ayuda.setFont(fuenteBoton);
		ayuda.setBorder(bordeBoton);
		ayuda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				mensajeAyuda.mostrar(getPanel(), frame);
			}
		});
		c.anchor = GridBagConstraints.CENTER;		c.insets = insetAyuda;
		c.gridx = 1; c.gridy = 4;
		this.add(ayuda, c);

		siguiente = new JButton("Siguiente");
		siguiente.setMinimumSize(dimensionBoton);
		siguiente.setPreferredSize(dimensionBoton);
		siguiente.setBackground(Color.decode("#E0E0E0"));
		siguiente.setFont(fuenteBoton);
		siguiente.setBorder(bordeBoton);
		siguiente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<PasajeroDTO> pasajeros = panelPasajerosSeleccionadosGroupBox.getPasajerosSeleccionados();
				
				if (pasajeros.isEmpty()) mensajePasajerosNoSeleccionados.mostrar(getPanel(), frameActual);				
				else {
					try {
						gestorPasajero.validarEdad(pasajeros.get(0).getId());
						OcupacionDTO ocupacionDTO = new OcupacionDTO(idHabitacion, ingreso, egreso, pasajeros, pasajeros.get(0));
						
						frameActual.cambiarTamanio(350, 400);
						frameActual.setNuevoPanel(new PanelMenuOcuparHabitacion(frameActual, (PanelOcuparHabitacionConPasajeros) getPanel(), ocupacionDTO));		
					
					}
					catch (ResponsableMenorException exc) {
						mensajeResponsableMenorDeEdad.mostrar(getPanel(), frame);
					}
				}
					
				
				
			}
		});
		c.anchor = GridBagConstraints.EAST;		c.insets = insetSiguiente;
		c.gridx = 2; c.gridy = 4;
		this.add(siguiente, c);
	}
	
	public PanelPermiteMensajes getPanel() {
		return this;
	}
	

	public void confirmoElMensaje(Integer idMensaje) {
		
		switch(idMensaje) {
		case 1:	//Si cancela, vuelve al panel anterior
			frameActual.setNuevoPanel(panelAnterior);
			break;
		case 2:	//
			break;
		case 3:	//
			break;		
		}
	}
	
	public void confirmoCancelar(Integer idMensaje) {
		
		//Ninguno de los mensajes tiene una funci�n si se presiona el bot�n de la izquierda
	}
	
	public void setCantPasajerosSeleccionados(Integer cant) {
		panelInformacionGroupBox.setSeleccionados(cant);
	}
}
