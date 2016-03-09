package basic

import common.AppUtil
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.io.Source

/**
  * This example uses an e-text book '20.000 Leagues under the Sea' by Jules Verne
  * (courtesy: Project Gutenberg)
  *
  * To run this example please:
  *
  * 1. Download the file
  *     http://www.textfiles.com/etext/FICTION/2000010.txt
  *
  * 2. Upload the downloaded file to HDFS (/var/log/)
  *     example (Mac OS X): hadoop fs -copyFromLocal ~/Downloads/2000010.txt /var/log/
  *
  * If all goes fine you should be rewarded with the following output:
  *
  * characters: 568889, words: 101838, the most frequent words:
  * (captain,564)
  * (nautilus,493)
  * (nemo,334)
  * (ned,283)
  * (sea,273)
  *
  *
  * Alternatively, change the 'main' method to accept a file of your choice as an input. For instance,
  * you could use the 'loremipsum.txt' attached to this project as a resource.
  *
  */
class TextAnalyser(val sc: SparkContext, val topN: Int) {
  val _commonWords = sc.broadcast(TextAnalyser.loadCommonWords())
  val _totalChars = sc.accumulator(0L, "Total Characters")

  def analyse(rdd: RDD[String]): TextStats = {
    val commonWords = _commonWords
    val totalChars = _totalChars

    // Count the total number of characters
    rdd.foreach(totalChars += _.length)

    // Populate words
    val words = rdd
      .flatMap(_.split("\\s"))          // Split on any white character
      .map(_.replaceAll(
      "[,.!?:;]", "")                   // Remove punctuation and transfer to lowercase
      .trim
      .toLowerCase)
      .filter(!_.isEmpty)               // Filter out any non-words

    // Identify the most frequently used words
    val wordCounts = words
      .filter(!commonWords.value.contains(_))  // Filter out all too common words
      .map((_, 1))
      .reduceByKey(_ + _)
      .sortBy(_._2, ascending = false)

    TextStats(totalChars.value, words.count(), wordCounts.take(topN))
  }
}

case class TextStats(totalChars: Long, totalWords: Long, mostFrequentWords: Seq[(String, Int)]) {
  override def toString = s"characters: $totalChars, " +
    s"words: $totalWords, " +
    "the most frequent words:\n" + mostFrequentWords.mkString("\n")
}

object TextAnalyser {

  def loadCommonWords(): List[String] =
    Source.fromInputStream(getClass.getResourceAsStream("/commonwords.txt"))
    .getLines()
      .filter(!_.isEmpty)
      .filter(!_.startsWith("#"))   // Comments
      .toList

  def main(args: Array[String]) {
    // Instantiate Spark
    val sc = AppUtil.sc("Text Analysis")

    val stats = new TextAnalyser(sc, 5).analyse(sc.textFile(AppUtil.hdfs("/var/log/2000010.txt")))
    println(stats)
  }
}
