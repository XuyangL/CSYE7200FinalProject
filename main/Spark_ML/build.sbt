import sbt.Keys.libraryDependencies

name := "Analysis"

version := "0.1"

scalaVersion := "2.12.12"

resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven" withAllowInsecureProtocol(true)


libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/org.apache.spark/spark-sql
  "org.apache.spark" %% "spark-sql" % "3.0.1",

  // https://mvnrepository.com/artifact/org.apache.spark/spark-core
  "org.apache.spark" %% "spark-core" % "3.0.1",

  // https://mvnrepository.com/artifact/org.apache.spark/spark-hive
  "org.apache.spark" %% "spark-hive" % "3.0.1" % "provided",

  // https://mvnrepository.com/artifact/org.apache.spark/spark-mllib
  "org.apache.spark" %% "spark-mllib" % "3.0.1",

  // https://mvnrepository.com/artifact/org.scalatest/scalatest
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",

  // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core
  "org.apache.logging.log4j" % "log4j-core" % "2.4.1",

  // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api
  "org.apache.logging.log4j" % "log4j-api" % "2.4.1"
)