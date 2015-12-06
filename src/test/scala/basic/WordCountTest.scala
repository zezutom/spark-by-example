package basic

import com.holdenkarau.spark.testing.SharedSparkContext
import org.scalatest.FunSuite

class WordCountTest extends FunSuite with SharedSparkContext {

  val expected = List(
    ("a", 3),
    ("b", 1),
    ("c", 3),
    ("d", 2),
    ("e", 1)
  )

  test("Word counts should be lexically ordered") {
    verify(Seq("c d a b a e c c d a"))
  }

  test("All words should be transferred to lowercase") {
    verify(Seq("c D a B a E c C d A"))
  }

  test("All input lines should be considered") {
    verify(Seq("c D a B", "a E", "c C d", "A"))
  }

  test("Empty space and punctuation should be ignored") {
    verify(Seq("c, D; a! B", "a ; E", "c C,, d", ";,A !"))
  }

  private def verify(seq: Seq[String]): Unit = {
    assert(new WordCount().transform(sc.makeRDD(seq)).collect().toList === expected)
  }
}
