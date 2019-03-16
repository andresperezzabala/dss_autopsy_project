package converters.autopsy;

import weka.core.Instances;
import static utils.Utils.*;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;

/**
 * Se encarga de convertir un ARFF RAW en un ARFF en formato Bag of Words. Guardara un diccionario o cargara uno para
 * usar como modelo.
 *
 * Necesita 4 argumentos:
 *  Ruta del archivo raw | ruta del bow a crear | opcion de guardar o cargar el diccionario -S o -L | ruta del diccionario
 *
 *  Ejemplo de guardar diccionario:
 *  java -jar AutopsyRaw2Bow.jar data.arff data_bow.arff -S dictionary_to_save
 *
 *  Ejemplo de cargar diccionario:
 *  java -jar AutopsyRaw2Bow.jar data.arff data_bow.arff -L dictionary_to_load
 */
public class AutopsyRaw2BoW {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("El programa necesita 4 argumentos:\n1) Ruta del RAW.arff.\n2) Ruta del BoW.arff.\n" +
                    "3) Opcion si guardar o cargar diccionario. Para guardar (save) el diccionario usar -S y para cargar (load) usar -L.\n" +
                    "4) Ruta del diccionario a cargar.\n\n" +
                    "Ejemplo:\n" +
                    "java -jar AutopsyRaw2Bow.jar data.arff data_bow.arff -S dictionary_to_save\n" +
                    "java -jar AutopsyRaw2Bow.jar data.arff data_bow.arff -L dictionary_to_load\n" +
                    "");
            System.exit(1);
        }

        // Leer argumentos de la consola.
        String rawPath = args[0];
        String outputBoWPath = args[1];
        String commandLineOption = args[2];

        DictionaryOption opt = null;
        if (commandLineOption.equals("-S")) {
            opt = DictionaryOption.SAVE;
        } else if (commandLineOption.equals("-L")) {
            opt = DictionaryOption.LOAD;
        } else {
            System.out.println("Error al elegir si cargar o guardar dicconario, los valores validos son '-S' para guardarlo o '-L' para cargarlo.");
            System.exit(1);
        }

        String dictionaryPath = args[3];

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
            instancesBoW = filterWithBoW(instances, new File(dictionaryPath), opt); // Si en lugar de BoW hubieramos querido usar TFIDF, usariamos:
                                                                                    // filterWithTFIDF(instances, file, opt)
        } catch (Exception e) {
            System.out.println("Error al convertir el archivo raw a BoW: " + rawPath);
            e.printStackTrace();
            System.exit(1);
        }
        saveInstances(instancesBoW, outputBoWPath);
    }
}
