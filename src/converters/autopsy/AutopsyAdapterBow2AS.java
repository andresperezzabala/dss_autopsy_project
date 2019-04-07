package converters.autopsy;


import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import static utils.Utils.*;

public class AutopsyAdapterBow2AS {

	public static void main(String[] args) {

		if (args.length != 3) {
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta del archivo_BOW.arff.\n2) Ruta del archivo_BOW_as.arff.\n3) Ruta del archivo de atributos.txt");
            System.exit(1);
        }

        String bowPath = args[0];
        String bowAsPath = args[1];
        String attributesPath = args[2];

        // Cargar archivo BOW
        Instances bow = null;
        try {
        	bow = loadInstances(bowPath);
        	bow.setClass(bow.attribute("gs_text34"));
            System.out.println("El numero de atributos inicial es: " + bow.numAttributes());
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + bowPath);
            e.printStackTrace();
            System.exit(1);
        }
        
        Remove filter = new Remove();
        try {
        	bow.deleteAttributeAt(0);
			String attributeArray = readAllBytesJava7(attributesPath);
			int[] attributes = stringIntArrayToIntArray(attributeArray);
			filter.setAttributeIndicesArray(attributes);
			filter.setInvertSelection(true);
			filter.setInputFormat(bow);
			Instances bowAs=Filter.useFilter(bow, filter);
			saveInstances(bowAs, bowAsPath);
		} catch (Exception e) {
			System.out.println("Error al generar filtro");
			e.printStackTrace();
			System.exit(1);
		}
        
	}

}
