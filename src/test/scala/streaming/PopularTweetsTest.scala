package streaming

import com.holdenkarau.spark.testing.StreamingSuiteBase
import org.apache.spark.streaming.Seconds
import org.json.JSONObject
import twitter4j.Status
import twitter4j.json.DataObjectFactory
;

class PopularTweetsTest extends StreamingSuiteBase {

  test("sanity check") {
    verify(List(List(status("#a"))), List(List(("#a", 1))))
  }

  test("single tweet, two hashtags: same count, lexical order applies") {
    verify(List(List(status("#a #b"))), List(List(("#a", 1), ("#b", 1))))
  }

  test("two hashtags: same count, lexical order applies") {
    verify(List(List(status("#b"), status("#a"))), List(List(("#a", 1), ("#b", 1))))
  }

  test("same hashtag multiple times") {
    verify(List(List(status("#a"), status("#a"), status("#a"))), List(List(("#a", 3))))
  }

  test("multiple hashtags, top counts first, lexical order next") {
    verify(List(List(status("#a"), status("#b #a"), status("#a"), status("#d"), status("#b #c"), status("#c"))),
      List(List(("#a", 3), ("#b", 2), ("#c", 2), ("#d", 1))))
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
