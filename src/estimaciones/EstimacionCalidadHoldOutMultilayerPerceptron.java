package textmining;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

import java.util.Random;

import static utils.Utils.*;

public class EstimacionCalidadHoldOutMultilayerPerceptron {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta del train_bow_as.arff.\n2) Ruta del txt con la evaluacion a guardar.\n");
            System.exit(1);
        }

        String trainPath = args[0];
        String resultsPath = args[1];

        Instances instances = null;
        try {
            instances = loadInstances(trainPath);
            instances.setClass(instances.attribute("gs_text34"));
            instances.randomize(new Random(1));
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + trainPath);
            e.printStackTrace();
            System.exit(1);
        }

        Instances train = null;
        Instances test = null;
        try {
            train = getTrain(70.0, instances);
            test = getTest(70.0, instances);
            train.setClass(train.attribute("gs_text34"));
            test.setClass(test.attribute("gs_text34"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al hacer la particion hold out.");
            System.exit(1);
        }

        MultilayerPerceptron cls = new MultilayerPerceptron();
        Evaluation eval = null;
        try {
            cls.setLearningRate(0.12);
            cls.setMomentum(0.6);
            cls.setTrainingTime(1000);
            eval = holdOutEval(cls, train, test);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al hacer hold out.");
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
