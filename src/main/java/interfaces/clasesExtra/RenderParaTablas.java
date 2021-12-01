package main.java.interfaces.clasesExtra;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class RenderParaTablas implements TableCellRenderer{
	
	DefaultTableCellRenderer render;
    Border b;
    public RenderParaTablas(TableCellRenderer r){
        render = (DefaultTableCellRenderer) r;
        render.setHorizontalAlignment(JLabel.CENTER);
        //It looks funky to have a different color on each side - but this is what you asked
        //You can comment out borders if you want too. (example try commenting out top and left borders)
        b = BorderFactory.createCompoundBorder(b, BorderFactory.createLineBorder(Color.BLACK));
    }

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
        JComponent result = (JComponent)render.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        result.setBorder(b);
        result.setOpaque(true);
        result.setBackground(Color.decode("#424242"));
        result.setForeground(Color.WHITE);
        result.setPreferredSize(new Dimension(400, 40));
       // result.setAlignmentX(JLabel.CENTER);
        
        return result;
	}
	
	
}
