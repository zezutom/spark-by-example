package streaming

import common.{AppUtil, StreamTransformer}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Duration, Seconds}
import twitter4j.Status

/**
  * Run as:
  *
  * sbt assembly
  *
  * $SPARK_HOME/bin/spark-submit \
  * --class "streaming.PopularTweets" \
  * target/scala-2.11/spark-by-example-assembly-1.0.jar
  */
class PopularTweets(val windowDuration: Duration, val slideDuration: Duration) extends StreamTransformer[Status, (String, Int)] {
  override def transform(stream: DStream[Status]): DStream[(String, Int)] =
    stream
      .map(_.getText)
      .flatMap(_.split(" "))
      .filter(_.startsWith("#"))
      .filter(_.matches("^#[a-zA-Z0-9]+$"))
      .map((_, 1))
      .reduceByKeyAndWindow(_ + _, _ - _, windowDuration, slideDuration)
      .transform(_.sortBy(_._2, ascending = false))
      .transform(_.sortBy(_._1))
}

object PopularTweets {

  def main(args: Array[String]) {

    // Instantiate streaming context
    val ssc = AppUtil.ssc("Trending Tweets", 1)

    // Connect to Twitter's public stream
    val stream = TwitterUtils.createStream(ssc, AppUtil.twitterAuth())

    // Watch for popular tweets (2-min sliding window)
    new PopularTweets(Seconds(60 * 2), Seconds(1))
      .transform(stream)
      .print(10)

    ssc.start()
    ssc.awaitTermination()
  }
}
