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
import javax.swing.table.DefaultTableModel;
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
import java.util.HashMap;
import java.util.Vector;
import java.awt.event.ActionEvent;


import static utils.Utils.loadInstances;
import static utils.Utils.loadModel;

import java.awt.BorderLayout;

import java.awt.GridLayout;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.BoxLayout;


public class SAD extends JFrame {

	private String testPath;
	private String modelPath;
	private JPanel contentPane;
	private final JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
	private JTable resultsTable;
	private JTable infoTable;
	private DefaultTableModel resultsModel;
	private DefaultTableModel infoModel;
	private TableColumnModel resultsColumnModel;
	private JScrollPane scrollPane;
	private Vector<Pairs> instanceList;
	private Classifier cls = null;
	private int k = 5;
	private JButton btnCargarFichero;
	private boolean tablaCargada = false;
	private Integer[] pK;
	private String[] pAtK;
	private HashMap<Integer,TableColumn> allColumns;
	private double totalReciprocalRank;
	private JLabel precisionAtKvalue;
	private JLabel meanRRvalue;
	private JSlider slider;
	private static java.text.DecimalFormat sf = new java.text.DecimalFormat("0.##E0");

	
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
		
		JPanel panelButtons = new JPanel();
		contentPane.add(panelButtons,BorderLayout.NORTH);
		panelButtons.setLayout(new GridLayout(0, 2, 0, 0));
		
		JButton btnCargarModelo = new JButton("Cargar modelo");
		panelButtons.add(btnCargarModelo);
		
		JButton btnCargarFichero = new JButton("Cargar fichero");
		btnCargarFichero.setEnabled(false);
		panelButtons.add(btnCargarFichero);
				
				
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
		
