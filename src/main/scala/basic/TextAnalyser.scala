package basic

import common.AppUtil
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.io.Source

/**
  * Run as:
  * $SPARK_HOME/bin/spark-submit \
  * --class "basic.TestAnalyser" \
  * target/scala-2.11/spark-by-example_2.11-1.0.jar
  *
  * Please note, this example uses an e-text book '20.000 Leagues under the Sea' by Jules Verne
  * (courtesy: Project Gutenberg)
  *
  * To run this example 'as is' please:
  *
  * 1. Download the file
  *     http://www.textfiles.com/etext/FICTION/2000010.txt
  *
  * 2. Upload the downloaded file to HDFS (/var/log/)
  *     example (Mac OS X): hadoop fs -copyFromLocal ~/Downloads/2000010.txt /var/log/
  *
  * Alternatively, change the 'main' method to accept a file of your choice as an input. For instance,
  * you could use the 'loremipsum.txt' attached to this project as a resource.
  *
  */
class TextAnalyser(val sc: SparkContext, val topN: Int) {

  def analyse(rdd: RDD[String]): TextStats = {
    val commonWords = Source.fromInputStream(getClass.getResourceAsStream("/commonwords.txt"))
      .getLines()
      .filter(!_.isEmpty)
      .filter(!_.startsWith("#"))   // Comments
      .toList
    val totalChars = sc.accumulator(0, "Total Characters")
    val totalWords = sc.accumulator(0, "Total Words")

    val wordCounts = rdd
      .map(x => { totalChars += x.length; x})
      .flatMap(_.split("\\s"))          // Split on any white character
      .map(_.replaceAll(
      "[,.!?:;]", "")                   // Remove punctuation and transfer to lowercase
      .trim
      .toLowerCase)
      .filter(!_.isEmpty)               // Filter out any non-words
      .map(x => {totalWords += 1; x})
      .filter(!commonWords.contains(_))  // Filter out all too common words
      .map((_, 1))
      .reduceByKey(_ + _)
      .sortBy(_._2, ascending = false)

    new TextStats(totalChars.value, totalWords.value, wordCounts.take(topN))
  }
}

class TextStats(val totalChars: Int, val totalWords: Int, val mostFrequentWords: Seq[(String, Int)]) {
  override def toString = s"characters: $totalChars, " +
    s"words: $totalWords, " +
    "the most frequent words:\n" + mostFrequentWords.mkString("\n")
}

object TextAnalyser {

  def main(args: Array[String]) {
    // Instantiate Spark
    val sc = AppUtil.sc("Text Analysis")

    val stats = new TextAnalyser(sc, 5).analyse(sc.textFile(AppUtil.hdfs("/var/log/2000010.txt")))

    println(s"STATS: $stats")
  }
}
