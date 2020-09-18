name := "spark-nlp-sentence-embeddings"
version := "0.1"
scalaVersion := "2.11.12"


// Dependencies for Spark
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.6"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.6"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.6" //% "runtime"

// Dependencies for SparkNLP
libraryDependencies += "com.johnsnowlabs.nlp" %% "spark-nlp" % "2.5.5"

// Dependencies for Scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test"
libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.0" % "test"
