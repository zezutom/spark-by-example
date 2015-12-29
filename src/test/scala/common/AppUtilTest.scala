package common

import java.util.Properties

import common.AppUtil._
import org.scalatest.FunSuite

class AppUtilTest extends FunSuite {

  test("should load a default configuration") {
    verifyConf("/app.conf")
  }

  test("it should be possible to override the default configuration") {
    verifyConf("/app-test.conf", true)
  }

  test("should construct a HDFS path") {
    assert(AppUtil.hdfs("/var/log/test.txt") === "hdfs:///var/log/test.txt")
  }

  private def verifyConf(confPath: String, isOverride: Boolean = false): Unit = {
    val expected = new Properties()
    expected.load {AppUtil.getClass.getResourceAsStream(confPath)}

    val actual = if (isOverride) AppUtil.loadConf(defaultPath = confPath) else AppUtil.loadConf()
    val key = "master.url"

    assert(actual != null)
    assert(expected.get(key).equals(actual.getProperty(key)))
  }
}
