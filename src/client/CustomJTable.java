package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;



public class CustomJTable extends JTable{

	public CustomJTable() {
		this.setRowSelectionAllowed(false);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.getTableHeader().setReorderingAllowed(false);	
		Font f = new Font("Arial", Font.PLAIN, 16);
		this.getTableHeader().setFont(f);
		this.setFont(f);
		this.setRowHeight(20);
	}
	
	public boolean isCellEditable(int row, int column) {                
		return false;               
	};			
	
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
        Component returnComp = super.prepareRenderer(renderer, row, column);
        Color alternateColor = new Color(252,242,206);
        Color whiteColor = Color.WHITE;
        if (!returnComp.getBackground().equals(getSelectionBackground())){
            Color bg = (row % 2 == 0 ? alternateColor : whiteColor);
            returnComp .setBackground(bg);
            bg = null;
        }
        return returnComp;
	}
}
