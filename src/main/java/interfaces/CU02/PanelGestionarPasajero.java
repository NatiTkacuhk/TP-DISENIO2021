package main.java.interfaces.CU02;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import main.java.dtos.PasajeroDTO;
import main.java.enums.TipoMensaje;
import main.java.excepciones.InputInvalidaException;
import main.java.excepciones.PasajeroNoSeleccionadoException;
import main.java.excepciones.SinResultadosException;
import main.java.gestores.GestorPasajero;
import main.java.interfaces.CU11.PanelAltaPasajero;
import main.java.interfaces.MenuPrincipal.PanelMenuPrincipal;
import main.java.interfaces.clasesExtra.Mensaje;
import main.java.interfaces.clasesExtra.PanelPermiteMensajes;
import main.java.interfaces.clasesExtra.RoundedBorder;
import main.java.interfaces.frames.FramePrincipal;

public class PanelGestionarPasajero extends JPanel implements PanelPermiteMensajes{
	
	private static final long serialVersionUID = 1L;
	
	// en este panel estan los botones y los dos otros paneles
	private PanelGestionarPasajeroBusqueda panelGestionarPasajeroBusqueda;
	private PanelGestionarPasajeroTabla panelGestionarPasajeroTabla;
	
	public GestorPasajero gestorPasajero;

	private FramePrincipal frameActual;
	private PanelMenuPrincipal panelAnterior;
	
	private String textoMensajeCancelar = "<html><p>�Est� seguro que desea cancelar la operaci�n?</p><html>";
	private Mensaje mensajeCancelar = new Mensaje(1, textoMensajeCancelar, TipoMensaje.CONFIRMACION, "Si", "No");
	
	private String textoMensajeNoExistePasajeroBuscar = "<html><p>No existe ning�n pasajero con los criterios de b�squeda"
														+ " seleccionados. �Desea agregar un nuevo pasajero?</p><html>";
	private Mensaje mensajeNoExistePasajeroBuscar = new Mensaje(2, textoMensajeNoExistePasajeroBuscar, TipoMensaje.CONFIRMACION, "Si", "No");
	
	private String textoMensajeNoExistePasajeroSiguiente = "<html><p>No seleccion� ning�n pasajero. �Desea agregar un nuevo pasajero?";
	private Mensaje mensajeNoExistePasajeroSiguiente = new Mensaje(3, textoMensajeNoExistePasajeroSiguiente, TipoMensaje.CONFIRMACION, "Si", "No");
	
	private String textoModificarPasajero = "<html><p>El CU12 'Modificar Pasajero' no debe ser implementado.</p><html>";
	private Mensaje mensajeModificarPasajero = new Mensaje(4, textoModificarPasajero, TipoMensaje.ERROR, "Aceptar", null);
	
	private JButton buscar;
	private JButton cancelar;
	private JButton siguiente;

	private Insets insetPanelBusqueda = new Insets(30,30,5,30);
	private Insets insetPanelTabla = new Insets(0,30,0,30);
	
	private Dimension dimensionBoton = new Dimension(90, 33);
	
	private RoundedBorder bordeBoton = new RoundedBorder(10, Color.decode("#BDBDBD"));
	
	private Font fuenteBoton = new Font("SourceSansPro", Font.PLAIN, 14);
	
	public PanelGestionarPasajero(final FramePrincipal frame, PanelMenuPrincipal panelAnterior) {
		gestorPasajero = GestorPasajero.getInstance();
		
		this.frameActual = frame;
		this.panelAnterior = panelAnterior;
		
		this.setBackground(Color.WHITE);
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		panelGestionarPasajeroBusqueda = new PanelGestionarPasajeroBusqueda(frameActual);
		c.insets = insetPanelBusqueda;
		c.fill = GridBagConstraints.BOTH; 		c.gridx = 0; c.gridy = 0;	c.gridwidth = 3;
		c.weightx = 0.1; c.weighty = 0.1;			this.add(panelGestionarPasajeroBusqueda, c);
		
		c.weightx = 0.0; c.weighty = 0.0;	
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
				panelGestionarPasajeroTabla.desactivarTabla();	//TODO: Limpiar paginacion
				this.panelGestionarPasajeroBusqueda.inputEsValida();

				PasajeroDTO filtros = panelGestionarPasajeroBusqueda.getFiltros();
			
				gestorPasajero.validarDatosBusqueda(filtros);
				Integer cantResultados = gestorPasajero.buscarCantidadPasajeros(filtros);
				
				panelGestionarPasajeroTabla.buscarResultados(filtros, cantResultados);
				
			}
			catch (InputInvalidaException exc) {
				// La lista posee "Apellido" o "Nombre" o "Documento", dependiendo en cual debe ponerse el labelError
				this.panelGestionarPasajeroBusqueda.colocarLabelInvalido(exc.getCamposInvalidos());
			}
			catch (SinResultadosException exc) {
				mensajeNoExistePasajeroBuscar.mostrar(getPanel(), frameActual);
			}	
		});
		c.anchor = GridBagConstraints.CENTER;		//c.insets = new Insets(0,60,10,0);
		c.gridx = 1; c.gridy = 1;
		this.add(buscar, c);
		
		panelGestionarPasajeroTabla = new PanelGestionarPasajeroTabla();
		c.insets = insetPanelTabla;
		c.fill = GridBagConstraints.BOTH; 		c.gridx = 0; c.gridy = 2;	c.gridwidth = 3;
		c.weightx = 0.8; c.weighty = 0.8;			this.add(panelGestionarPasajeroTabla, c);
		c.weightx = 0.1; c.weighty = 0.1;	
		c.gridwidth = 1;
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
		c.anchor = GridBagConstraints.WEST;		c.insets = new Insets(0,60,10,0);
		c.gridx = 0; c.gridy = 3;
		this.add(cancelar, c);

		siguiente = new JButton("Siguiente");
		siguiente.setMinimumSize(dimensionBoton);
		siguiente.setPreferredSize(dimensionBoton);
		siguiente.setBackground(Color.decode("#E0E0E0"));
		siguiente.setFont(fuenteBoton);
		siguiente.setBorder(bordeBoton);
		siguiente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					@SuppressWarnings("unused")
					PasajeroDTO pasajero = panelGestionarPasajeroTabla.pasajeroSeleccionado();
					
					mensajeModificarPasajero.mostrar(getPanel(), frame);
				}
				catch (PasajeroNoSeleccionadoException exc) {
					mensajeNoExistePasajeroSiguiente.mostrar(getPanel(), frame);
				}
			}
		});
		c.anchor = GridBagConstraints.EAST;		c.insets = new Insets(0,0,10,60);
		c.gridx = 2; c.gridy = 3;
		this.add(siguiente, c);
	}
	
	public PanelPermiteMensajes getPanel() {
		return this;
	}
	

	public void confirmoElMensaje(Integer idMensaje) {
		
		switch(idMensaje) {
		case 1:	//Si cancela, vuelve a MenuPrincipal
			frameActual.setNuevoPanel(panelAnterior);
			break;
		case 2:	//Si no se encontro ning�n pasajero, va a la pantalla de AltaPasajero
		case 3:	//Si no se seleccion� ning�n pasajero, va a la pantalla de AltaPasajero
			frameActual.setNuevoPanel(new PanelAltaPasajero(frameActual, this));	
			break;		
		}
	}
	
	public void confirmoCancelar(Integer idMensaje) {
		
		//Ninguno de los mensajes tiene una funci�n si se presiona el bot�n de la izquierda
	}
}
