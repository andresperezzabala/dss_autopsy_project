package converters.autopsy;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

import static utils.Utils.*;

public class AutopsyBoW2BoWAttributeSelection {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta del BoW.arff.\n2) Ruta del BoW_attribute_selection.arff.\n");
            System.exit(1);
        }
        String bowPath = args[0];
        String outputAttrSelecion = args[1];

        // Cargar archivo BOW
        Instances instances = null;
        try {
            instances = loadInstances(bowPath);
            instances.setClass(instances.attribute("gs_text34"));
            System.out.println("El numero de atributos inicial es: " + instances.numAttributes());
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + bowPath);
            e.printStackTrace();
            System.exit(1);
        }

        // Hacer la seleccion de atributos sobre el archivo BoW
        Instances instancesAttrSelection = null;
        try {
            instancesAttrSelection = filterAttributes(instances);
            System.out.println("El numero de atributos despues de realizar la seleccion de atributos es: " + instancesAttrSelection.numAttributes());
        } catch (Exception e) {
            System.out.println("Error al realizar la seleccion de atributos");
            e.printStackTrace();
            System.exit(1);
        }
        saveInstances(instancesAttrSelection, outputAttrSelecion);

    }
}
