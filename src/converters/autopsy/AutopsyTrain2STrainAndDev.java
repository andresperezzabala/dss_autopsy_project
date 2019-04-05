package converters.autopsy;

import static utils.Utils.getTest;
import static utils.Utils.getTrain;
import static utils.Utils.loadInstances;
import static utils.Utils.manualSearchBestParamsRN;
import static utils.Utils.saveInstances;
import java.util.Random;

import weka.core.Instances;

public class AutopsyTrain2STrainAndDev {

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
		    System.err.println("No se ha proporcionado el fichero de entreno. Ni las rutas del Split Train y Dev");
			System.exit(1);
		}
		
		String instances = args[0];
		String STrainPath = args[1];
		String devPath = args[2];
		Instances train=null;
		try {
			train=loadInstances(instances);
			train.setClass(train.attribute("gs_text34"));
			train.randomize(new Random(1));  // pongo random uno, para que a todos nos de el mismo resultado el randomize
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al cargar el conjunto train");
		}

		Instances train_split = getTrain(70.0, train);
		Instances dev = getTest(70.0, train);  // si, pone 70.0 tambien, el getTest te devuelve lo contrario a ese 70, es decir, el 30 porciento

		saveInstances(train_split, STrainPath);
		saveInstances(dev, devPath);

		System.exit(0);
	}
}
