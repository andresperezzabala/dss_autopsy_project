package converters.spam;

import static utils.Utils.saveInstances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;


public class SpamMain {

	public static void main(String[] args) {
		
		/*
		 *Como generar un archivo arff
		 *https://waikato.github.io/weka-wiki/creating_arff_file/	
		 *La clase FastVector est? obsoleta
		*/
		
		//Crear un vector de atributos
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		
		// atributo de tipo string		
		atts.add(new Attribute("text", (List<String>) null));
		
		// atributo de tipo nominal
		List<String> attVals = new ArrayList<String>();
		attVals.add("spam");
		attVals.add("ham");
		atts.add(new Attribute("class", attVals));
		
		//Se crea un conjunto de datos de instancias vacias.
		Instances data = new Instances(args[1], atts, 0);
		
		//Se indica cual es la clase
		data.setClassIndex(data.numAttributes()-1);
		
		//Se obtiene el contenido del fichero		
		/*
		 * Pregunta: Java, the fastest class to read from a txt file [closed]
		 * Autor: Rahul Tripathi
		 * url: https://stackoverflow.com/questions/13480183/java-the-fastest-class-to-read-from-a-txt-file/13480293#13480293
		 */
				
		try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {			
			String[] instanceLine;
			double[] instanceValue;
			String line = null;			
			
			//Por cada linea hasta que llegue a final del documento
			while ((line = br.readLine()) != null) { 
				// Cada linea representa una instancia, y se separa la clase del texto
				instanceLine = line.split("\\s+", 2); 		
				
				//Instanciar los valores que tomara la Instancia
				instanceValue = new double[data.numAttributes()];
				
				//A?adir un string
				instanceValue[0] = data.attribute(0).addStringValue(instanceLine[1]);
				
				//A?adir un valor nominal, en este caso la clase.
				instanceValue[1] = attVals.indexOf(instanceLine[0]);
		
				//A?adir la instancia al conjunto de datos;
				data.add(new DenseInstance(1.0, instanceValue));							
			}				
			saveInstances(data, args[1]);
		} catch(Exception ex) {
		    ex.printStackTrace();
		}

		
			
		
		
	}

}
