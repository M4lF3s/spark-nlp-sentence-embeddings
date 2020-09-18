import com.johnsnowlabs.nlp.DocumentAssembler
import com.johnsnowlabs.nlp.annotators.Tokenizer
import com.johnsnowlabs.nlp.annotators.sbd.pragmatic.SentenceDetector
import com.johnsnowlabs.nlp.embeddings.{BertEmbeddings, SentenceEmbeddings}
import org.apache.spark.ml.Pipeline

object PipelineWithSentenceEmbeddingsAnnotator {

  val documentAssembler = new DocumentAssembler()
    .setInputCol("text")
    .setOutputCol("document")
  val sentenceDetector = new SentenceDetector()
    .setInputCols(Array("document"))
    .setOutputCol("sentence")
  val regexTokenizer = new Tokenizer()
    .setInputCols("sentence")
    .setOutputCol("token")
  val embeddings = BertEmbeddings.load("src/main/resources/models/bert_base_uncased_en_2.4.0_2.4_1580579889322/")
    .setInputCols("sentence","token")
    .setOutputCol("embeddings")
    .setPoolingLayer(0)
  val embeddingsSentence = new SentenceEmbeddings()
    .setInputCols("sentence","embeddings")
    .setOutputCol("sentence_embeddings")
    .setPoolingStrategy("AVERAGE")

  val pipeline = new Pipeline()
    .setStages(Array(
      documentAssembler,
      sentenceDetector,
      regexTokenizer,
      embeddings,
      embeddingsSentence
    ))

}
