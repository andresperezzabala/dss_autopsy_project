package converters.autopsy;

import static utils.Utils.*;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

/**
 * Clase para entrenar la red neuronal.
 */
public class AutopsyRedNeuronal {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("El programa necesita 3 argumentos:\n1) Ruta del train_bow_as.arff.\n2) Ruta del test_bow_as.arff.\n3) Ruta del resultado de la evaluacion eval.txt\n");
            System.exit(1);
        }

        String trainPath = args[0];
        String testPath = args[1];
        String evalPath = args[2];

        Instances train = null;
        try {
            train = loadInstances(trainPath);
            train.setClass(train.attribute("gs_text34"));
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + trainPath);
            e.printStackTrace();
            System.exit(1);
        }

        Instances test = null;
        try {
            test = loadInstances(testPath);
            test.setClass(test.attribute("gs_text34"));
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + test);
            e.printStackTrace();
            System.exit(1);
        }

        MultilayerPerceptron cls = new MultilayerPerceptron();
        Evaluation eval = null;
        try {
            eval = holdOutEval(cls, train, test);
        } catch (Exception e) {
            System.out.println("Error al entrenar la red neuronal.");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            String results = getEvaluationResults(eval);
            System.out.println(results);
            printToFile(results, evalPath);
        } catch (Exception e) {
            System.out.println("Error al mostrar los resultados.");
            e.printStackTrace();
            System.exit(1);
        }

    }

}
