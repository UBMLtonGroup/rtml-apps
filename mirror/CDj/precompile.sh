#! /bin/bash

JARS=dist/lib

if [ "X$1" == X ] ; then
	echo "precompile.sh input_binary_dump output_java_source" >&2
	exit 1
fi

INPUT=$1
shift

if [ "X$1" != X ] ; then
	OUTPUT=$1
else
	OUTPUT=/dev/stdout
fi

java -cp $JARS/tools.jar dump.DumpToJavaCode $INPUT $OUTPUT
