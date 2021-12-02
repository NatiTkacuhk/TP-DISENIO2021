package main.java.interfaces.CU07;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import main.java.dtos.OcupacionDTO;
import main.java.dtos.ResponsableDePagoDTO;

public class FrameFacturarConsumos extends JFrame{
	
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	public FrameFacturarConsumos(OcupacionDTO ocupacionDTO, ResponsableDePagoDTO responsableDTO) {
		super("Sistema Hotel Premier");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 600);
		contentPane = new PanelFacturarConsumos(this, ocupacionDTO, responsableDTO);
		setContentPane(contentPane);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
}
