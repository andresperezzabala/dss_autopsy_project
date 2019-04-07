#!/bin/bash
# Este script recoge un csv, lo convierte a un formato valido para el modelo y realiza su prediccion.
#
# EJEMPLO DE USO
# ./predict_class.sh ../datos/autopsy/prueba_una_instancia_presentacion.csv pred.txt

# recoger argumentos
csv_file=$1
output_pred=$2

# nombres de archivos
raw_file="raw.arff"
bow_file="bow.arff"
bow_as_file="bow_as.arff"
dictionary="../datos/autopsy/dictionary.txt"
atributos="../datos/autopsy/atributos.txt"
model="../modelos/multilayer_perceptron.model"


java -jar ../jars/AutopsyCSV2Raw.jar $csv_file $raw_file &&
java -jar ../jars/AutopsyRaw2BoW.jar $raw_file $bow_file -L $dictionary &&
java -jar ../jars/AutopsyAdapterBow2AS.jar $bow_file $bow_as_file $atributos &&
java -jar ../jars/Predictions.jar $model $bow_as_file $output_pred
