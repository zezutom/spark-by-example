[![Build Status](https://travis-ci.org/zezutom/spark-by-example.svg?branch=master)](https://travis-ci.org/zezutom/spark-by-example)
[![Coverage Status](https://coveralls.io/repos/zezutom/spark-by-example/badge.svg?branch=master&service=github&ts=123)](https://coveralls.io/github/zezutom/spark-by-example?branch=master)
# Spark by Example

## Table of Contents
- [Get Started](#get-started)
  - [Toolbox](#toolbox) 
  - [Preparation Steps](#preparation-steps)
- [Basics](#basics)
  - [RDD Operations: Word Count](#rdd-operations-word-count)
  - [Accumulators: Text Analysis](#accumulators-text-analysis)

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
##### Step 3 - Start a Data Node
```
$HADOOP_PREFIX/sbin/hadoop-daemon.sh --config $HADOOP_CONF_DIR --script hdfs start datanode
```
##### Step 4 - Create a Log Directory
```
hadoop fs -mkdir -p /var/log
```
## Basics

### RDD Operations: Word Count
Resilient Distributed Datasets (RDDs):
* fault-tolerant, as in zero data loss
* are operated on in parallel: big data, cluster of workers
* contain collection of data elements: text, numbers, business entities etc.

See the official [Programming Guide](http://spark.apache.org/docs/latest/programming-guide.html#resilient-distributed-datasets-rdds) for more details.

RDD Operations
* Transformations
  * create new RDDs according to transformation rules
  * are lazy, ie. nothing happens until the changes are 'committed' by calling an action
  * represent the Map part of the Map Reduce pattern
* Actions
 * trigger transformation workflow 
 * return aggregated result back to the driver program (beware: memory consumption and resource utilization)
 * represent the Reduce part of the Map Reduce pattern

See the official [Programming Guide](http://spark.apache.org/docs/latest/programming-guide.html#rdd-operations) for more details.

A _word count_ example is a _hello world_ in a space of Map Reduce and big data processing. The example parses a text file comprising ten paragraphs of random text (lorem ipsum) and produces a lexically ordered set of word counts.

Output:
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

Source: [WordCount.scala](https://github.com/zezutom/spark-by-example/blob/master/src/main/scala/basic/WordCount.scala), [WordCountTest.scala](https://github.com/zezutom/spark-by-example/blob/master/src/test/scala/basic/WordCountTest.scala)

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

### Accumulators: Text Analysis

Accumulators:
* counters or sums that can be reliably used in parallel processing
* native support for numeric types, extensions possible via API
* workers (tranformation) can modify, but cannot read
* only a driver (action) can read the accumulated value

See the official [Programming Guide](http://spark.apache.org/docs/latest/programming-guide.html#accumulators-a-nameaccumlinka) for more details.

The problem of text analysis expands on the _word count_ example from the [previous section](#rdd-operations-word-count). Accumulators are applied to collect standard and overly not too interesting facts about the analysed text, such as a total number of characters and words. 

Arguably the most exciting part is an effort to capture the essence of a piece of an English text, such as a book, within _N_ most frequently used words. 

First and foremost, casual expressions that don't carry substantial information need to be filtered out. The [English Club](https://www.englishclub.com) helped me arrive at a list of common words I choose to skip: [commonwords.txt](https://github.com/zezutom/spark-by-example/blob/master/src/main/resources/commonwords.txt).

The actual word count naturally makes use of the logic implemented in the _word count_ example. However, minor tweaks apply:
* word count pairs are sorted by counts rather than alphabetically
* descending sort order guarantees the most frequent pairs are always on top of the list

Code excerpt:
```
...
.filter(!commonWords.contains(_))  // Filter out all too common words
.map((_, 1))
.reduceByKey(_ + _)
.sortBy(_._2, ascending = false)
```

Now, the fun part. Forget the boring 'loremipsum' and reach out for some genuine master piece, such as _20.000 Leagues under the Sea_ by Jules Verne. Courtesy of [textfiles.com](http://www.textfiles.com). Here is what the text analyser concluded about the remarkable book:

```
characters: 568889, words: 101838, the most frequent words:
(captain,564)
(nautilus,493)
(nemo,334)
(ned,283)
(sea,273)
```

Source: [TextAnalyser.scala](https://github.com/zezutom/spark-by-example/blob/master/src/main/scala/basic/TextAnalyser.scala), [TextAnalyserTest.scala](https://github.com/zezutom/spark-by-example/blob/master/src/test/scala/basic/TextAnalyserTest.scala)

#### Run the Example

##### Step 1 - Download the Book
[20.000 Leagues under the Sea by Jules Verne](http://www.textfiles.com/etext/FICTION/2000010.txt)

##### Step 2 - Copy the Downloaded TXT file to HDFS
The example below is for Mac OS X, your 'downloads' directory location might differ.
```
hadoop fs -copyFromLocal ~/Downloads/2000010.txt /var/log/
```
##### Step 3 - Build the Project
Run the following command in the project root directory:
```
sbt package
```
##### Step 4 - Submit to Spark
```
$SPARK_HOME/bin/spark-submit --class "basic.TextAnalyser" target/scala-2.11/spark-by-example_2.11-1.0.jar
```

#### View the Results
The results are printed directly into the console, so no extra work is needed.


