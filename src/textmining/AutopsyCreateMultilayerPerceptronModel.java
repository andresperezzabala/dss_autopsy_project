package textmining;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

import static utils.Utils.loadInstances;
import static utils.Utils.saveModel;

public class AutopsyCreateMultilayerPerceptronModel {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta del train_bow_as.arff.\n2) Ruta del modelo a guardar.\n");
            System.exit(1);
        }

        String trainPath = args[0];
        String modelPath = args[1];

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

        try {
            saveModel(cls, modelPath);
        } catch (Exception e) {
            System.out.println("Error al intentar guardar el modelo en " + modelPath);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
