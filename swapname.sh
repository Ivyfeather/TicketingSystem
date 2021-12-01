#!/bin/bash
if [ ! -n "$1" ];then
	echo "Missing name 1"
	exit 0
elif [ ! -n "$2" ];then
	echo "Missing name 2"
	exit 0
fi

mv $1 tmp
mv $2 $1
mv tmp $2


echo "Exchange Success!"

