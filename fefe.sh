mkdir log

if [ $1 = "replay" ]
then
	for i in $( seq 1 $2)
	do
		./replay.sh log/his$i > log/log$i
	done
else
	for i in $( seq 1 $1)
	do
		./verify.sh
	done
fi
