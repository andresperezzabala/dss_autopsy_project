package converters.autopsy;


import weka.filters.Filter;
import static utils.Utils.*;

import java.io.File;

import weka.attributeSelection.AttributeSelection;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.unsupervised.attribute.Remove;

public class AutopsyAdaptBow2AS {

	public static void main(String[] args) {

		if (args.length != 3) {
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta del train_AS_BoW.arff.\n2) Ruta del test_BOW.arff.\n3) Ruta del archi test_AS_BOW.arff\n");
            System.exit(1);
        }
        String bowASPath = args[0];
        String bowPath = args[1];
        String outputPath = args[2];

        // Cargar archivo BOW
        Instances instances = null;
        Instances test = null;
        try {
            instances = loadInstances(bowASPath);
            instances.setClass(instances.attribute("gs_text34"));
            System.out.println("El numero de atributos inicial es: " + instances.numAttributes());
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + bowASPath);
            e.printStackTrace();
            System.exit(1);
        }
        
        
        try {
        	test = loadInstances(bowPath);
        	test.setClass(test.attribute("gs_text34"));
            System.out.println("El numero de atributos inicial es: " + test.numAttributes());
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + bowPath);
            e.printStackTrace();
            System.exit(1);
        }
        
        Remove filter = new Remove();
        try {
			filter.setInputFormat(instances);
			filter.setInvertSelection(true);
		} catch (Exception e) {
			System.out.println("Error al poner el formato. " + bowASPath);
			e.printStackTrace();
			System.exit(1);
		}
        
        try {
        	Instances output = Filter.useFilter(test, filter);
        	saveInstances(output, outputPath);
		} catch (Exception e) {
			System.out.println("Error al apicar el filtro " + bowPath);
			e.printStackTrace();
			System.exit(1);
		}

	}

}
