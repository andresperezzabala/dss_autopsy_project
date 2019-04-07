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
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta del train_BOW.arff.\n2) Ruta del test_BOW.arff.\n3) Ruta del archivo train_BOW_ASS.arff\n4) Ruta del archivo test_BOW_ASS.arff");
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
        	trainBow.deleteAttributeAt(0);
        	int[] attributes=filterAttributesRanked(trainBow, 83);
			filter.setAttributeIndicesArray(attributes);
			filter.setInvertSelection(true);
			filter.setInputFormat(trainBow);
			Instances trainBowAs=Filter.useFilter(trainBow, filter);
			saveInstances(trainBowAs, trainBowAsPath);
		} catch (Exception e) {
			System.out.println("Error al generar filtro");
			e.printStackTrace();
			System.exit(1);
		}
        
        try {
        	testBow.deleteAttributeAt(0);
        	Instances testBowAs = Filter.useFilter(testBow, filter);
        	saveInstances(testBowAs, testBowAsPath);
		} catch (Exception e) {
			System.out.println("Error al apicar el filtro " + testBowPath);
			e.printStackTrace();
			System.exit(1);
		}

	}

}
