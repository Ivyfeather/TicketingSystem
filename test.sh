#! /bin/sh

rm testlog
./clean.sh
javac -encoding UTF-8 -cp . ticketingsystem/Test.java

echo  "Thread: 8 testNum 10万" >> testlog
java -cp . ticketingsystem/Test 8 100000 >> testlog
cat testlog

echo  "Thread: 16 testNum 10万" >> testlog
java -cp . ticketingsystem/Test 16 100000 >> testlog
cat testlog

echo  "Thread: 32 testNum 10万" >> testlog
java -cp . ticketingsystem/Test 32 100000 >> testlog
cat testlog

echo  "Thread: 64 testNum 10万" >> testlog
java -cp . ticketingsystem/Test 64 100000 >> testlog

echo  "Thread: 8 testNum 100万" >> testlog
java -cp . ticketingsystem/Test 8 1000000 >> testlog

echo  "Thread: 16 testNum 100万" >> testlog
java -cp . ticketingsystem/Test 16 1000000 >> testlog

echo  "Thread: 32 testNum 100万" >> testlog
java -cp . ticketingsystem/Test 32 1000000 >> testlog

echo  "Thread: 64 testNum 100万" >> testlog
java -cp . ticketingsystem/Test 64 1000000 >> testlog

mv testlog testDone
