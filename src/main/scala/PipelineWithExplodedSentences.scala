import com.johnsnowlabs.nlp.{DocumentAssembler, EmbeddingsFinisher}
import com.johnsnowlabs.nlp.annotators.Tokenizer
import com.johnsnowlabs.nlp.annotators.sbd.pragmatic.SentenceDetector
import com.johnsnowlabs.nlp.embeddings.{BertEmbeddings, SentenceEmbeddings}
import org.apache.spark.ml.Pipeline

object PipelineWithExplodedSentences {

  val documentAssembler = new DocumentAssembler()
    .setInputCol("text")
    .setOutputCol("document")
  val sentenceDetector = new SentenceDetector()
    .setInputCols(Array("document"))
    .setOutputCol("sentence")
    .setExplodeSentences(true)
  val regexTokenizer = new Tokenizer()
    .setInputCols("sentence")
    .setOutputCol("token")
  val embeddings = BertEmbeddings.load("src/main/resources/models/bert_base_uncased_en_2.4.0_2.4_1580579889322/")
    .setInputCols("sentence","token")
    .setOutputCol("bert_embeddings")
    .setPoolingLayer(0)

  val pipeline = new Pipeline()
    .setStages(Array(
      documentAssembler,
      sentenceDetector,
      regexTokenizer,
      embeddings
    ))

}
