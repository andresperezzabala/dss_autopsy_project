package converters.autopsy;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.core.Attribute;
import weka.core.converters.DictionarySaver;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;

import static utils.Utils.*;

public class AutopsyTrainModel {

    public static void main(String[] args) throws Exception {
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


        AttributeSelection filter = new AttributeSelection();
        filter.setEvaluator(new CfsSubsetEval()); // Correlation-based feature selection
        filter.setSearch(new BestFirst());
        filter.setInputFormat(train);
        Instances train_as = Filter.useFilter(train, filter);

        // Creo que el filtro habria que guardarlo en un archivo para poder usarlo cuando se quiera
        Instances test_as = Filter.useFilter(test, filter);

        System.out.println(train_as.numAttributes());
        System.out.println(test_as.numAttributes());

        NaiveBayes cls = new NaiveBayes();
        Evaluation eval = null;
        try {
            eval = holdOutEval(cls, train_as, test_as);
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
