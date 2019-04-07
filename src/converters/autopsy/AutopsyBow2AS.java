package converters.autopsy;


import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import static utils.Utils.*;

public class AutopsyBow2AS {

	public static void main(String[] args) {

		if (args.length != 3) {
            System.err.println("El programa necesita 3 argumentos:\n1) Ruta del train_BOW.arff.\n2) Ruta del train_BOW_as.arff.\n3) Ruta de los atributos a guardar");
            System.exit(1);
        }

        String trainBowPath = args[0];
        String trainBowAsPath = args[1];
        String attsPath = args[2];

        // Cargar archivo BOW
        Instances trainBow = null;
        try {
        	trainBow = loadInstances(trainBowPath);
        	trainBow.setClass(trainBow.attribute("gs_text34"));
            System.out.println("El numero de atributos inicial es: " + trainBow.numAttributes());
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + trainBowPath);
            e.printStackTrace();
            System.exit(1);
        }
        
        
        Remove filter = new Remove();
        try {
        	trainBow.deleteAttributeAt(0);
        	int[] attributes=filterAttributesRanked(trainBow, 83);
			filter.setAttributeIndicesArray(attributes);
			filter.setInvertSelection(true);
			filter.setInputFormat(trainBow);
			Instances trainBowAs=Filter.useFilter(trainBow, filter);
			saveInstances(trainBowAs, trainBowAsPath);
			printToFile(intArrayToString(attributes), attsPath);
		} catch (Exception e) {
			System.out.println("Error al generar filtro");
			e.printStackTrace();
			System.exit(1);
		}
        
	}

}
