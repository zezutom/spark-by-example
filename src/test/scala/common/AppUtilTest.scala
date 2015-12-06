package common

import org.scalatest.FunSuite

class AppUtilTest extends FunSuite {

  test("should construct a HDFS path") {
    assert(AppUtil.hdfs("/var/log/test.txt") === "hdfs:///var/log/test.txt")
  }
}
