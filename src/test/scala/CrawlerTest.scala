
import helper.LocalhostClient
import scraper._
import uri._
import org.scalatest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

class CrawlerTest extends FlatSpec with Matchers with BeforeAndAfterAll {
  val crawler = new Crawler()
  val localHttpServer = LocalhostClient.server

  override def beforeAll {
    localHttpServer.start
  }

  override def afterAll = {
    localHttpServer.stop
    crawler.close()
  }

  "Crawler" should "get sitemap of the page" in {
    LocalhostClient.stubServer

    val http = "http"
    val domain = s"${LocalhostClient.Host}:${LocalhostClient.Port}"

    val rootURI = URI.AbsoluteURI(http, domain, Seq.empty, None, None)

    val sitemap = Await.result(crawler.crawling(rootURI), 5 second)

    sitemap.size shouldBe(4)

    sitemap should contain key(rootURI)
    sitemap should contain value (
      Set(  URI.AbsoluteURI(http, domain, Seq(), Some("test1"), None),
            URI.AbsoluteURI(http, domain, Seq(), Some("test2"), None)),
      Set(  Asset(LocalhostClient.mainScriptAssetURL),
            Asset(LocalhostClient.mainCSSAssetURL))
    )
  }

}
