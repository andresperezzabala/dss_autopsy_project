package converters.autopsy;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

import static utils.Utils.*;

public class AutopsyTrainModel {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("El programa necesita 2 argumentos:\n1) train.arff.\n2) test.arff.\n");
            System.exit(1);
        }

        // Recoger argumentos de la terminal
        String trainPath = args[0];
        String testPath = args[1];

        // Cargar las instancias
        Instances train = null;
        Instances test = null;
        try {
            train = loadInstances(trainPath);
            train.setClass(train.attribute("gs_text34"));

            test = loadInstances(testPath);
            test.setClass(test.attribute("gs_text34"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar las instancias.");
            System.exit(1);
        }

        // Hacer hold out
        NaiveBayes cls = new NaiveBayes();
        Evaluation eval = null;
        try {
            eval = holdOutEval(cls, train, test);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al intentar hacer hold out.");
            System.exit(1);
        }

        // Mostrar resultados del hold out.
        try {
            System.out.println(getEvaluationResults(eval));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al intentar mostrar los resultados de la evaluacion.");
            System.exit(1);
        }
    }
}
