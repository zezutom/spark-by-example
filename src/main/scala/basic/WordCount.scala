package basic

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by tom on 06/12/2015.
  */
object WordCount {
  def main(args: Array[String]) {
    // Instantiate Spark
    val conf = new SparkConf().setMaster("local[2]").setAppName("Word Count")
    val sc = new SparkContext(conf)

    // Count individual words and save the results in an alphabetical order
    val counts = sc.textFile("hdfs:///var/log/loremipsum.txt")
      .flatMap(_.split("\\s"))    // Split on any white character
      .filter(!_.isEmpty)         // Filter out any non-words
      .map(_.stripSuffix(",")     // Remove punctuation and transfer to lowercase
            .stripSuffix(".")
            .stripSuffix("!")
            .stripSuffix("?")
            .stripSuffix(":")
            .stripSuffix(";")
            .toLowerCase)
      .map(word => (word, 1))     // Finally, count words
          .reduceByKey(_ + _)
      .sortByKey()                // and sort the word counts in a lexical order

    counts.saveAsTextFile("hdfs:///var/out/wordcount")
  }

}
