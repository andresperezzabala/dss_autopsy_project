package converters.filmReviews;

import weka.core.Instances;

import static utils.Utils.*;

public class FilmReviewsARFFToBoW {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta arff.\n2) Salida arff-BoW.\n");
            System.exit(1);
        }

        String dataPath = args[0];
        String outputPath = args[1];

        Instances instances = null;
        try {
            instances = loadInstances(dataPath);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Imposible cargar las instancias del archivo");
            System.exit(1);
        }

        Instances instancesBoW = null;
        try {
            instancesBoW = filterWithBoW(instances, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Imposible mediante BoW");
            System.exit(1);
        }

        saveInstances(instancesBoW, outputPath);
    }

}
