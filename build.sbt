name := "spark-by-example"

version := "1.0"

scalaVersion := "2.11.7"

def sparkVersion = "1.5.2"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion
libraryDependencies += "com.holdenkarau" %% "spark-testing-base" % "1.5.1_0.2.1" % "test"