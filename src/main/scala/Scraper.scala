import Http.URL
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import collection.JavaConverters._


object Scraper {
  def getHTML(url: URL): String = {
    Jsoup.connect(url).get().toString()
  }

  def parseHtml(html:String): Element = {
    Jsoup.parseBodyFragment(html)
  }
  private def isInternal(url: URL, baseUrl: URL): Boolean = {
    url.contains(baseUrl) || url.startsWith("/")
  }

  def allExtractor(html: Element, baseUrl: URL): (Set[Asset], Set[Link]) = {
    val elements = html.getAllElements.asScala
    elements.foldLeft((Set.empty[Asset], Set.empty[Link])) {
      case ((assets, internalLinks), element) =>
        element.tagName match {
          case "link" if element.attr("href").nonEmpty =>
            (assets + Asset(element.attr("href")), internalLinks)
          case "script" | "img" if element.attr("src").nonEmpty =>
            (assets + Asset(element.attr("src")), internalLinks)
          case "a" if isInternal(element.attr("href"), baseUrl) =>
            (assets, internalLinks + Link(Http.reformateUrl(baseUrl, element.attr("href")), baseUrl))
          case _ => (assets, internalLinks)
        }
    }
  }

}
