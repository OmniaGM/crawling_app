import org.scalatest._

class HttpTest extends FlatSpec with Matchers  {
  "Http" should "split url to protocol" in {
    val url = "http://example.com:80/test"

    val (protocol, host, port, path) = Http.splitURL(url)

    protocol shouldBe("http")
    host shouldBe("example.com")
    port shouldBe(80)
    path shouldBe("/test")
  }

  it should "split base url from any url" in {
    val url = "http://example.com:80/test"

    Http.baseURL(url) shouldBe("http://example.com:80")
  }

  it should "reformat wrong url" in {
    val baseUrl = "http://example.com:80"
    val url = "/test"
    Http.reformateUrl(baseUrl, url) shouldBe("http://example.com:80/test")
  }

  it should "reformat remove any params from the url" in {
    val baseUrl = "http://example.com:80"
    val url = "http://example.com:80/test?c=ab"
    Http.reformateUrl(baseUrl, url) shouldBe("http://example.com:80/test")
  }



}
