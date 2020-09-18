import org.apache.spark.ml.linalg.{DenseVector, Vectors}
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{explode, udf}

class EmbeddingPipelineSpec extends UnitSpec with SharedSparkContext {

  /** Helper Function from:
   *  https://nlp.johnsnowlabs.com/docs/en/annotators#sentenceembeddings
   */
  val convertToVectorUDF: UserDefinedFunction = udf((matrix : Seq[Float]) => {
    Vectors.dense(matrix.toArray.map(_.toDouble))
  })

  /** The Body of this Function was basically taken from the com.johnsnowlabs.nlp.embeddings.SentenceEmbeddings class:
   *  https://github.com/JohnSnowLabs/spark-nlp/blob/master/src/main/scala/com/johnsnowlabs/nlp/embeddings/SentenceEmbeddings.scala#L88-L96
   */
  val averageWordEmbeddingsUDF : UserDefinedFunction = udf((matrix : Seq[Seq[Float]]) => {
    val res = Array.ofDim[Float](matrix(0).length)
    matrix(0).indices.foreach {
      j =>
        matrix.indices.foreach {
          i =>
            res(j) += matrix(i)(j)
        }
        res(j) /= matrix.length
    }
    Vectors.dense(res.map(_.toDouble))
  })

  /** Test for Evaluating the results from the SentenceEmbeddings Annotator
   *
   *  df1 Represents the Sentence Embeddings calculated using the SentenceEmbeddings Annotator.
   *  df2 Represents the Embeddings calculated by exploding the Sentences using the SentenceDetector Annotator and then
   *  manually averaging the respective Word-Embeddings
   */
  "SentenceEmbeddings Annotator" should "return same result as SentenceDetector with setExplodeSentences" in {

    val document = "It was the White Rabbit, trotting slowly back again. Oh my dear paws! Oh my fur and whiskers!"

    val sqlContext = spark.sqlContext
    import sqlContext.implicits._

    val data = Seq(document).toDF("text")

    val df1 = PipelineWithSentenceEmbeddingsAnnotator.pipeline.fit(data).transform(data)
      .select(explode($"sentence_embeddings.embeddings").as("sentence_embedding"))
      .withColumn("features", convertToVectorUDF($"sentence_embedding"))
      .select("features")

    df1.show(truncate = false)

    val df2 = PipelineWithExplodedSentences.pipeline.fit(data).transform(data)
      .withColumn("features", averageWordEmbeddingsUDF($"bert_embeddings.embeddings"))
      .select("features")

    df2.show(truncate = false)

    assert(df1.collect()(0).get(0).asInstanceOf[DenseVector].toArray sameElements df2.collect()(0).get(0).asInstanceOf[DenseVector].toArray)

  }

}
