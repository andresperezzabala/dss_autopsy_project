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

		if (args.length != 4) {
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta del train_AS_BoW.arff.\n2) Ruta del test_BOW.arff.\n3) Ruta del archi test_AS_BOW.arff\n");
            System.exit(1);
        }
        String trainBowPath = args[0];
        String testBowPath = args[1];
        String trainBowAsPath = args[2];
        String testBowAsPath = args[3];

        // Cargar archivo BOW
        Instances trainBow = null;
        Instances testBow = null;
        try {
        	trainBow = loadInstances(trainBowPath);
        	trainBow.setClass(trainBow.attribute("gs_text34"));
            System.out.println("El numero de atributos inicial es: " + trainBow.numAttributes());
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + trainBowPath);
            e.printStackTrace();
            System.exit(1);
        }
        
        
        try {
        	testBow = loadInstances(testBowPath);
        	testBow.setClass(testBow.attribute("gs_text34"));
            System.out.println("El numero de atributos inicial es: " + testBow.numAttributes());
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + testBowPath);
            e.printStackTrace();
            System.exit(1);
        }
        
        Remove filter = new Remove();
        try {
        	int[] attributes=filterAttributesRanked(trainBow, 100);
        	Instances trainBowAs=trainBow;
        	saveInstances(trainBowAs, trainBowAsPath);
			filter.setAttributeIndicesArray(attributes);
			filter.setInvertSelection(true);
			filter.setInputFormat(trainBowAs);
		} catch (Exception e) {
			System.out.println("Error al generar filtro");
			e.printStackTrace();
			System.exit(1);
		}
        
        try {
        	Instances testBowAs = Filter.useFilter(testBow, filter);
        	saveInstances(testBowAs, testBowAsPath);
		} catch (Exception e) {
			System.out.println("Error al apicar el filtro " + testBowPath);
			e.printStackTrace();
			System.exit(1);
		}

	}

}
