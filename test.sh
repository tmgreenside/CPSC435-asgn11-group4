tester="tgreenside"
filename="TermPairCount.java"
classDirectory="termpaircount_classes"
testPlatform="Amazon"

rm -r $classDirectory
mkdir $classDirectory

if ["$testPlatform" == "Amazon"] then
    javac -classpath /usr/lib/hadoop/client/hadoop-common-2.8.5-amzn-3.jar:/usr/lib/hadoopmapreduce/hadoop-mapreduce-client-core-2.8.5-amzn-3.jar -d $classDirectory $filename
else
    javac -classpath /share/hadoop-common-2.5.1.jar:/share/hadoop-mapreduce-client-core2.5.1.jar -d $classDirectory $filename
fi
