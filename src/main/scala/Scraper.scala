import play.api.libs.ws.ahc.StandaloneAhcWSClient
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}


object Scraper {

  def getHTML(url: Net.URL, wsClient: StandaloneAhcWSClient)(implicit ec: ExecutionContext): Future[String] = {
    wsClient.url(url).get().map { resp => resp.body }
  }

  def parseHtml(html:String): Element = {
    Jsoup.parseBodyFragment(html)
  }

  def allExtractor(html: Element, domain: URI.Domain): (Option[Base], Set[Asset], Set[Link]) = {
    html
      .getAllElements.asScala
      .foldLeft[(Option[Base], Set[Asset], Set[Link])]((None, Set.empty, Set.empty)) {
      case ((maybeBase, assets, internalLinks), element) =>
        element.tagName match {
          case "base" if element.attr("href").nonEmpty =>
            //ASSUMPTION: ignore if the html has 2 base tags which is invalid
            val maybeBase = URI.parseRaw(element.attr("href")).toOption.map(Base)
            (maybeBase, assets, internalLinks)
          case "link" if element.attr("href").nonEmpty =>
            (maybeBase, assets + Asset(element.attr("href")), internalLinks)
          case "script" | "img" if element.attr("src").nonEmpty =>
            (maybeBase, assets + Asset(element.attr("src")), internalLinks)
          case "a" =>
            val maybeLink =
              URI.parseRaw(element.attr("href")).toOption
              .filter(uri => Net.isInternal(uri, domain) )
              .map(Link)
            (maybeBase, assets, internalLinks ++ maybeLink)
          case _ => (maybeBase, assets, internalLinks)
        }
    }
  }

}
