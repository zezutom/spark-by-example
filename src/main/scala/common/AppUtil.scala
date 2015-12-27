package common

import java.nio.file.{Files, Paths}
import java.util.Properties

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder

import scala.io.Source

/**
  * Functionality common to most of the examples
  */
object AppUtil {

  def sparkConf(appName: String): SparkConf =
    new SparkConf().setMaster("local[2]").setAppName(appName)

  def sc(appName: String): SparkContext =
    new SparkContext(sparkConf(appName))

  def ssc(appName: String, seconds: Int): StreamingContext = {
    val ssc = new StreamingContext(sparkConf(appName), Seconds(seconds))
    ssc.checkpoint(".")
    ssc
  }

  def hdfs(path: String): String = s"hdfs://$path"

  /**
    * Twitter config:
    *
    * Don't put your Twitter credentials in the bundled config file (twitter.conf).
    * Instead, create a text file containing the Twitter API keys somewhere in your filesystem
    * and make it accessible via an environment variable (called TWITTER_CONF by default).
    *
    * @param confEnv      Environment variable containing a path to the config file
    * @param defaultPath  Default config shipped within the package
    * @return OAuth credentials
    */
  def twitterAuth(confEnv: String = "TWITTER_CONF", defaultPath: String = "/twitter.conf"): Option[OAuthAuthorization] = {

    val conf = new Properties()

    // Read the config from the filesystem, or fall back to the bundled one (not recommended, see above)
    System.getenv(confEnv) match {
      case path if path != null && Files.exists(Paths.get(path)) =>
        conf.load {Source.fromFile(path).bufferedReader()}
      case _ =>
        conf.load {getClass.getResourceAsStream(defaultPath)}
    }
    val authConf = new ConfigurationBuilder()
      .setDebugEnabled(true)
      .setOAuthConsumerKey(conf.getProperty("consumerKey"))
      .setOAuthConsumerSecret(conf.getProperty("consumerSecret"))
      .setOAuthAccessToken(conf.getProperty("accessToken"))
      .setOAuthAccessTokenSecret(conf.getProperty("accessTokenSecret"))
      .build()
    Some(new OAuthAuthorization(authConf))
  }
}
