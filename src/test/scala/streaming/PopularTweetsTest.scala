package streaming

import com.holdenkarau.spark.testing.StreamingSuiteBase
import org.apache.spark.streaming.Seconds
import org.json.JSONObject
import twitter4j.Status
import twitter4j.json.DataObjectFactory
;

class PopularTweetsTest extends StreamingSuiteBase {

  test("sanity check") {
    verify(List(List(status("#a"))), List(List(("#a", 1L))))
  }

  test("single tweet, two hashtags: same count, lexical order applies") {
    verify(List(List(status("#a #b"))), List(List(("#a", 1L), ("#b", 1L))))
  }

  test("two hashtags: same count, lexical order applies") {
    verify(List(List(status("#b"), status("#a"))), List(List(("#a", 1L), ("#b", 1L))))
  }

  test("same hashtag multiple times") {
    verify(List(List(status("#a"), status("#a"), status("#a"))), List(List(("#a", 3L))))
  }

  test("multiple hashtags, top counts first, lexical order next") {
    verify(List(List(status("#a"), status("#b #a"), status("#a"), status("#d"), status("#b #c"), status("#c"))),
      List(List(("#a", 3L), ("#b", 2L), ("#c", 2L), ("#d", 1L))))
  }

  private def verify(input: List[List[Status]], expected: List[List[(String, Long)]]): Unit = {
    testOperation[Status, (String, Long)](input, popularTweets().transform _, expected, useSet = true)
  }

  private def popularTweets() = new PopularTweets(Seconds(60 * 2), Seconds(1))

  private def status(text: String): Status = {
    val json = new JSONObject().put("text", text)
    val status = DataObjectFactory.createStatus(json.toString)
    status
  }
}
