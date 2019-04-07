package textmining;

import static utils.Utils.*;

import java.io.PrintWriter;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class ParamOptimizationHoldOut {

	public static void main(String[] args) throws Exception {
		if(args.length != 5) {
			System.out.println("El programa necesita 2 argumentos:\\n1) Ruta del conjunto de entrenamiento train.arff\n2)Ruta del dev.arff \n3)LearningRate Begin \n4) LearningRate End\n5)Ruta del resultado , resultadoParametrosOptimos.txt");
			System.exit(1);
		}
		
		String bowTrainASPath = args[0];
		String bowDevASPath = args[1];
		double lrB = Double.parseDouble(args[2]);
		double lrE = Double.parseDouble(args[3]);
		String resultPath = args[4];
		
		Instances train = null;
		try {
			train=loadInstances(bowTrainASPath);
			train.setClass(train.attribute("gs_text34"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al cargar el conjunto train");
			System.exit(1);
		}
		Instances dev = null;
		try {
			dev=loadInstances(bowDevASPath);
			dev.setClass(dev.attribute("gs_text34"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al cargar el conjunto dev");
			System.exit(1);
		}
		
  	
		MultilayerPerceptron cls = new MultilayerPerceptron();
        
		Evaluation eval = null;
        
		StringBuilder result = new StringBuilder();
		
		
		double fMeasure;
		double optLR=0;
        double optM=0;
        double optWeightFM=0;
        for(double l=lrB;l<=lrE;l+=0.01){
        	for(double m=0.5;m<=0.8;m+=0.1){
        		System.out.println("Iteracion "+l+", "+m);
        		result.append("Iteracion "+l+", "+m);
             	cls=new MultilayerPerceptron();
        		cls.setTrainingTime(1000);
        		cls.setLearningRate(l);
        		cls.setMomentum(m);
        		try {
					//eval = new Evaluation(train);
					//eval.crossValidateModel(cls, train, 10, new Random(1));
        			eval=holdOutEval(cls, train, dev);
        		} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error al evaluar");
				}
        		fMeasure=eval.weightedFMeasure();
        		result.append(" --> "+optWeightFM+" vs "+ fMeasure + "\n");
        		result.append(eval.toSummaryString());
        		result.append(eval.toClassDetailsString());
        		System.out.println(fMeasure);
        		System.out.println(optWeightFM);
        		if(fMeasure>optWeightFM) {
        			optWeightFM=fMeasure;
        			optLR=l;
        	        optM= m;
        	        result.append("----------------------\nf optima: " + optWeightFM + "\nlr optima: " + optLR + "\nmomento optimo: "+ optM+ "\n----------------------\n");
        	        System.out.println("f optima "+optWeightFM);
        	        System.out.println("lr optima "+optLR);
        	        System.out.println("momento optimo "+optM);
        		}
        	}
        }
        result.append("--------------------RESULTADO--------------------\nf-measure optima es: "+ optWeightFM + "\nratio de aprendizaje optimo es: "+optLR+"\nmomento optimo: "+ optM);

		try {
			PrintWriter writer=new PrintWriter(resultPath);
			writer.print(result);
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
}
