package streaming

import com.holdenkarau.spark.testing.StreamingSuiteBase
import org.apache.spark.streaming.Seconds
import org.json.JSONObject
import twitter4j.Status
import twitter4j.json.DataObjectFactory
;

class PopularTweetsTest extends StreamingSuiteBase {

  test("single hashtag transformation") {
    verify(List(List(status("#a"))), List(List(("#a", 1))))
  }

  test("single tweet, two hashtags: same count, lexical order applies") {
    verify(List(List(status("#a #b"))), List(List(("#a", 1), ("#b", 1))))
  }

  test("two hashtags: same count, lexical order applies") {
    verify(List(List(status("#a"), status("#b"))), List(List(("#a", 1), ("#b", 1))))
  }

  private def verify(input: List[List[Status]], expected: List[List[(String, Int)]]): Unit = {
    testOperation[Status, (String, Int)](input, popularTweets().transform _, expected, useSet = true)
  }

  private def popularTweets(): PopularTweets = new PopularTweets(Seconds(60 * 2), Seconds(1))

  private def status(text: String): Status = {
    val json = new JSONObject().put("text", text)
    val status = DataObjectFactory.createStatus(json.toString)
    status
  }
}
