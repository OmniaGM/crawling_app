package scraper

import helper._
import uri._
import akka.actor.{Actor, ActorSystem}
import akka.stream.ActorMaterializer
import org.jsoup.Jsoup
import org.scalatest._
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration._

class ScraperTest extends FlatSpec with Matchers  with BeforeAndAfterAll {

  "Scraper" should "get html of the page" in {
    val localHttpServer = LocalhostClient.server
    localHttpServer.start
    LocalhostClient.stubIndex

    val url = s"http://${LocalhostClient.Host}:${LocalhostClient.Port}/"

    val (system: ActorSystem, wsClient: StandaloneAhcWSClient) = setupWSclient

    val html =  Await.result(Scraper.getHTML(url, wsClient), 10 second)
    html should include("<head>")
    html should include("<body")

    wsClient.close()
    localHttpServer.stop

    Await.result(system.terminate(), 1 second)
  }

  it should "parse html to return only the body element" in {
    val html = """<html>
                 |<head>
                 |<title>your title here</title>
                 |</head>
                 |<body>
                 |test
                 |</body>
                 |</html>"""
    val el = Scraper.parseHtml(html)

    el.children().size() shouldBe(1)
    el.child(0).tagName shouldBe("html")
  }


  it should "extract base link" in {
    val html = Jsoup.parse("""<html>
                             |<head>
                             |    <base href="http://example.com/x/"/>
                             |</head>
                             |</html>""")

    val (base, _, _) = Scraper.allExtractor(html, domain = "http://example.com")
    val baseUri = base.get.uri.asInstanceOf[URI.AbsoluteURI]
    baseUri.scheme shouldBe "http"
    base.get.uri.asInstanceOf[URI.AbsoluteURI].domain shouldBe "example.com"
    base.get.uri.asInstanceOf[URI.AbsoluteURI].path shouldBe Seq("x")
  }

  it should "extract assets links from the page" in {
    val html = Jsoup.parse("""<html>
                 |<head>
                 |    <link href="http://example.com/stylex.css"/>
                 |</head>
                 |</html>""")

    val (_, assets, _) = Scraper.allExtractor(html, domain = "http://example.com")
    assets.size shouldBe(1)
    assets should contain (Asset("http://example.com/stylex.css"))
  }

  it should "extract scripts from the page" in {
    val html = Jsoup.parse("""<html>
                             |<head>
                             |    <script src="http://example.com/js.js"></script>
                             |</head>
                             |</html>""")

    val (_, assets, _) = Scraper.allExtractor(html, domain = "http://example.com")
    assets.size shouldBe(1)
    assets should contain (Asset("http://example.com/js.js"))
  }

  it should "extract images from the page" in {
    val html = Jsoup.parse("""<html>
                             |<body>
                             |<img src="http://example.com/img.png"/>
                             |</body>
                             |</html>""")

    val (_, assets, _) = Scraper.allExtractor(html, domain = "http://example.com")
    assets.size shouldBe(1)
    assets should contain (Asset("http://example.com/img.png"))
  }

  it should "extract all assets in the page e.g images, links and scripts" in {
    val html = Jsoup.parse("""<html>
                             |<head>
                             |    <link href="http://example.com/stylex.css"/>
                             |    <script src="http://example.com/js.js"></script>
                             |</head>
                             |<body>
                             |<img src="http://example.com/img.png"/>
                             |</body>
                             |</html>""")

    val (_, assets, _) = Scraper.allExtractor(html, domain = "http://example.com")
    assets.size shouldBe(3)
    assets should contain (Asset("http://example.com/stylex.css"))
    assets should contain (Asset("http://example.com/js.js"))
    assets should contain (Asset("http://example.com/img.png"))
  }

  it should "extract anchor links from the page" in {
    val html = Jsoup.parse("""<html>
                             |<body>
                             |    <a href="/internal/">internal</a>
                             |    <a href="https://external.com">external</a>
                             |    <a href="http://google.com">internal</a>
                             |</body>
                             |</html>""")

    val (_, _, links) = Scraper.allExtractor(html, domain = "google.com")
    links.size shouldBe(2)
    val rootRelativeuri = links.toSeq(0).uri.asInstanceOf[URI.RootRelativeURI]
    rootRelativeuri.path shouldBe Seq("internal")
    val absoluteURI = links.toSeq(1).uri.asInstanceOf[URI.AbsoluteURI]
    absoluteURI.toURL shouldBe ("http://google.com")
  }

  it should "extract all assets and anchor links from the page" in {
    val html = Jsoup.parse("""<html>
                             |<head>
                             |    <link href="http://example.com/stylex.css"/>
                             |    <script src="http://example.com/js.js"></script>
                             |</head>
                             |<body>
                             |    <img src="http://example.com/img.png"/>
                             |    <a href="/internal/">internal</a>
                             |    <a href="https://example.com">external</a>
                             |    <a href="http://google.com">internal</a>
                             |</body>
                             |</html>""")

    val (_, assets, links) = Scraper.allExtractor(html, domain = "google.com")

    assets.size shouldBe(3)
    assets should contain (Asset("http://example.com/stylex.css"))
    assets should contain (Asset("http://example.com/js.js"))
    assets should contain (Asset("http://example.com/img.png"))

    links.size shouldBe(2)
    val rootRelativeuri = links.toSeq(0).uri.asInstanceOf[URI.RootRelativeURI]
    rootRelativeuri.path shouldBe Seq("internal")
    val absoluteURI = links.toSeq(1).uri.asInstanceOf[URI.AbsoluteURI]
    absoluteURI.toURL shouldBe ("http://google.com")
  }
  def setupWSclient = {
    implicit val system = ActorSystem()
    implicit val actor = Actor
    implicit val materializer = ActorMaterializer()

    val wsClient = StandaloneAhcWSClient()
    (system, wsClient)
  }
}

