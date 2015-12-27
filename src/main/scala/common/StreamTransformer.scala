package common

import org.apache.spark.streaming.dstream.DStream

trait StreamTransformer[S, T] {

  def transform(stream: DStream[S]): DStream[T]
}
