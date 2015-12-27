name := "spark-by-example"

version := "1.0"

scalaVersion := "2.11.7"

def sparkVersion = "1.5.2"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided"
libraryDependencies += "org.apache.spark" %% "spark-streaming-twitter" % sparkVersion
libraryDependencies += "com.holdenkarau" %% "spark-testing-base" % "1.5.1_0.2.1" % "test"

/** Assembly Plugin Configuration **/

// Skip tests when packaging
test in assembly := {}

// Conflicting path resolution
assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "spark", "unused", xs @ _*)         => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}