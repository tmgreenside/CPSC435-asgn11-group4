tester="tgreenside"
filename="TermPairCount.java"
classname="TermPairCount"
jarFile="termpaircount.jar"
classDirectory="termpaircount_classes"
testPlatform="Amazon"

rm -r $classDirectory
mkdir $classDirectory

if ["$testPlatform" == "Amazon"] then
    javac -classpath /usr/lib/hadoop/client/hadoop-common-2.8.5-amzn-3.jar:/usr/lib/hadoopmapreduce/hadoop-mapreduce-client-core-2.8.5-amzn-3.jar -d $classDirectory $filename
else
    javac -classpath /share/hadoop-common-2.5.1.jar:/share/hadoop-mapreduce-client-core2.5.1.jar -d $classDirectory $filename
fi

jar -cvf $jarFile -C $classDirectory .

if ["$testPlatform" == "Amazon"] then
    /usr/bin/hadoop fs -mkdir /user/"$classname"
    /usr/bin/hadoop fs -mkdir /user/"$classname"/input
    /usr/bin/hadoop fs -put ./input/*.txt /user/"$classname"/input
    /usr/bin/hadoop jar $jarFile $classname /user/"$classname"/input /user/"$classname"/output
    /usr/bin/hadoop fs -cat /user/"$classname"/output/part-00000

else
    hadoop fs -mkdir /user/"$tester" (use your own last name)
    hadoop fs -mkdir /user/"$tester"/termpaircount
    hadoop fs -mkdir /user/"$tester"/termpaircount/input
    hadoop fs -copyFromLocal ./input/*.txt /user/"$tester"/termpaircount/input
fi
