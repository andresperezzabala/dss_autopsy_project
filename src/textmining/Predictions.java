package textmining;

import static utils.Utils.*;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;

public class Predictions {

	public static void main(String[] args) {
		if(args.length != 3) {
			System.out.println("El programa necesita 3 argumentos:\n1) Ruta del modelo\n2)Ruta del test.arff \n3)Ruta donde guardar las predicciones");
			System.exit(1);
		}
		Classifier cls = null;
		try {
			cls = loadModel(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al cargar el modelo");
		}
		Instances labeled = null;
		try {
			labeled = loadInstances(args[1]);
			labeled.setClass(labeled.attribute("gs_text34"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al cargar el test");
		}
		//pone a missing value la clase de unlabeled
		Instances unlabeled = unlabel(labeled,"gs_text34");
		predict(cls, unlabeled, args[2]);
		
	}

}
