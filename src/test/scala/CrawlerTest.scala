import org.jsoup.Jsoup
import org.scalatest._

class CrawlerTest extends FlatSpec with Matchers  {
  "Crawler" should "get sitemap of the page" in {
    val rootURL = "http://localhost:8080/"
    val baseUrl = "http://localhost:8080"
    val sitemap = Crawler.crawling(rootURL)
    sitemap.size shouldBe(4)

    sitemap should contain key(Link(url = rootURL ))
    sitemap should contain value (
      Set(  Link(s"$baseUrl/test1"),
            Link(s"$baseUrl/test2")),
      Set(Asset("https://jquery.com/jquery-wp-content/themes/jquery/css/base.css?v=1"))
    )
  }

}
