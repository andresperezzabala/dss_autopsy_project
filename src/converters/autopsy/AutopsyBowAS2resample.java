package converters.autopsy;

import static utils.Utils.loadInstances;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;

public class AutopsyBowAS2resample {
	public static void main(String[] args) {

		if (args.length != 2) {
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta del TRAIN_FSS.arff.\n2) Ruta del TRAIN_FSS_RESAMPLED.arff");
            System.exit(1);
        }
		
        String trainFssPath = args[0];
        String trainFssResampledPath = args[1];
        
     // Cargar archivo
        Instances trainFss = null;
        try {
        	trainFss = loadInstances(trainFssPath);
        	trainFss.setClass(trainFss.attribute("gs_text34"));
  
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + trainFssPath);
            e.printStackTrace();
            System.exit(1);
        }
        
        Resample rs = new Resample();
		try {
			rs.setInputFormat(trainFss);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error al remuestrar el conjunto de datos");
			e.printStackTrace();
			System.exit(1);
		}
		
		rs.setBiasToUniformClass(1.0);
		
		try {
			Instances trainFssResample = Filter.useFilter(trainFss, rs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error al  aplicar el remuestreo");
			e.printStackTrace();
			System.exit(1);
		}
		
		
		
	}
}
