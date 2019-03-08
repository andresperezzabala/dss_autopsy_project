package converters.autopsy;

import static utils.Utils.saveInstances;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class AutopsyMain {

	public static void main(String[] args) {
		if (args.length != 2) {
            System.err.println("El programa necesita 2 argumentos:\n1) Ruta de la carpeta para datos.\n2) Ruta arff.\n");
            System.exit(1);
        }
        String csvPath = args[0];
        String outputArffPath = args[1];
        CSVLoader loader =new CSVLoader();
        try {
			loader.setSource(new File(csvPath));
		} catch (IOException e) {
			System.out.println("error al cargar el archivo csv");
			e.printStackTrace();
			System.exit(1);
		}
        Instances data=null;
        try {
			data=loader.getDataSet();
		} catch (IOException e) {
			System.out.println("error al convertir en arff ");
			e.printStackTrace();
			System.exit(1);
		}
        saveInstances(data,outputArffPath);

	}

}
