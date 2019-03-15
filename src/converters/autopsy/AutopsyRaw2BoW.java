package converters.autopsy;

import weka.core.Instances;
import static utils.Utils.*;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;

/**
 * Se encarga de convertir un ARFF RAW en un ARFF en formato Bag of Words
 */
public class AutopsyRaw2BoW {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta del RAW.arff.\n2) Ruta del BoW.arff.\n");
            System.exit(1);
        }
        String rawPath = args[0];
        String outputBoWPath = args[1];

        // Cargar archivo RAW
        Instances instances = null;
        try {
            instances = loadInstances(rawPath);
            instances.setClass(instances.attribute("gs_text34"));
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo raw: " + rawPath);
            e.printStackTrace();
            System.exit(1);
        }

        // Convertir RAW a BoW
        Instances instancesBoW = null;
        try {
            instancesBoW = filterWithBoW(instances); // Si en lugar de BoW hubieramos querido usar TFIDF, usariamos:
                                                     // filterWithTFIDF(instances)
        } catch (Exception e) {
            System.out.println("Error al convertir el archivo raw a BoW: " + rawPath);
            e.printStackTrace();
            System.exit(1);
        }
        saveInstances(instancesBoW, outputBoWPath);
    }
}
