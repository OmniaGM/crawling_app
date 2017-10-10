import org.scalatest._

class NetTest extends FlatSpec with Matchers  {
  "Http" should "check if the uri is internal if it's absolute url" in {

    Net.isInternal(
      URI.AbsoluteURI("http", "www.google.com", Seq(), None, None),
      "www.google.com") shouldBe true
  }

  it should "check if the uri is external uri" in {

    Net.isInternal(
      URI.AbsoluteURI("http", "www.example.com", Seq(), None, None),
      "www.google.com") shouldBe false
  }

  it should "check if the uri is internal if the uri is protocol url" in {

    Net.isInternal(
      URI.ProtocolRelativeURI("www.google.com", Seq(), None, None),
      "www.google.com") shouldBe true
  }

  it should "fetch any not absolute / protocol url type as internal" in {

    Net.isInternal(
      URI.RelativeURI(Seq("t"), Some("test"), None)
      , "www.google.com") shouldBe true
  }


}
