package common

import org.apache.spark.rdd.RDD

trait Transformer[S, T] {

  def transform(rdd: RDD[S]): RDD[T]
}
