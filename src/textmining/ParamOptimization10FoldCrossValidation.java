package textmining;

import static utils.Utils.*;

import java.io.PrintWriter;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class ParamOptimization10FoldCrossValidation {

	public static void main(String[] args) {
		if(args.length != 2) {
			System.out.println("El programa necesita 2 argumentos:\\n1) Ruta del conjunto de entrenamiento train_BOW_AS.arff\\n2)Ruta del resultado , resultadoParametrosOptimos.txt");
			System.exit(1);
		}
		
		String bowASPath = args[0];
		String resultPath = args[1];
		
		Instances train = null;
		try {
			train=loadInstances(bowASPath);
			train.setClass(train.attribute("gs_text34"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al cargar el conjunto train");
			System.exit(1);
		}
		
		int minorityCIndex = getMinoritaryNominalClassIndex(train);
    	
		MultilayerPerceptron cls = new MultilayerPerceptron();
        
		Evaluation eval = null;
        
		StringBuilder result = new StringBuilder();
		
		train.randomize(new Random(42));
		double fMeasure;
		double optLR=0;
        double optM=0;
        double optF=0;
        for(double l=0.1;l<=1;l+=0.1){
        	for(double m=0.5;m<=1;m+=0.1){
        		System.out.println("Iteracion "+l+", "+m);
        		result.append("Iteracion "+l+", "+m);
             	cls=new MultilayerPerceptron();
        		cls.setTrainingTime(100);
        		cls.setLearningRate(l);
        		cls.setMomentum(m);
        		try {
					eval = new Evaluation(train);
					eval.crossValidateModel(cls, train, 10, new Random(1));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error al evaluar");
				}
        		fMeasure=eval.fMeasure(minorityCIndex);
        		result.append(" --> "+optF+" vs "+ fMeasure + "\n");
        		System.out.println(fMeasure);
        		System.out.println(optF);
        		if(fMeasure>optF) {
        			optF=fMeasure;
        			optLR=l;
        	        optM= m;
        	        result.append("----------------------\nf optima: " + optF + "\nlr optima: " + optLR + "\nmomento optimo: "+ optM+ "\n----------------------\n");
        	        System.out.println("f optima "+optF);
        	        System.out.println("lr optima "+optLR);
        	        System.out.println("momento optimo "+optM);
        		}
        	}
        }
        result.append("--------------------RESULTADO--------------------\nf-measure optima es: "+ optF + "\nratio de aprendizaje optimo es: "+optLR+"\nmomento optimo: "+ optM);

		try {
			PrintWriter writer=new PrintWriter(args[2]);
			writer.print(result);
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
}
