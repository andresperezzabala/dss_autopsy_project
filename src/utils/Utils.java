package utils;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

import weka.classifiers.lazy.IBk;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

import weka.core.stemmers.LovinsStemmer;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.unsupervised.instance.Resample;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Random;

import static weka.classifiers.lazy.IBk.*;

public class Utils {

    /**
     * Carga las instancias.
     * @param path la ruta de donde coger las instancias
     * @return
     */
    public static Instances loadInstances(String path) throws Exception {
        DataSource source = new DataSource(path);
        return source.getDataSet();
    }


    /**
     * Devuelve el indice de la clase minoritaria.
     * @param instances Instancias con la clase ya puesta.
     * @return
     */
    public static int getMinoritaryNominalClassIndex(Instances instances){
        int classIndex = instances.classIndex();
        AttributeStats classStats = instances.attributeStats(classIndex);

        int[] classFreqs = classStats.nominalCounts;
        int minIndex = 0;
        int minFreq = classFreqs[0];

        int index = 0;
        for(int freq: classFreqs) {
            if (freq < minFreq && freq != 0) {
                minFreq = freq;
                minIndex = index;
            }
            index++;
        }

        return minIndex;
    }


    /**
     * Devuelve informacion del conjunto de datos.
     * @param instances
     * @return
     */
    public static String getInfo(Instances instances) {
        StringBuilder result = new StringBuilder();
        result.append("##### INSTANCES INFO #####");
        result.append("\n");
        result.append("\n");
        result.append("Size: ").append(instances.size());
        result.append("\n");
        result.append("\n");

        result.append("## ATRIBUTES ");
        result.append(instances.numAttributes());
        result.append(" ##");
        result.append("\n");
        
        Enumeration<Attribute> atts = instances.enumerateAttributes();
        
        while (atts.hasMoreElements()) {
        	Attribute attribute = atts.nextElement();
            result.append(attribute.toString());
            result.append("\n");
        }
        if (instances.classIndex() >= 0) {
            result.append("CLASS\n");
            result.append(instances.classAttribute().toString());
        }
        result.append("\n");
        result.append("\n");

        for (int i = 0; i < instances.numAttributes(); i++) {
            String stats = instances.attributeStats(i).toString();
            result.append(instances.attribute(i).name());
            result.append("\n");
            result.append(stats);
            if (instances.attribute(i).isNumeric()) {
//                result.append("Max: " + instances.attributeStats(i).numericStats.max);
//                result.append("\n");
//                result.append("Min: " + instances.attributeStats(i).numericStats.min);
//                result.append("\n");
//                result.append("Mean: " + instances.attributeStats(i).numericStats.mean);
//                result.append("\n");
//                result.append("StdDev: " + instances.attributeStats(i).numericStats.stdDev);
//                result.append("\n");
                result.append("Stats:\n" + instances.attributeStats(i).numericStats);
            }
            result.append("\n");
        }

        return result.toString();
    }


    /**
     * Devuelve un nuevo dataset filtrando los atributos que no son importantes.
     * @param instances
     * @return
     */
    public static Instances filterAttributes(Instances instances) throws Exception {
        AttributeSelection filter = new AttributeSelection();
        filter.setEvaluator(new CfsSubsetEval()); // Correlation-based feature selection
        filter.setSearch(new BestFirst());
        filter.setInputFormat(instances);
        return Filter.useFilter(instances, filter);
    }
    
    /**
     * Devuelve un nuevo dataset filtrando los atributos que no son importantes.
     * @param instances
     * @return
     */
    public static Instances filterAttributesRanked(Instances instances, int numAttributes) throws Exception {
        AttributeSelection filter = new AttributeSelection();
        filter.setEvaluator(new InfoGainAttributeEval()); // Correlation-based feature selection
        Ranker r = new Ranker();
        r.setNumToSelect(numAttributes);
        filter.setSearch(r);
        filter.setInputFormat(instances);
        return Filter.useFilter(instances, filter);
    }


