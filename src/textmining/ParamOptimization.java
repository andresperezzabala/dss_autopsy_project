package textmining;

import static utils.Utils.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import jdk.internal.org.objectweb.asm.tree.MultiANewArrayInsnNode;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class ParamOptimization {

	public static void main(String[] args) {
		if(args.length<3) {
			System.out.println("El programa necesita 3 argumentos:\\n1) Ruta del conjunto de entrenamiento train_BOW_AS.arff\\n2)Ruta del test_BOW_AS.model\\n3)ruta para el CalidadEsperada.txt");
			System.exit(1);
		}
		
		Instances train=null;
		try {
			train=loadInstances(args[0]);
			train.setClass(train.attribute("gs_text34"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al cargar el conjunto train");
		}
		
		Instances dev=null;
		try {
			dev=loadInstances(args[1]);
			dev.setClass(dev.attribute("gs_text34"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al cargar el conjunto test");
		}
		
		double[] optParams=manualSearchBestParamsRN(train, dev);
		
		MultilayerPerceptron cls=new MultilayerPerceptron();
		cls.setLearningRate(optParams[0]);
		cls.setMomentum(optParams[1]);
		Evaluation kFold=null;
		try {
			kFold=crossvalidation(cls, train, 10);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error al hacer la evaluacion 10kfoldcrossvalidation");
		}

		cls=new MultilayerPerceptron();
		cls.setLearningRate(optParams[0]);
		cls.setMomentum(optParams[1]);
		Evaluation noH=null;
		try {
			noH=noHonestEval(cls, train);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error al hacer la evaluacion no honesta");
		}
		try {
			PrintWriter writer=new PrintWriter(args[2]);
			writer.write(kFold.toClassDetailsString()+"\n"+kFold.toMatrixString());
			writer.write(noH.toClassDetailsString()+"\n"+noH.toMatrixString());
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
