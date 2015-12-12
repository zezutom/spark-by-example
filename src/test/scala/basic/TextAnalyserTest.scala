package basic

import com.holdenkarau.spark.testing.SharedSparkContext
import org.scalatest.FunSuite

class TextAnalyserTest extends FunSuite with SharedSparkContext {

  // Fun with pangrams, see more at http://www.fun-with-words.com/pang_example.html
  val text = "The quick brown fox jumps over a lazy dog. Quick zephyrs blow, vexing daft Jim. Quick brown fox and zephyrs ..."

  test("Character count") {
    assert(analyse.totalChars === text.length)
  }

  test("Word count") {
    assert(analyse.totalWords === 20) // as in words in the 'text' constant
  }

  test("Top N words, lexical order applies if two or more words have the same frequency") {
    assert(analyse.mostFrequentWords === Seq(("quick", 3), ("brown", 2), ("fox", 2), ("zephyrs", 2)))
  }

  private def analyse: TextStats =  {
    new TextAnalyser(sc, 4).analyse(sc.makeRDD(Seq(text)))
  }
}