    /**
     * Hace kfold crossvalidation dado un dataset y un clasificador.
     * @param cls clasificador
     * @param instances instancias
     * @param folds numero de iteraciones
     * @return
     * @throws Exception
     */
    public static Evaluation crossvalidation(Classifier cls, Instances instances, int folds) throws Exception {
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(cls, instances, folds, new Random(1));
        return evaluation;
    }


    /**
     * Leave one out es crossvalidation con k=numero de instancias.
     * @param cls el clasificador.
     * @param instances las instancias.
     * @return
     * @throws Exception
     */
    public static Evaluation leaveOneOut(Classifier cls, Instances instances) throws Exception {
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(cls, instances, instances.size(), new Random(1));
        return evaluation;
    }


    /**
     * Hace una evaluacion hold out.
     * @param cls
     * @param train
     * @param test
     * @return
     * @throws Exception
     */
    public static Evaluation holdOutEval(Classifier cls, Instances train, Instances test) throws Exception {
        cls.buildClassifier(train);
        Evaluation evaluation = new Evaluation(train);
        evaluation.evaluateModel(cls, test);
        return evaluation;
    }


    /**
     * Evaluacion NO honesta. Se entrena y evalua sobre el conjunto completo de datos.
     * Se entrena y evalua sobre el conjunto completo de datos.
     * @param cls
     * @param instances
     * @return
     * @throws Exception
     */
    public static Evaluation noHonestEval(Classifier cls, Instances instances) throws Exception {
        cls.buildClassifier(instances);
        Evaluation evaluation = new Evaluation(instances);
        evaluation.evaluateModel(cls, instances);
        return evaluation;
    }


    /**
     * Parte un dataset.
     * @param percent por donde partirlo.
     * @param instances el dataset a partir.
     * @param inverse si percent es 70%, que inverse sea true significa
     *                que coge el 70% si inverse es false cogera el 30%.
     * @return
     * @throws Exception
     */
    public static Instances split(Double percent, Instances instances, Boolean inverse) throws Exception {
        Resample filter = new Resample();
        filter.setInvertSelection(inverse);
        filter.setNoReplacement(true);
        filter.setRandomSeed(1);
        filter.setSampleSizePercent(percent);
        filter.setInputFormat(instances);

        return Filter.useFilter(instances, filter);
    }


    /**
     * Hace una particion train dado un dataset y el porcentaje
     * @param percent por donde hacer la particion
     * @param instances el dataset
     * @return
     * @throws Exception
     */
    public static Instances getTrain(Double percent, Instances instances) throws Exception {
        return split(percent, instances, false);
    }


    /**
     * Hace una particion test dado un dataset y el porcentaje
     * @param percent por donde hacer la particion
     * @param instances el dataset
     * @return
     * @throws Exception
     */
    public static Instances getTest(Double percent, Instances instances) throws Exception {
        return split(percent, instances, true);
    }


    /**
     * El output de weka.
     * @param evaluation
     * @return
     * @throws Exception
     */
    public static String getEvaluationResults(Evaluation evaluation) throws Exception {

        //        double acc=evaluation.pctCorrect();
//        double inc=evaluation.pctIncorrect();
//        double kappa=evaluation.kappa();
//        double mae=evaluation.meanAbsoluteError();
//        double rmse=evaluation.rootMeanSquaredError();
//        double rae=evaluation.relativeAbsoluteError();
//        double rrse=evaluation.rootRelativeSquaredError();
//        double confMatrix[][]= evaluation.confusionMatrix();
//
//        System.out.println("Correctly Classified Instances  " + acc);
//        System.out.println("Incorrectly Classified Instances  " + inc);
//        System.out.println("Kappa statistic  " + kappa);
//        System.out.println("Mean absolute error  " + mae);
//        System.out.println("Root mean squared error  " + rmse);
//        System.out.println("Relative absolute error  " + rae);
//        System.out.println("Root relative squared error  " + rrse);
//        System.out.println("Confusion matrix:");
//        System.out.println(confMatrix.);
        String result = evaluation.toSummaryString() +
                "\n" +
                evaluation.toClassDetailsString() +
                "\n" +
                evaluation.toMatrixString() +
                "\n";
        return result;
    }


