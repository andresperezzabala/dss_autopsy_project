package client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;


import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.awt.event.ActionEvent;


import static utils.Utils.loadInstances;
import static utils.Utils.loadModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;



import java.awt.GridLayout;
import javax.swing.JSlider;


public class SAD extends JFrame {

	private String testPath;
	private String modelPath;
	private JPanel contentPane;
	private final JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
	private JTable resultsTable;
	private JScrollPane scrollPane;
	private DefaultTableModel tableModel;
	private Vector<Pairs> instanceList;
	private Classifier cls = null;
	private JSlider slider;
	private int k = 5;
	private JButton btnCargarFichero;
	private boolean tablaCargada = false;
	private TableColumnModel columnModel;
	private HashMap<Integer,TableColumn> allColumns;
	private FixedColumnTable fct;
	private double[][] pAtK;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SAD frame = new SAD();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Create the frame.
	 */
	public SAD() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panelBototones = new JPanel();
		contentPane.add(panelBototones,BorderLayout.NORTH);
		panelBototones.setLayout(new GridLayout(0, 2, 0, 0));
		
		JButton btnCargarModelo = new JButton("Cargar modelo");
		panelBototones.add(btnCargarModelo);
		
		JButton btnCargarFichero = new JButton("Cargar fichero");
		btnCargarFichero.setEnabled(false);
		panelBototones.add(btnCargarFichero);
				
		//TABLA
		resultsTable = new JTable() {
                    
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
		};
		
		resultsTable.setRowSelectionAllowed(false);
		scrollPane = new JScrollPane(resultsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		resultsTable.getTableHeader().setReorderingAllowed(false);
		
		
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		
		//MODEL		
		btnCargarModelo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				int returnVal = fc.showOpenDialog(SAD.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		        	modelPath = fc.getSelectedFile().getPath();
		        	new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							loadClientModel();
							btnCargarFichero.setEnabled(true);
							return null;
					    }
					}.execute();
		        }		        	
		   }
		});
		
		//TEST
		btnCargarFichero.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				int returnVal = fc.showOpenDialog(SAD.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		        	testPath = fc.getSelectedFile().getPath();
		        	new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							loadClientData();
							return null;
					    }
					}.execute();
		        }		        	
		   }
		});
			
		slider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(5);
        slider.addChangeListener(new ChangeListener() {
  	      public void stateChanged(ChangeEvent event) {
  	    	if (tablaCargada) {
  	    		int value = slider.getValue();
  	  	        if (value != k) {
  	  	        	updatePercentageAtK(value,k);
  	  	        	k = value;
  	  	        	fct.updateFixedTable(pAtK[k-1]);
  	  	        }	
  	    	}  	             
  	      }
  	    });
        contentPane.add(slider, BorderLayout.SOUTH);
	}

	public void loadClientModel() {		
		try {
			cls = loadModel(modelPath);
			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error al cargar el modelo",  "ERROR", JOptionPane.ERROR_MESSAGE);
			System.out.println("Error al cargar el modelo");
			btnCargarFichero.setEnabled(false);
		}
	}
	
	public void loadClientData() {
		Instances labeled = null;
		try {
			labeled = loadInstances(testPath);
			labeled.setClass(labeled.attribute("gs_text34"));
		} catch (Exception e) {
			e.printStackTrace();		
			JOptionPane.showMessageDialog(null, "Error al cargar el test",  "ERROR", JOptionPane.ERROR_MESSAGE);
			System.out.println("Error al cargar el test");
			tablaCargada = false;
			
		}
		
		if (k > labeled.numClasses()) k = labeled.numClasses();		
		
		slider.setMaximum(labeled.numClasses());
		slider.setValue(k);
		slider.setPaintTicks(true);
        slider.setPaintLabels(true);		  
        
        tableModel = new DefaultTableModel(0,0);
        
        tableModel.addColumn("Real");
	    tableModel.addColumn("P@k");
	         
        int maxWidth = 0;
        int classValueWidth;
	    for (int column = 1; column <= labeled.numClasses(); column++) {
	    	tableModel.addColumn(column + "º pocisión");
	    	classValueWidth = labeled.classAttribute().value(column-1).length()*6;
	    	if (classValueWidth > maxWidth) maxWidth = classValueWidth; 
	    }
	       	    
	    resultsTable.setModel(tableModel);
	    resultsTable.getColumnModel().getColumn(0).setPreferredWidth(maxWidth);
	    resultsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
	    for (int c = 2; c < labeled.numClasses() + 2; c++) {
	    	 resultsTable.getColumnModel().getColumn(c).setPreferredWidth(maxWidth + 50);
	    }
	    
	    fct = new FixedColumnTable(2, scrollPane);
     
		
		double[] classes;
		
		// data of the table
		pAtK = new double[labeled.numClasses()][labeled.numInstances()];
		
		int i = 0;
		double pAtKvalue;
		for(Instance ins : labeled) {
			pAtKvalue = 0;
			try {
				classes = cls.distributionForInstance(ins);				
				instanceList = new Vector<Pairs>();
				for (int j = 0; j < classes.length; j++) {
					instanceList.add(new Pairs(classes[j],labeled.classAttribute().value(j)));
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Error al obtener las clases");
			}
		
			Collections.sort(instanceList, new Comparator<Pairs>() {
			    @Override
			    public int compare(Pairs p1, Pairs p2) {
			        return p2.getPercentage().compareTo(p1.getPercentage());
			    }
			});			
				
						
			Vector<Object> vector = new Vector<Object>();
			
			vector.addElement(labeled.classAttribute().value((int) ins.classValue()));
		
			
			for (int l = 0; l < instanceList.size(); l++ ) {
				pAtKvalue += instanceList.get(l).getPercentage();
				pAtK[l][i] = pAtKvalue;
				vector.addElement(instanceList.get(l).getResult());
			}
			
			pAtK[instanceList.size()-1][i] = 1;
			
			vector.add(1, pAtK[k-1][i]);
						
			tableModel.addRow(vector);
			
			i++;
		}
		
		
		columnModel = resultsTable.getColumnModel();
				
		allColumns = new HashMap<Integer,TableColumn>();
		for (int x = 0; x < columnModel.getColumnCount() ; x++)
		{
			allColumns.put(x, columnModel.getColumn(x));
		}
		
		tablaCargada = true;
		
		updatePercentageAtK(k, 48);

	}
	
	public void updatePercentageAtK(int pValue, int ant) {
		
		if (pValue > ant) {
			for (int i = ant; i < pValue ; i++)
			{		      
	    	    resultsTable.addColumn(allColumns.get(i));
			}
			
		}else {
			for (int i = pValue + 1; i <= ant; i++)
			{
		        TableColumn column = columnModel.getColumn(pValue);
	    	    resultsTable.removeColumn( column );
			}
			
		}
		
		
	}
	


	
}
