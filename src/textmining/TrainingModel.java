package textmining;

import static utils.Utils.loadInstances;
import static utils.Utils.saveModel;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

public class TrainingModel {

	public static void main(String[] args) {
		if (args.length != 2) {
    		System.err.println("El programa necesita 2 argumentos:\n1) Ruta del conjunto de entrenamiento train_RAW.arff\n2)Ruta del modelo.model");
			System.exit(1);
    	}
    	
	  	String arrfPath = args[0];
        String outputModelPath = args[1];
        
        Instances instances = null;
        
        try {
			instances = loadInstances(arrfPath);
		    instances.setClass(instances.attribute("gs_text34"));
		} catch (Exception e) {
			System.out.println("Error al cargar el archivo raw: " + arrfPath);
		    e.printStackTrace();
		    System.exit(1);
		}
        
        Classifier NB = new NaiveBayes();
        try {
			NB.buildClassifier(instances);
		} catch (Exception e) {
			System.out.println("Error al generar el modelo");
			e.printStackTrace();
			System.exit(1);
		}
		
        try {
			saveModel(NB, outputModelPath);
		} catch (Exception e) {
			System.out.println("Error al guardar el modelo");
			e.printStackTrace();
			System.exit(1);
		}	
	}

}
