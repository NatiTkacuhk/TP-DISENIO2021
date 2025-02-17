package main.java.interfaces.CU11;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import main.java.dtos.PasajeroDTO;
import main.java.enums.TipoMensaje;
import main.java.excepciones.DocumentoRepetidoException;
import main.java.gestores.GestorPasajero;
import main.java.interfaces.CU02.PanelGestionarPasajero;
import main.java.interfaces.MenuPrincipal.PanelMenuPrincipal;
import main.java.interfaces.clasesExtra.Mensaje;
import main.java.interfaces.clasesExtra.PanelPermiteMensajes;
import main.java.interfaces.clasesExtra.RoundedBorder;
import main.java.interfaces.frames.FramePrincipal;

public class PanelAltaPasajero extends JPanel implements PanelPermiteMensajes{
	
	private static final long serialVersionUID = 1L;
	
	private PanelAltaPasajeroDatos panelDarAltaPasajero;
	
	private JButton cancelar;
	private JButton siguiente;

	private Insets insetPanel = new Insets(30,30,10,30);
	
	private Dimension dimensionBoton = new Dimension(90, 33);
	
	private RoundedBorder bordeBoton = new RoundedBorder(10, Color.decode("#BDBDBD"));
	
	private Font fuenteBoton = new Font("SourceSansPro", Font.PLAIN, 14);
	
	private FramePrincipal frameActual;
	private PanelGestionarPasajero panelAnterior;
	
	private String textoMensajeCancelar = "<html><p>�Est� seguro que desea cancelar la operaci�n?</p><html>";
	private Mensaje mensajeCancelar = new Mensaje(1, textoMensajeCancelar, TipoMensaje.CONFIRMACION, "Si", "No");
	
	private String textoMensajeDocumentoRepetido = "<html><p>�CUIDADO! El tipo y n�mero de documento ya existen en el sistema.</p><html>";
	private Mensaje mensajeDocumentoRepetido = new Mensaje(2, textoMensajeDocumentoRepetido, TipoMensaje.EXITO, "Aceptar Igualmente", "Corregir");
	
	private String textoMensajePasajeroCreado = "<html><p>El pasajero se agreg� al sistema correctamente.</p><html>";
	private Mensaje mensajePasajeroCreado = new Mensaje(3, textoMensajePasajeroCreado, TipoMensaje.EXITO, "Aceptar", null);
	
	private PasajeroDTO pasajeroDTO;
	private GestorPasajero gestorPasajero;
	
	public PanelAltaPasajero(final FramePrincipal frame, PanelGestionarPasajero panelAnterior) {
		this.frameActual = frame;
		this.panelAnterior = panelAnterior;
		
		this.setBackground(Color.WHITE);
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		panelDarAltaPasajero  = new PanelAltaPasajeroDatos(frameActual);
		c.insets = insetPanel;
		c.fill = GridBagConstraints.BOTH; 		c.gridx = 0; c.gridy = 0;	c.gridwidth = 2;
		c.weightx = 0.5; c.weighty = 0.5;			this.add(panelDarAltaPasajero, c);
		c.weightx = 0.1; c.weighty = 0.1;	
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0,0,0,0);
		
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
		c.gridx = 0; c.gridy = 1;
		this.add(cancelar, c);

		siguiente = new JButton("Siguiente");
		siguiente.setMinimumSize(dimensionBoton);
		siguiente.setPreferredSize(dimensionBoton);
		siguiente.setBackground(Color.decode("#E0E0E0"));
		siguiente.setFont(fuenteBoton);
		siguiente.setBorder(bordeBoton);
		siguiente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(panelDarAltaPasajero.inputEsNoVacio()) {
					
					if(panelDarAltaPasajero.inputTieneFormatoValido()) {
						
						pasajeroDTO = panelDarAltaPasajero.crearDTOS();
						
						gestorPasajero = GestorPasajero.getInstance();
						
						try {
							gestorPasajero.validarDatosPasajero(pasajeroDTO);
							
							gestorPasajero.crearPasajero(pasajeroDTO);
							
							mensajePasajeroCreado.mostrar(getPanel(), frame);
						} catch (DocumentoRepetidoException e1) {
							
							// Informar que un pasajero con ese dni ya existe
							mensajeDocumentoRepetido.mostrar(getPanel(), frame);
						
						}
						
						
					}	
				}
					
					//mensajeDocumentoRepetido.mostrar(getPanel(), frame); //Hay que crear luego la validaci�n de que no se repitan DNIs
			}
		});
		c.anchor = GridBagConstraints.EAST;		c.insets = new Insets(0,0,10,60);
		c.gridx = 1; c.gridy = 1;
		this.add(siguiente, c);
	}
	
	public PanelPermiteMensajes getPanel() {
		return this;
	}

	public void confirmoElMensaje(Integer idMensaje) {
		
		switch(idMensaje) {
		case 1:	//Si cancela, vuelve a GestionarPasajero
			frameActual.setNuevoPanel(panelAnterior);
			break;
		case 2:	//Si tiene documento repetido, se guarda igualmente (primero muestra el mensaje de que se cre� el pasajero)
			gestorPasajero.crearPasajero(pasajeroDTO);
			mensajePasajeroCreado.mostrar(getPanel(), frameActual);
			break;
		case 3:	//Si se creo el pasajero, vuelve al MenuPrincpal
			frameActual.setNuevoPanel(new PanelMenuPrincipal(frameActual));
			break;		
		}
		

	}

	public void confirmoCancelar(Integer idMensaje) {
		
		switch(idMensaje) {
		case 1:	//Si no quiere cancelar, no pasa nada
			break;
		case 2:	//Si quiere corregir, se centra en el campo NumeroDocumento
			this.panelDarAltaPasajero.centrarDocumento();			
			break;		
		}	

	}

}

