package textmining;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import static utils.Utils.*;

public class UseModelToEvaluateTest {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("El programa necesita 3 argumentos:\n1) Ruta del modelo\n2)Ruta del test_bow_as.arff \n3)Ruta donde guardar las predicciones");
            System.exit(1);
        }

        String modelPath = args[0];
        String testPath = args[1];
        String resultsPath = args[2];

        Classifier cls = null;
        try {
            cls = loadModel(modelPath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al cargar el modelo");
            System.exit(1);
        }

        Instances test = null;
        try {
            test = loadInstances(testPath);
            test.setClass(test.attribute("gs_text34"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al cargar el test");
            System.exit(1);
        }

        Evaluation eval = null;
        try {
            eval = new Evaluation(test);
            eval.evaluateModel(cls, test);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al evaluar el test");
            System.exit(1);
        }

        String results = null;
        try {
            results = getEvaluationResults(eval);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al coger la info de la evaluacion");
            System.exit(1);
        }

        printToFile(results, resultsPath);
    }
}
