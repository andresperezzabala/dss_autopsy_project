package converters.filmReviews;

import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;

import java.io.File;
import java.io.IOException;

import static utils.Utils.saveInstances;

public class FilmReviewsMain {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta de la carpeta para datos.\n2) Ruta arff.\n");
            System.exit(1);
        }

        // Cargar atributos
        String folderPath = args[0];
        String outputPath = args[1];

        // Preparar la clase que carga los directorios
        TextDirectoryLoader directoryLoader = new TextDirectoryLoader();
        try {
            directoryLoader.setDirectory(new File(folderPath));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se ha podido cargar el directorio indicado.");
            System.exit(1);
        }

        // Intentar cargar las instancias
        Instances data = null;
        try {
            data = directoryLoader.getDataSet();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se ha podido convertir a instancias.");
            System.exit(1);
        }

        // Guardar las instancias.
        saveInstances(data, outputPath);
    }

}
