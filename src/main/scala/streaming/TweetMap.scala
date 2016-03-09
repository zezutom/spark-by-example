package streaming

import common.{AppUtil, StreamTransformer}
import org.apache.spark.streaming.dstream.DStream
import twitter4j.Status


/**
  * Which countries tweet the most? Country + percentage of tweets worldwide
  * see: http://aworldoftweets.frogdesign.com/
  * Created by tom on 29/12/2015.
  */
class TweetMap extends StreamTransformer[Status, (String, String)] {

  override def transform(stream: DStream[Status]): DStream[(String, String)] =
    stream
      .map(_.getText)
      .flatMap(_.split(" "))
      .filter(_.startsWith("#"))
      .filter(_.matches("^#[a-zA-Z0-9]+$"))
      .map((_, "0.00"))

  // TODO updateStateByKey
  // see http://spark.apache.org/docs/latest/streaming-programming-guide.html#updatestatebykey-operation

}

object TweetMap {

  // Instantiate Spark
  val sc = AppUtil.sc("Tweet Map")

  val totalTweets = sc.accumulator(0, "Total Tweets")

  def countTweets(newValues: Seq[Int], runningCount: Option[Int]): Option[Int] = {
//    val newCount = ...  // add the new values with the previous running count to get the new count
//    Some(newCount)
    Some(0) // TODO
  }

  def main(args: Array[String]) {

  }
}
