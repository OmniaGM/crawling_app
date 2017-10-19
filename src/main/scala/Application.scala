import uri._
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends App{
  override def main(args: Array[String]) = {
    if (args.length == 0) {
      println("enter root url, should start with valid protocol")
    }
    val url = args(0)

    println(s"Crawling $url ... ")

    val uri: URI.AbsoluteURI = URI.parseRaw(url) match {
      case Success(x: URI.AbsoluteURI) => x
      case _ => sys.error("Invalid URL")
    }

    val crawler = new Crawler()
    crawler.crawling(uri).foreach { siteMap =>
      for ((page,(internalLinks, assets)) <- siteMap) {
        val assetsSrc = assets.map(_.src)
        val linkedPages = internalLinks.map(_.toURL)

        printf(s"Page: %s \n =========== \n", page.toURL)
        printf("      ===>> depend on assets \n")
        assetsSrc.map( printf(s"            %s\n", _))
        printf("      ===>> and linked to: \n")
        linkedPages.map( printf(s"            %s\n", _))
      }
      println("*** Found " + siteMap.size + " internal page ***")
      crawler.close()
    }
  }
}
