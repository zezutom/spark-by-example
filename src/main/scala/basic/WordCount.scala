package basic

import common.{AppUtil, Transformer}
import org.apache.spark.rdd.RDD

/**
  * Run as:
  * $SPARK_HOME/bin/spark-submit \
  * --class "basic.WordCount" \
  * target/scala-2.11/spark-by-example_2.11-1.0.jar
  */
class WordCount extends Transformer[String, (String, Int)] {

  override def transform(rdd: RDD[String]): RDD[(String, Int)] = {
    rdd
    .flatMap(_.split("\\s"))    // Split on any white character
    .map(_.replaceAll(
      "[,.!?:;]", "")           // Remove punctuation and transfer to lowercase
      .trim
      .toLowerCase)
    .filter(!_.isEmpty)         // Filter out any non-words
    .map((_, 1))                // Initialize word count pairs
    .reduceByKey(_ + _)         // Finally, count words
    .sortByKey()                // and sort the word counts in a lexical order
  }
}

object WordCount {

  def main(args: Array[String]) {
    // Instantiate Spark
    val sc = AppUtil.sc("Word Count")

    // Count individual words and save the results in an alphabetical order
    val counts = new WordCount().transform(sc.textFile(AppUtil.hdfs("/var/log/loremipsum.txt")))

    counts.saveAsTextFile(AppUtil.hdfs("/var/out/wordcount"))
  }
}
