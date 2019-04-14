testPlatform="Amazon"
tester="tgreenside"

if [ "$testPlatform" == "Amazon" ]
then
    /usr/bin/hadoop fs -rm -r /user/TermPairCount
else
    hadoop fs -rmr /user/"$tester"
fi
