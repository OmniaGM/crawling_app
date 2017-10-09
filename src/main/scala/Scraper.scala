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

  def allExtractor(html: Element, domain: URI.Domain): (Option[Base], Set[Asset], Set[Link]) = {
    val elements = html.getAllElements.asScala
    elements.foldLeft[(Option[Base], Set[Asset], Set[Link])]((None, Set.empty, Set.empty)) {
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
              .filter(uri => Http.isInternal(uri, domain) )
              .map(Link)
            (maybeBase, assets, internalLinks ++ maybeLink)
          case _ => (maybeBase, assets, internalLinks)
        }
    }
  }

}
