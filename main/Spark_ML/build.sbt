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
  "org.apache.spark" %% "spark-mllib" % "3.0.1"
)

/*
libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core" % "2.3.2",
  "org.apache.spark" % "spark-sql" % "2.3.2"
)
libraryDependencies += "org.apache.spark" % "spark-sql" % "2.4.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "com.cloudera.sparkts" % "sparkts" % "0.4.0"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.0"

*/