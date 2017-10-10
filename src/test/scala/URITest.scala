import org.scalatest._
import scala.util.Success


class URITest extends FlatSpec with Matchers {

  "URI" should "parse an absolute url with port" in {
    val maybeURI = URI.parseRaw("http://localhost:8080")

    maybeURI shouldBe Success(
      URI.AbsoluteURI("http", "localhost:8080", Seq(), None, None)
    )
  }

  it should "parse an absolute url to absolute uri with normal domain" in {
    val maybeURI = URI.parseRaw("http://www.google.com")

    maybeURI shouldBe Success(
      URI.AbsoluteURI("http", "www.google.com", Seq(), None, None)
    )
  }

  it should "parse an absolute url with query" in {
    val maybeURI = URI.parseRaw("http://localhost:8080/?x=m")

    maybeURI shouldBe Success(
      URI.AbsoluteURI("http", "localhost:8080", Seq(), None, Some("x=m"))
    )
  }

  it should "parse an absolute url with page" in {
    val maybeURI = URI.parseRaw("http://localhost:8080/test")

    maybeURI shouldBe Success(
      URI.AbsoluteURI("http", "localhost:8080", Seq(), Some("test"), None)
    )
  }

  it should "parse an absolute url with path" in {
    val maybeURI = URI.parseRaw("http://localhost:8080/t/test")

    maybeURI shouldBe Success(
      URI.AbsoluteURI("http", "localhost:8080", Seq("t"), Some("test"), None)
    )
  }

  it should "parse an absolute url" in {
    val maybeURI = URI.parseRaw("http://localhost:8080/t/test?x=m")

    maybeURI shouldBe Success(
      URI.AbsoluteURI("http", "localhost:8080", Seq("t"), Some("test"), Some("x=m"))
    )
  }

  it should "parse a protocol url with protocol" in {
    val maybeURI = URI.parseRaw("//localhost:8080/")

    maybeURI shouldBe Success(
      URI.ProtocolRelativeURI("localhost:8080", Seq(), None, None)
    )
  }

  it should "parse a protocol url with normal domain" in {
    val maybeURI = URI.parseRaw("//example.com/")

    maybeURI shouldBe Success(
      URI.ProtocolRelativeURI("example.com", Seq(), None, None)
    )
  }

  it should "parse a protocol url with page " in {
    val maybeURI = URI.parseRaw("//localhost:8080/test")

    maybeURI shouldBe Success(
      URI.ProtocolRelativeURI("localhost:8080", Seq(), Some("test"), None)
    )
  }

  it should "parse a protocol url with path" in {
    val maybeURI = URI.parseRaw("//localhost:8080/t/test")

    maybeURI shouldBe Success(
      URI.ProtocolRelativeURI("localhost:8080", Seq("t"), Some("test"), None)
    )
  }

  it should "parse a protocol url with query" in {
    val maybeURI = URI.parseRaw("//localhost:8080/?x=m")

    maybeURI shouldBe Success(
      URI.ProtocolRelativeURI("localhost:8080", Seq(), None, Some("x=m"))
    )
  }

  it should "parse a protocol url" in {
    val maybeURI = URI.parseRaw("//localhost:8080/t/test?x=m")

    maybeURI shouldBe Success(
      URI.ProtocolRelativeURI("localhost:8080", Seq("t"), Some("test"), Some("x=m"))
    )
  }

  it should "parse a relative url" in {
    val maybeURI = URI.parseRaw("t/test?x=m")

    maybeURI shouldBe Success(
      URI.RelativeURI(Seq("t"), Some("test"), Some("x=m"))
    )
  }

  it should "parse a relative url start with ../" in {
    val maybeURI = URI.parseRaw("../t/test?x=m")

    maybeURI shouldBe Success(
      URI.RelativeURI(Seq("..","t"), Some("test"), Some("x=m"))
    )
  }

  it should "parse a root-relative url start with" in {
    val maybeURI = URI.parseRaw("/t/test?x=m")

    maybeURI shouldBe Success(
      URI.RootRelativeURI(Seq("t"), Some("test"), Some("x=m"))
    )
  }

  it should "return same uri if your try to absolutify an absolute uri" in {
    val domain = "localhost:8080"
    val uri = URI.AbsoluteURI("http", domain, Seq("t"), Some("test2"), None)
    val base = URI.AbsoluteURI("http", domain, Seq(), Some("test"), None)

    URI.absolutify(uri, base) shouldBe(
      URI.AbsoluteURI("http", domain, Seq("t"), Some("test2"), None)
      )
  }

  it should "absolutify a protocol uri" in {
    val domain = "localhost:8080"
    val uri = URI.ProtocolRelativeURI(domain, Seq("t"), Some("test2"), None)
    val base = URI.AbsoluteURI("http", "example.com", Seq(), Some("test"), None)

    URI.absolutify(uri, base) shouldBe(
      URI.AbsoluteURI("http", domain, Seq("t"), Some("test2"), None)
      )
  }

  it should "absolutify a relative uri" in {
    val domain = "localhost:8080"
    val uri = URI.RelativeURI(Seq("t"), Some("test2"), None)
    val base = URI.AbsoluteURI("http", domain, Seq("t3"), Some("test"), None)

    URI.absolutify(uri, base) shouldBe(
      URI.AbsoluteURI("http", domain, Seq("t3", "t"), Some("test2"), None)
      )
  }

  it should "absolutify a root-relative uri" in {
    val domain = "localhost:8080"
    val uri = URI.RootRelativeURI(Seq("t"), Some("test2"), None)
    val base = URI.AbsoluteURI("http", domain, Seq("t3"), Some("test"), None)

    URI.absolutify(uri, base) shouldBe(
      URI.AbsoluteURI("http", domain, Seq("t"), Some("test2"), None)
      )
  }
}
