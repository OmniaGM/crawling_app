import Http.URL

object Crawler {
  type SiteMap = Map[Link, (Set[Link], Set[Asset])]

  private def hasBeenVisitedBefore(visitedBefore: Set[Link], url: URL): Boolean =
    visitedBefore.exists(page => page.url == url)

  def crawling(url: URL): SiteMap = {

    val root = Link(url = url)
    val baseUrl = Http.baseURL(url)


    def _rec(visitedLinks: Set[Link],
             toVisitLinks: Set[Link],
             currentSitemap: SiteMap = Map.empty): SiteMap = {

      val unVistedLinks = toVisitLinks -- visitedLinks

      if (unVistedLinks.isEmpty) currentSitemap
      else {
        if (unVistedLinks.head.url == "http://tomblomfield.com/archive/2016/1")
          println("debug me")

        val link = unVistedLinks.head
        val html = Scraper.parseHtml(Scraper.getHTML(link.url))

        val (assets, newLinksToVisit) = Scraper.allExtractor(html, baseUrl)

        _rec(
          visitedLinks + link,
          unVistedLinks.tail ++ newLinksToVisit -- visitedLinks,
          currentSitemap + (link -> (newLinksToVisit, assets))
        )
      }

    }

    _rec(Set.empty[Link], Set(root))
  }
}