		//TABLA
		resultsTable = new CustomJTable();
		infoTable = new CustomJTable();		
		scrollPane = new JScrollPane(resultsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
		contentPane.add(scrollPane, BorderLayout.CENTER);
				
		//STATISTICS
		
		
		JPanel precisionAtKpanel = new JPanel();
		contentPane.add(precisionAtKpanel, BorderLayout.SOUTH);
		precisionAtKpanel.setLayout(new BoxLayout(precisionAtKpanel, BoxLayout.X_AXIS));
				
		JPanel panelStatistics = new JPanel();
		panelStatistics.setLayout(new GridLayout(2, 2, 1, 0));
		
		JLabel meanRRlabel = new JLabel("Mean Reciprocal Rank (MRR) :");
		panelStatistics.add(meanRRlabel);
		
		JLabel pressionAtKlabel = new JLabel("P@K:");
		panelStatistics.add(pressionAtKlabel);
		
		meanRRvalue = new JLabel("00.00");
		panelStatistics.add(meanRRvalue);
				
		precisionAtKvalue = new JLabel("00.00");
		panelStatistics.add(precisionAtKvalue);		
		
		precisionAtKpanel.add(panelStatistics);
				
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
  	  	        	precisionAtKvalue.setText(pAtK[k-1]);
  	  	        }	
  	    	}  	             
  	      }
  	    });
        
        precisionAtKpanel.add(slider);

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
		
		slider.setEnabled(false);
		contentPane.remove(scrollPane);
		//TABLA
		resultsTable = new CustomJTable();
		infoTable = new CustomJTable();		
		scrollPane = new JScrollPane(resultsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);			
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		//LOAD DATA
		Instances labeled = null;
		totalReciprocalRank = 0;
		try {
			labeled = loadInstances(testPath);
			labeled.setClass(labeled.attribute("gs_text34"));
		} catch (Exception e) {
			e.printStackTrace();		
			JOptionPane.showMessageDialog(null, "Error al cargar el test",  "ERROR", JOptionPane.ERROR_MESSAGE);
			System.out.println("Error al cargar el test");
			tablaCargada = false;
			
		}
				
		//SLIDER
		if (k > labeled.numClasses()) k = labeled.numClasses();				
		slider.setMaximum(labeled.numClasses());
		slider.setValue(k);
		slider.setPaintTicks(true);
        slider.setPaintLabels(true);		        
   
        //TABLE HEADERS
        infoModel = new DefaultTableModel(0,0);        
   		infoModel.addColumn("Module");
   		infoModel.addColumn("Age");
   		infoModel.addColumn("Real");
   		infoModel.addColumn("Rank");
   		infoModel.addColumn("RR");
   		infoTable.setModel(infoModel);
   		
   		resultsModel = new DefaultTableModel(0,0);   		
        int maxWidth = 0;
        int classValueWidth;
	    for (int column = 1; column <= labeled.numClasses(); column++) {
	    	resultsModel.addColumn(column + "º pocisión");
	    	classValueWidth = labeled.classAttribute().value(column-1).length()*7;
	    	if (classValueWidth > maxWidth) maxWidth = classValueWidth; 
	    }	       	    
	    resultsTable.setModel(resultsModel);
		    
	    infoTable.getColumnModel().getColumn(0).setPreferredWidth(80);
	    infoTable.getColumnModel().getColumn(1).setPreferredWidth(40);
	    infoTable.getColumnModel().getColumn(2).setPreferredWidth(maxWidth);
	    infoTable.getColumnModel().getColumn(3).setPreferredWidth(50);
	    infoTable.getColumnModel().getColumn(4).setPreferredWidth(60);
	
	    infoTable.setPreferredScrollableViewportSize(infoTable.getPreferredSize());
	    scrollPane.setRowHeaderView(infoTable);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, infoTable.getTableHeader());
		pK = new Integer[labeled.numClasses()];
		for (int p = 0; p < pK.length; p++) {
			pK[p] = 0;
		}
		double[] classes;
		int i = 0;
		for(Instance ins : labeled) {
			try {
				classes = cls.distributionForInstance(ins);				
				instanceList = new Vector<Pairs>();
				for (int j = 0; j < classes.length; j++) {
					instanceList.add(new Pairs(classes[j],j,labeled.classAttribute().value(j)));
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Error al obtener las clases");
			}
		
			//ORDENAR LAS INSTANCIAS
			Collections.sort(instanceList, new Comparator<Pairs>() {
			    @Override
			    public int compare(Pairs p1, Pairs p2) {
			        return p2.getPercentage().compareTo(p1.getPercentage());
			    }
			});			
									
			Vector<Object> resultsVector = new Vector<Object>();
			Vector<Object> infoVector = new Vector<Object>();
			
			infoVector.addElement(ins.toString(labeled.attribute("module")));
			infoVector.addElement(ins.toString(labeled.attribute("age_years")));			
			infoVector.addElement(labeled.classAttribute().value((int) ins.classValue()));
			
			double rank = 0;			
			for (int l = 0; l < instanceList.size(); l++ ) {
				if (ins.classValue() == instanceList.get(l).getClassPredictedValue()) {
					rank = l+1;
					pK[l]+= 1;
				}
				resultsVector.addElement(instanceList.get(l).getResult());
			}			
			
			infoVector.add(rank);	
			totalReciprocalRank += 1.0/rank;
			infoVector.add(sf.format(1.0/rank));
			
						
			resultsModel.addRow(resultsVector);
			infoModel.addRow(infoVector);
			
			i++;
		}
		
		pAtK = new String[labeled.numClasses()];
		pAtK[0] =  String.valueOf((pK[0]*1.0/labeled.numInstances()));
		for (int p = 1; p < labeled.numClasses(); p++) {	
			pK[p] = pK[p-1] + pK[p];
			pAtK[p] =  String.valueOf((pK[p]*1.0/labeled.numInstances()));
		}
		
		resultsColumnModel = resultsTable.getColumnModel();
				
		allColumns = new HashMap<Integer,TableColumn>();
		for (int x = 0; x < resultsColumnModel.getColumnCount() ; x++)
		{
			resultsColumnModel.getColumn(x).setPreferredWidth(maxWidth + 55);
			allColumns.put(x, resultsColumnModel.getColumn(x));
		}
		
		tablaCargada = true;
		slider.setEnabled(true);
		meanRRvalue.setText(String.valueOf(totalReciprocalRank/i));
				
		updatePercentageAtK(k, labeled.numClasses());
		precisionAtKvalue.setText(pAtK[k-1]);
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
		        TableColumn column = resultsColumnModel.getColumn(pValue);
	    	    resultsTable.removeColumn(column);
			}
			
		}
	
		
	}
	
	


	
}
