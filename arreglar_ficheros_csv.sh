#!/bin/bash

# EJEMPLO DE USO DEL SCRIPT
#
# 1) darle permisos de ejecucion si no los tiene
# chmod + x arreglar_ficheros_csv.sh
#
# 2) Para usarlo hay que darle 2 argumentos:
#       fichero_original.csv
#       fichero_arreglato.csv
#
# ./arreglar_ficheros_csv.sh datos/tweet_sentiment_eGela/tweetSentiment.train.csv out.csv

cp $1 $2

# quitar saltos de linea de windows
sed -i 's/\r//' $2

# quitar lineas en blanco
sed -i '/^$/d' $2

# quitar espacios al final de las lineas
#sed -i 's/[ \t]*$//' "$2"

# añadir comillas al principio y al final de la linea si no la tienen
sed -i 's/[^"]$/&"/' $2
sed -i 's/^[^"]/"&/' $2

