package converters.autopsy;

import static utils.Utils.*;

import java.util.Random;

import weka.core.Instances;

public class AutopsyParamsMP {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
		    System.err.println("No se ha proporcionado el fichero de entreno.");
			System.exit(1);
		}
		
		Instances train=null;
		try {
			train=loadInstances(args[0]);
			train.setClass(train.attribute("gs_text34"));
			train.randomize(new Random(1));  // pongo random uno, para que a todos nos de el mismo resultado el randomize
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al cargar el conjunto train");
		}

		Instances train_split = getTrain(70.0, train);
		Instances dev = getTest(70.0, train);  // si, pone 70.0 tambien, el getTest te devuelve lo contrario a ese 70, es decir, el 30 porciento

		double[] optParams=manualSearchBestParamsRN(train_split, dev); // no hago un print porque ya lo hace el metodo

		System.exit(0);
	}
}
