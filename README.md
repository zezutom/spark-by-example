# Spark by Example

## Basics

### Word Count
A 'hello world' in a space of Map Reduce and big data processing. The example parses a text file comprising ten paragraphs of random text (lorem ipsum) and produces a lexically ordered set of word counts.

#### Used Technologies
* Apache Spark 1.5.2 compiled for Scala 2.11
* Hadoop 2.7.1
* Scala 2.11.7
* SBT 0.13.9

#### Preparation Steps

##### Step 1 - Start Hadoop
If you haven't used your local installation of Hadoop before, start with HDFS formatting.
```
$HADOOP_PREFIX/bin/hdfs namenode -format localnode
```
Next, start the Name Node
```
$HADOOP_PREFIX/sbin/hadoop-daemon.sh --config $HADOOP_CONF_DIR --script hdfs start namenode
```
##### Step 2 - Copy a Text File to HDFS
The input file is located in the resources directory. Please copy it to HDFS as follows:
```
hadoop fs -copyFromLocal src/main/resources/loremipsum.txt /var/log/
```
##### Step 3 - Build the Project
Run the following command in the project root directory:
```
sbt package
```

#### Run the Example

##### Step 1 - Submit to Spark
Assuming your local installation of Hadoop is up-n-running, the text file has been copied to HDFS and the project has been built, you are ready to submit the application to Spark. To do so, run the following command while in the project root directory:
```
$SPARK_HOME/bin/spark-submit --class "basic.WordCount" target/scala-2.11/spark-by-example_2.11-1.0.jar
```

##### Step 2 - View the Results
Once Spark execution is done, the resulting word counts are stored as text files (two or more) in HDFS. Here is an example of how to easily view one of the results without the need to move the file from HDFS to your local filesystem.
```
hadoop fs -cat '/var/out/wordcount/part-00000' | less
```

##### Step 3 (Optional) - Clean Up
Delete the output HDFS location so that you can re-submit the job.
```
hadoop fs -rm -r /var/out/wordcount/
```







