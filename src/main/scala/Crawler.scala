import Http.URL

object Crawler {
  type SiteMap = Map[Link, (Set[Link], Set[Asset])]

  private def hasBeenVisitedBefore(visitedBefore: Set[Link], url: URL): Boolean =
    visitedBefore.exists(page => page.url == url)

  def crawling(url: URL): SiteMap = {
    def _rec(visitedLinks: Set[Link],
             toVisitLinks: Set[Link],
             currentSitemap: SiteMap = Map.empty): SiteMap = {

      if (toVisitLinks.isEmpty) currentSitemap
      else {
        val unVistedLinks = toVisitLinks -- visitedLinks
        val link = unVistedLinks.head
        val html = Scraper.parseHtml(Scraper.getHTML(link.url))

        val (assets, newLinksToVisit) = Scraper.allExtractor(html, link.baseUrl)

        _rec(
          visitedLinks + link,
          unVistedLinks.tail ++ newLinksToVisit -- visitedLinks,
          currentSitemap + (link -> (newLinksToVisit, assets))
        )
      }

    }

    val root = Link(url = url, baseUrl = Http.baseURL(url))
    _rec(Set.empty[Link], Set(root))
  }
}