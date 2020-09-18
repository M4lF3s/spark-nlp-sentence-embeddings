import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.scalatest.{BeforeAndAfterAll, Suite}

/** This Interface provides a Local Spark Context for the Unit-Tests to use
 *
 */
trait SharedSparkContext extends BeforeAndAfterAll { self: Suite =>

  @transient private var _spark : SparkSession = _

  def spark : SparkSession = _spark

  var conf = new SparkConf(false)

  override def beforeAll() : Unit = {
    _spark = SparkSession.builder()
      .master("local[2]")
      .appName("test")
      .config(conf)
      .getOrCreate()
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    _spark.stop()
    _spark = null
    super.afterAll()
  }

}