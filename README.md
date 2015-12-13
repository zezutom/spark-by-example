[![Build Status](https://travis-ci.org/zezutom/spark-by-example.svg?branch=master)](https://travis-ci.org/zezutom/spark-by-example)
[![Coverage Status](https://coveralls.io/repos/zezutom/spark-by-example/badge.svg?branch=master&service=github&ts=123)](https://coveralls.io/github/zezutom/spark-by-example?branch=master)
# Spark by Example

## Table of Contents
- [Get Started](#get-started)
  - [Toolbox](#toolbox) 
  - [Preparation Steps](#preparation-steps)
- [Basics](#basics)
  - [RDD Transformations: Word Count](#rdd-transformations:word-count)
  - [Accumulators: Text Analysis](#text-analysis)

## Get Started

#### Toolbox
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
##### Step 2 - Start a Name Node
```
$HADOOP_PREFIX/sbin/hadoop-daemon.sh --config $HADOOP_CONF_DIR --script hdfs start namenode
```
##### Step 3 - Create a Log Directory
```
hadoop fs -mkdir -p /var/log
```
## Basics

### RDD Transformations: Word Count
A _hello world_ in a space of Map Reduce and big data processing. The example parses a text file comprising ten paragraphs of random text (lorem ipsum) and produces a lexically ordered set of word counts.

Output example:
```
(a,9)
(ac,13)
(accumsan,1)
(ad,1)
(adipiscing,1)
(aenean,6)
(aliquam,7)
..
(lorem,5)
(luctus,5)
(maecenas,1)
(magna,8)
..
```
#### Run the Example

##### Step 1 - Copy a Text File to HDFS
The input file is located in the resources directory. Please copy it to HDFS as follows:
```
hadoop fs -copyFromLocal src/main/resources/loremipsum.txt /var/log/
```
##### Step 2 - Build the Project
Run the following command in the project root directory:
```
sbt package
```
##### Step 3 - Submit to Spark
Assuming your local installation of Hadoop is up-n-running, the text file has been copied to HDFS and the project has been built, you are ready to submit the application to Spark. To do so, run the following command while in the project root directory:
```
$SPARK_HOME/bin/spark-submit --class "basic.WordCount" target/scala-2.11/spark-by-example_2.11-1.0.jar
```

#### View the Results
Once Spark execution is done, the resulting word counts are stored as text files (two or more) in HDFS. Here is an example of how to easily view one of the results without the need to move the file from HDFS to your local filesystem.
```
hadoop fs -cat '/var/out/wordcount/part-00000' | less
```

Don't forget to delete the output HDFS location, before submitting the job again.
```
hadoop fs -rm -r /var/out/wordcount/
```







