package helper

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping

object LocalhostClient {
  val Host = "localhost"

  val server = new MockServer(Host)
  val Port = server.port

  val mainScriptAssetURL =  "https://code.jquery.com/jquery-1.11.3.js"
  val mainCSSAssetURL = "https://jquery.com/jquery-wp-content/themes/jquery/css/base.css?v=1"

  private val mainAssets =
    s"""
      |<script src="$mainScriptAssetURL"></script>
      |<link rel="stylesheet" href="$mainCSSAssetURL">
    """.stripMargin


  private def buildHTML(body: String, moreHeadContent: String = "" ): String = {
    val head = mainAssets + moreHeadContent
    s"""
       |<html>
       |  <head>
       |   $head
       |  </head>
       |  <body>
       |  $body
       |  </body>
       |</html>
    """.stripMargin
  }

  private def stubPage(url: String, status: Int, body: String): StubMapping = {
    stubFor(get(urlEqualTo(url))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withBody(body)))
  }

  def stubIndex: StubMapping = {
    stubPage("/", 200, buildHTML("""
      |  <a href="/test1">1</a></br>
      |  <a href="/test2">2</a></br>
      |  <a href="https://www.twitter.com">twitter</a>
    """.stripMargin))
  }

  def stubServer: Seq[StubMapping] = {
    Seq(
      stubIndex,
      stubPage("/test1", 200, buildHTML(
        """
        |<a href="/test3">3</a></br>
        |  <a href="/test2#xx">2</a></br>
        |  <a href="https://www.facebook.com">fb</a>
      """.stripMargin)),
      stubPage("/test2", 200, buildHTML(
        """
          |  <a href="/test1">1</a></br>
          |  <a href="/test3">3</a></br>
          |  <a href="https://www.facebook.com">fb</a>
        """.stripMargin)),
      stubPage("/test3", 200, buildHTML(
      """
        |  <img src="http://scalatra.org/images/logo-x.png" />
        |  <a href="/test1">1</a></br>
        |  <a href="/">home</a></br>
        |  <a href="https://www.google.com">google</a>
      """.stripMargin))
    )
  }
}