import org.scalatest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

class CrawlerTest extends FlatSpec with Matchers with BeforeAndAfterAll {
  val crawler = new Crawler()

  override def afterAll = {
    crawler.close()
  }

  "Crawler" should "get sitemap of the page" in {
    val http = "http"
    val domain = "localhost:8080"
    val rootURI = URI.AbsoluteURI(http, domain, Seq.empty, None, None)

    val sitemap = Await.result(crawler.crawling(rootURI), 5 second)

    sitemap.size shouldBe(4)

    sitemap should contain key(rootURI)
    sitemap should contain value (
      Set(  URI.AbsoluteURI(http, domain, Seq(), Some("test1"), None),
            URI.AbsoluteURI(http, domain, Seq(), Some("test2"), None)),
      Set(  Asset("https://code.jquery.com/jquery-1.11.3.js"),
            Asset("https://jquery.com/jquery-wp-content/themes/jquery/css/base.css?v=1"))
    )
  }

}
