package main.java.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter.SortKey;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import main.java.interfaces.clasesExtra.ModeloTablaPasajeros;
import main.java.interfaces.clasesExtra.RoundedBorder;

public class PanelGestionarPasajeroTablaPrueba extends JPanel{

	private JTable tabla;
	private ModeloTablaPasajeros miModelo;
	
	private JLabel label;
	
	private Vector filaSeleccionada = null;
	private Integer nroFilaSeleccionada;
	private JScrollPane tableContainer;
	
	private Insets insetTabla = new Insets(15, 100, 15, 100);
	
	private Font fuenteLabelCampo =new Font("SourceSansPro", Font.PLAIN, 14);
	private Font fuenteGroupBox = new Font("SourceSansPro", Font.PLAIN, 18);	
	
	//private RoundedBorder bordeCampo = new RoundedBorder(5, Color.decode("#BDBDBD"));
	
	//Predicate<Pasajero> FiltroApellido, FiltroNombre, FiltroTipoDocumento, FiltroNumeroDocumento;
	
	public PanelGestionarPasajeroTablaPrueba(JFrame frame) {
		
		this.setBackground(Color.WHITE);
		
		this.setBorder(new TitledBorder (new LineBorder (Color.black, 1), "Resultados de b�squeda", 0, 0, fuenteGroupBox));
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		miModelo = new ModeloTablaPasajeros();
		
		//miModelo.cargarEstaciones(gestorEstacion.getEstaciones());
		
		tabla = new JTable(miModelo);
		tableContainer = new JScrollPane(tabla);
		
		tabla.getTableHeader().setReorderingAllowed(false); //Para que no se muevan las columnas
		
		tabla.setRowSelectionAllowed(true);
		tabla.setColumnSelectionAllowed(false);
		
		tabla.setFocusable(false); //Para que no seleccione una sola columna
		
		tabla.getTableHeader().setResizingAllowed(false);	//Para que no puedas cambiar el tama�o de las columnas
		
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		tabla.setAutoCreateRowSorter(true);	//Para que se ordenen
		
		tabla.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {				
				filaSeleccionada = miModelo.getDataVector().elementAt(tabla.getSelectedRow());
				nroFilaSeleccionada = tabla.getSelectedRow();
			}
			
			
		});
		
		
		//PARA CENTRAR
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		tabla.setDefaultRenderer(Object.class, centerRenderer);
		
		tabla.setBackground(Color.white);
		tabla.setGridColor(Color.white);
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
		
			c.anchor = GridBagConstraints.CENTER; c.insets = new Insets(0,0,0,0);
		
		label = new JLabel("PAGINACI�N");	label.setFont(fuenteLabelCampo);	c.gridx = 0; c.gridy = 1;	this.add(label, c);
		
		JButton boton = new JButton();
		boton.addActionListener(e -> {
			SortKey sortkey = tabla.getRowSorter().getSortKeys().get(0);
			System.out.println(tabla.getColumn(sortkey.getColumn()).getIdentifier());
			System.out.println(sortkey.getSortOrder().toString());
			
		});
		GridBagConstraints gbc_n = new GridBagConstraints();
		gbc_n.gridx = 4;
		gbc_n.gridy = 0;
		gbc_n.weightx = 1.0;
		gbc_n.weighty = 1.0;
		this.add(boton, gbc_n);

	
	}
}

//public void actualizarTabla(String[] campos) {
//	
//	miModelo.limpiarTabla();
//	
//	filtroId = (campos[0] == null) ? e -> true : e -> e.getId().toString().contains(campos[0]);
//	
//	filtroNombre = (campos[1] == null) ? e -> true : e -> e.getNombre().toUpperCase().contains(campos[1].toUpperCase()); 
//	
//	filtroHoraApertura = (campos[2] == null) ? e -> true : e -> ((Integer) e.getHorarioApertura().getHour()).toString().contains(campos[2]); // == (Integer.parseInt(campos[2]));
//	
//	filtroMinutoApertura = (campos[3] == null) ? e -> true : e -> ((Integer) e.getHorarioApertura().getMinute()).toString().contains(campos[3]); // == (Integer.parseInt(campos[3]));
//	
//	filtroHoraCierre = (campos[4] == null) ? e -> true : e -> ((Integer) e.getHorarioCierre().getHour()).toString().contains(campos[4]); // == (Integer.parseInt(campos[4]));
//	
//	filtroMinutoCierre = (campos[5] == null) ? e -> true : e -> ((Integer)e.getHorarioCierre().getMinute()).toString().contains(campos[5]); // == (Integer.parseInt(campos[5])); 
//	
//	List<Estacion> estaciones = gestorEstacion.getEstaciones().stream().filter(filtroId)
//																	   .filter(filtroNombre)
//																	   .filter(filtroHoraApertura)
//																	   .filter(filtroMinutoApertura)
//																	   .filter(filtroHoraCierre)
//																	   .filter(filtroMinutoCierre)
//																	   .collect(Collectors.toList());
//	miModelo.cargarEstaciones(estaciones);
//	
//}
	
	