    /**
     * Guarda instancias en un archivo
     * @param instances las instancias a guardar
     * @param path la ruta a guardar
     */
    public static void saveInstances(Instances instances, String path) {
        try {
            DataSink.write(path, instances);
        }
        catch (Exception e) {
            System.err.println("Failed to save data to: " + path);
            e.printStackTrace();
        }
    }


    /**
     * Escribe texto en un archivo.
     * @param text
     * @param path
     */
    public static void printToFile(String text, String path) {
        try {
            PrintWriter out = new PrintWriter(path);
            out.println(text);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Guarda el modelo en un archivo.
     * @param cls clasificador a guardar
     * @param path rutado donde guardarlo
     * @throws Exception
     */
    public static void saveModel(Classifier cls, String path) throws Exception {
        SerializationHelper.write(path, cls);
    }


    /**
     * Carga el modelo de un archivo.
     * @param path ruta de donde cargarlo
     * @return
     * @throws Exception
     */
    public static Classifier loadModel(String path) throws Exception {
        return (Classifier) SerializationHelper.read(path);
    }

    /**
     * Calcula los mejores parametros para el KNN mediante fuerza bruta. Tarda muchisimo.
     * @param instances
     * @return
     * @throws Exception
     */
    public static String manualSearchBestParamsKNN(Instances instances) throws Exception {
        int minoritaryClassIndex = getMinoritaryNominalClassIndex(instances);
        IBk cls;
        int[] weigths = {WEIGHT_NONE, WEIGHT_INVERSE, WEIGHT_SIMILARITY};
        DistanceFunction[] distances = {new ManhattanDistance(), new EuclideanDistance()};
        Evaluation eval;
        double bestFMeasure = 0;
        int bestDistance = 0;
        int bestK = 0;
        int bestWeight = 0;

        for(int k = 1; k < (int) (0.5 * instances.size()); k++){
            cls = new IBk(k);

            for(int w = 0; w < weigths.length; w++){
                cls.setDistanceWeighting(new SelectedTag(weigths[w], TAGS_WEIGHTING));

                for(int d = 0; d < distances.length; d++) {
                    cls.getNearestNeighbourSearchAlgorithm().setDistanceFunction(distances[d]);

                    System.out.println("k: " + k);
                    System.out.println("d: " + d);
                    System.out.println("w: " + w);
                    System.out.println();

                    eval = new Evaluation(instances);
                    eval.crossValidateModel(cls, instances, 10, new Random(1));

                    double currentFMeasure = eval.fMeasure(minoritaryClassIndex);
                    if (currentFMeasure > bestFMeasure) {
                        bestDistance = d;
                        bestFMeasure = currentFMeasure;
                        bestK = k;
                        bestWeight = w;
                    }
                }
            }
        }

        String textWeight = "";
        switch (bestWeight) {
            case 0: textWeight = "WEIGHT_NONE";
                break;
            case 1: textWeight = "WEIGHT_INVERSE";
                break;
            case 2: textWeight = "WEIGHT_SIMILARITY";
                break;
        }

        String textDistance = "";
        switch (bestDistance) {
            case 0: textDistance = "Manhattan";
                break;
            case 1: textDistance = "Euclidean";
                break;
        }

        String result = "";
        result += "\n";
        result += "BestDistance: " + textDistance;
        result += "\n";
        result += "BestK: " + bestK;
        result += "\n";
        result += "BestWeight: " + textWeight;
        result += "\n";
        result += "BestFmeasure: " + bestFMeasure;
        result += "\n";
        result += "\n";
        return result;
    }


    public static enum DictionaryOption {
        SAVE, LOAD
    }

    /**
     * Convierte un conjunto de instancias a TFIDF, ademas guarda o carga el diccionario a usar.
     * @param instances
     * @param dictionaryFile
     * @param opt
     * @return
     * @throws Exception
     */
    public static Instances filterWithBoW(Instances instances, File dictionaryFile, DictionaryOption opt) throws Exception {
        return filterStringToWord(instances, false, dictionaryFile, opt);
    }

    /**
     * Convierte un conjunto de instancias a TFIDF, ademas guarda o carga el diccionario a usar.
     * @param instances
     * @param dictionaryFile
     * @param opt
     * @return
     * @throws Exception
     */
    public static Instances filterWithTFIDF(Instances instances, File dictionaryFile, DictionaryOption opt) throws Exception {
        return filterStringToWord(instances, true, dictionaryFile, opt);
    }

    /**
     * Convierte un conjunto de instancias a TFIDF o BOW, ademas guarda o carga el diccionario a usar.
     * @param instances
     * @param useTFIDF
     * @param dictonaryFile
     * @param opt
     * @return
     * @throws Exception
     */
    public static Instances filterStringToWord(Instances instances, boolean useTFIDF, File dictonaryFile, DictionaryOption opt) throws Exception {

        if (opt == DictionaryOption.LOAD) {
            FixedDictionaryStringToWordVector fixedDictfilter = new FixedDictionaryStringToWordVector();
            fixedDictfilter.setDictionaryFile(dictonaryFile);
            fixedDictfilter.setLowerCaseTokens(true);
            fixedDictfilter.setLowerCaseTokens(true); // considerar iguales las palabras en minuscula y mayuscula
            fixedDictfilter.setOutputWordCounts(true); // true: contador de palabaras, false: 1 si aparece 0 si no
            fixedDictfilter.setTFTransform(useTFIDF); // no aplica TF
            fixedDictfilter.setIDFTransform(useTFIDF); // no aplica IDF
            fixedDictfilter.setInputFormat(instances);

            // Filtrar las stop words, por ejemplo: "of", "the"...etc
            fixedDictfilter.setStopwordsHandler(new Rainbow());

            // Quedarnos con la raiz de las palabras, ejemplo:
            //    "differences" -> "diff"
            //    "different"   -> "diff"
            //
            fixedDictfilter.setStemmer(new LovinsStemmer());
            return Filter.useFilter(instances, fixedDictfilter);

        } else if (opt == DictionaryOption.SAVE) {

            StringToWordVector filter = new StringToWordVector();
            filter.setWordsToKeep(20000000);
            filter.setDictionaryFileToSaveTo(dictonaryFile);
            filter.setLowerCaseTokens(true); // considerar iguales las palabras en minuscula y mayuscula
            filter.setOutputWordCounts(true); // true: contador de palabaras, false: 1 si aparece 0 si no
            filter.setTFTransform(useTFIDF); // no aplica TF
            filter.setIDFTransform(useTFIDF); // no aplica IDF
            filter.setInputFormat(instances);

            // Filtrar las stop words, por ejemplo: "of", "the"...etc
            filter.setStopwordsHandler(new Rainbow());

            // Quedarnos con la raiz de las palabras, ejemplo:
            //    "differences" -> "diff"
            //    "different"   -> "diff"
            //
            filter.setStemmer(new LovinsStemmer());

            // Eliminar tokens que no queremos: ejemplo: numeros, simbolos...
            WordTokenizer tokenizer = new WordTokenizer();
            tokenizer.setDelimiters(".,;:[]%'\"()?!/\n -_><&#=*1234567890$");
            filter.setTokenizer(tokenizer);

            return Filter.useFilter(instances, filter);
        } else {
            System.err.println("Option not supported.");
            return null;
        }
    }

    /**
     * Genera un HashMap de instancias cuya clave será el conjunto de atributos que se representan mediante un String
     * Los atributos deben ir separados mediante un espacion " "
     */
    public static HashSet<String> generateHashSet(Instances pData, String[] pKeys) {        	
    	HashSet<String> data = new HashSet<String>();    	
    	for(Instance i: pData) { //Recorrer todas las instancias    		
    		data.add(generatekey(pData, pKeys, i)); // Añadir al HashSet la clave    	 	
    	}   
    	return data;
    }
    
    public static String generatekey(Instances pData, String[] pKeys, Instance i) {
    	String key = "";
    	for (String k : pKeys) { //Genera la clave por cada atributo requerido
			key += Double.toString(i.value(pData.attribute(k))); //Añadir a la clave el valor del atributo k en la instancia i
		}
    	return key;
    }
   
  
	
    
}
