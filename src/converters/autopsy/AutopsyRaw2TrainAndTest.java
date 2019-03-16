package converters.autopsy;

import static utils.Utils.loadInstances;
import static utils.Utils.generateHashSet;
import static utils.Utils.generatekey;
import static utils.Utils.saveInstances;
import java.util.HashSet;

import weka.core.Instance;
import weka.core.Instances;

public class AutopsyRaw2TrainAndTest {

	 public static void main(String[] args) {
		if (args.length != 4) {
			System.err.println("El programa necesita 4 argumentos:\n1) Ruta del Raw.arff.\n2) Ruta del test.arff a copiar \n3)ruta del train\n4)Ruta del test");
			System.exit(1);
		}
		 
		String rawPath = args[0];
		String test = args[1];
		
		Instances instances = null;
		Instances testIntances = null;
		
		//Obtener conjunto de datos 
		try {
			instances = loadInstances(rawPath);
		    instances.setClass(instances.attribute("gs_text34"));
		} catch (Exception e) {
			System.out.println("Error al cargar el archivo raw: " + rawPath);
		    e.printStackTrace();
		    System.exit(1);
		}
		
		//Obtener conjunto de datos test
		try {
			testIntances = loadInstances(test);
			testIntances.setClass(instances.attribute("gs_text34"));
		} catch (Exception e) {
			System.out.println("Error al cargar el archivo raw: " + test);
		    e.printStackTrace();
		    System.exit(1);
		}
		
		String keyAtts = "newid module";
		String[] keys = keyAtts.split(" ");
		//Generar HashMap el test
		HashSet<String> testHS = generateHashSet(testIntances, keys);
		
		//Crear el arff del test 		
		Instances newTest = new Instances(instances, 0);
		//Crear el arff del train
		Instances newTrain = new Instances(instances, 0);
		
		for(Instance i : instances) {			
			if (testHS.contains(generatekey(instances, keys, i))) { //Si esta en el test a copiar
				newTest.add(i); //lo añado al test
			}else { //si no
				newTrain.add(i); //lo añado al train
			}
		}		
		//Salvar los arff del train y test
		saveInstances(newTrain, args[2]);
		saveInstances(newTest, args[3]);

		
	 }
}
