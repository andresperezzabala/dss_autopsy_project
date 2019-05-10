package textmining;

import static utils.Utils.loadInstances;
import static utils.Utils.loadModel;

import java.io.PrintWriter;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.core.Instances;

public class RankingSAD {

	public static void main(String[] args) throws Exception {
		if(args.length != 3) {
			System.out.println("El programa necesita 3 argumentos:\n1)Ruta del modelo\n2)Ruta del test.arff \n3)Ruta donde guardar las predicciones");
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
		
		StringBuilder result = new StringBuilder();
		ArrayList<double[]> predictions = new ArrayList<double[]>();
		double[] classes;
		double clsvalue;
		for(int i=0;i<labeled.numInstances();i++) {
			clsvalue = cls.classifyInstance(labeled.instance(i));
			result.append("\nPrediccion: "+ + clsvalue + ", " + labeled.classAttribute().value((int) clsvalue) + " ");
			result.append("Real: "+labeled.classAttribute().value((int)labeled.get(i).classValue())+ " Lista: ");
			classes = cls.distributionForInstance(labeled.instance(i));		
			for (int j = 0; j < classes.length ; j ++) {
				result.append(j + " " + classes[j] + " | ");
			}
			predictions.add(classes);			
		}
		
		PrintWriter writer = new PrintWriter(args[2]);
		writer.print(result);
		writer.close();
	}
	
}
