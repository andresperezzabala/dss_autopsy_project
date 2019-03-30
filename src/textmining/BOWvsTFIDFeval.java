package textmining;

import static utils.Utils.*;

import java.io.PrintWriter;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

public class BOWvsTFIDFeval {

	public static void main(String[] args) throws Exception {
		
		if (args.length != 3) {
			System.out.println("El programa necesita 3 argumentos:\n1) BOW.arff\n2) TFIDF.arff 3) result.txt");
		}
		
		String bowASPath = args[0];
		String tfidfASPath = args[1];
		String evalPath = args[2];
		
		
		Instances bowAS = null;
		Instances tfidfAS = null;
		
		bowAS = loadInstances(bowASPath);
		bowAS.setClass(bowAS.attribute("gs_text34"));
		tfidfAS = loadInstances(tfidfASPath);
		tfidfAS.setClass(tfidfAS.attribute("gs_text34"));
		Classifier NB = new NaiveBayes();
		
		Evaluation bowEval = null;
		Evaluation tfidfEval = null;
		Random random = null;
		StringBuilder result = new StringBuilder();
	
		for(int i = 1; i <= 10; i++) {
			bowEval = new Evaluation(bowAS);
			tfidfEval = new Evaluation(tfidfAS);
			random = new Random(i*10);
			bowEval.crossValidateModel(NB, bowAS, 10, random);
			System.out.println(i + " BOW");
			result.append("Naive Bayes BOW AS" + i*10 + "\n");
			result.append(bowEval.toSummaryString());
			result.append(bowEval.toClassDetailsString());
			tfidfEval.crossValidateModel(NB, tfidfAS, 10, random);
			System.out.println(i + " TFIDF");
			result.append("Naive Bayes TFIDF AS" + i*10 + "\n");
			result.append(tfidfEval.toSummaryString());
			result.append(tfidfEval.toClassDetailsString());
		}
		
		bowEval = new Evaluation(bowAS);
		tfidfEval = new Evaluation(tfidfAS);

		bowEval.crossValidateModel(NB, bowAS, 10, random);
		System.out.println(" BOW");
		result.append("Naive Bayes BOW AS ultimo" + "\n");
		result.append(bowEval.toSummaryString());
		result.append(bowEval.toClassDetailsString());
		tfidfEval.crossValidateModel(NB, tfidfAS, 10, random);
		System.out.println(" TFIDF");
		result.append("Naive Bayes BOW AS ultimo" +"\n");
		result.append(tfidfEval.toSummaryString());
		result.append(tfidfEval.toClassDetailsString());
		
		System.out.println(result.toString());
		
		PrintWriter w = new PrintWriter(evalPath);
		w.print(result.toString());
		w.close();
	
		
	}
	
}
