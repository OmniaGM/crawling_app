import org.scalatest._
import org.jsoup.Jsoup

class ScraperTest extends FlatSpec with Matchers  {
  "Scraper" should "get html of the page" in {
    val html = Scraper.getHTML("http://localhost:8080/")
    html should include("<head>")
    html should include("<body")
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

  it should "extract assets links from the page" in {
    val html = Jsoup.parse("""<html>
                 |<head>
                 |    <link href="http://example.com/stylex.css"/>
                 |</head>
                 |</html>""")

    val (assets, _) = Scraper.allExtractor(html, baseUrl = "http://example.com")
    assets.size shouldBe(1)
    assets should contain (Asset("http://example.com/stylex.css"))
  }

  it should "extract scripts from the page" in {
    val html = Jsoup.parse("""<html>
                             |<head>
                             |    <script src="http://example.com/js.js"></script>
                             |</head>
                             |</html>""")

    val (assets, _) = Scraper.allExtractor(html, baseUrl = "http://example.com")
    assets.size shouldBe(1)
    assets should contain (Asset("http://example.com/js.js"))
  }

  it should "extract images from the page" in {
    val html = Jsoup.parse("""<html>
                             |<body>
                             |<img src="http://example.com/img.png"/>
                             |</body>
                             |</html>""")

    val (assets, _) = Scraper.allExtractor(html, baseUrl = "http://example.com")
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

    val (assets, _) = Scraper.allExtractor(html, baseUrl = "http://example.com")
    assets.size shouldBe(3)
    assets should contain (Asset("http://example.com/stylex.css"))
    assets should contain (Asset("http://example.com/js.js"))
    assets should contain (Asset("http://example.com/img.png"))
  }

  it should "extract anchor links from the page" in {
    val html = Jsoup.parse("""<html>
                             |<body>
                             |    <a href="/internal/">internal</a>
                             |    <a href="https://example.com">external</a>
                             |    <a href="http://google.com">internal</a>
                             |</body>
                             |</html>""")

    val (_, links) = Scraper.allExtractor(html, baseUrl = "http://google.com")
    links.size shouldBe(2)
    links.toSeq(0).url shouldBe ("http://google.com/internal/")
    links.toSeq(1).url shouldBe ("http://google.com")
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

    val (assets, links) = Scraper.allExtractor(html, baseUrl = "http://google.com")

    assets.size shouldBe(3)
    assets should contain (Asset("http://example.com/stylex.css"))
    assets should contain (Asset("http://example.com/js.js"))
    assets should contain (Asset("http://example.com/img.png"))

    links.size shouldBe(2)
    links.toSeq(0).url shouldBe ("http://google.com/internal/")
    links.toSeq(1).url shouldBe ("http://google.com")
  }
}
