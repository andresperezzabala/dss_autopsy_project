package estimaciones;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

import static utils.Utils.*;

/**
 * Se encarga de estimar la calidad del modelo.
 */
public class EstimacionCalidadNoHonestaMultilayerPerceptron {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta del train_bow_as.arff.\n2) Ruta del txt con la evaluacion a guardar.\n");
            System.exit(1);
        }

        String trainPath = args[0];
        String resultsPath = args[1];

        Instances train = null;
        try {
            train = loadInstances(trainPath);
            train.setClass(train.attribute("gs_text34"));
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + trainPath);
            e.printStackTrace();
            System.exit(1);
        }

        MultilayerPerceptron cls = new MultilayerPerceptron();
        try {
            cls.setLearningRate(0.12);
            cls.setMomentum(0.6);
            cls.setTrainingTime(1000);
            cls.buildClassifier(train);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al entrenar el multilayer perceptron.");
            e.printStackTrace();
            System.exit(1);
        }

        Evaluation eval = null;
        try {
            eval = new Evaluation(train);
            eval.evaluateModel(cls, train);
        } catch (Exception e) {
            System.out.println("Error al evaluar");
            e.printStackTrace();
            System.exit(1);
        }

        String results = null;
        try {
            results = getEvaluationResults(eval);
        } catch (Exception e) {
            System.out.println("Error al intentar sacar resultados de la evaluacion");
            e.printStackTrace();
            System.exit(1);
        }

        printToFile(results, resultsPath);
    }
}
