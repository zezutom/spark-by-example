package common

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Functionality common to most of the examples
  */
object AppUtil {

  def sparkConf(appName: String): SparkConf =
    new SparkConf().setMaster("local[2]").setAppName(appName)

  def sc(appName: String): SparkContext =
    new SparkContext(sparkConf(appName))

  def hdfs(path: String): String = s"hdfs://$path"
}
