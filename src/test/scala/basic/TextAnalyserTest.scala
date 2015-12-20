package basic

import com.holdenkarau.spark.testing.SharedSparkContext
import org.scalatest.FunSuite

class TextAnalyserTest extends FunSuite with SharedSparkContext {

  // Fun with pangrams, see more at http://www.fun-with-words.com/pang_example.html
  val text = "The quick brown fox jumps over a lazy dog. Quick zephyrs blow, vexing daft Jim. Quick brown fox and zephyrs ..."

  test("Character count") {
    assertResult(text.length)(analyse.totalChars)
  }

  test("Word count") {
    assertResult(20)(analyse.totalWords) // as in words in the 'text' constant
  }

  test("Top N words, lexical order applies if two or more words have the same frequency") {
    assertResult(Seq(("quick", 3), ("brown", 2), ("fox", 2), ("zephyrs", 2)))(analyse.mostFrequentWords)
  }

  test("Common words are available via broadcast") {
    val analyser = new TextAnalyser(sc, 4)
    assertResult(TextAnalyser.loadCommonWords())(analyser._commonWords.value)
  }
  private def analyse: TextStats =  {
    new TextAnalyser(sc, 4).analyse(sc.makeRDD(Seq(text)))
  }
}
