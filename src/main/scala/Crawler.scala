object Crawler {
  type SiteMap = Map[URI.AbsoluteURI, (Set[URI.AbsoluteURI], Set[Asset])]

  def fetch(uri: URI.AbsoluteURI): (Set[URI.AbsoluteURI], Set[Asset]) = {
    val html = Scraper.parseHtml(Scraper.getHTML(uri.toURL()))
    val (maybeBase, assets, newLinksToVisit) = Scraper.allExtractor(html, uri.domain)
    val base =
      maybeBase
        .map(base => URI.absolutify(base.uri, uri))
        .getOrElse(uri)

    val newUrisToVisit = newLinksToVisit
      .map(link => URI.absolutify(link.uri, base))

    (newUrisToVisit, assets)

  }

  def crawling(uri: URI.AbsoluteURI): SiteMap = {
    val domain = uri.domain

    def _rec(visitedLinks: Set[URI.AbsoluteURI],
             toVisitLinks: Set[URI.AbsoluteURI],
             currentSitemap: SiteMap = Map.empty): SiteMap = {

      val unVistedLinks = toVisitLinks -- visitedLinks

      if (unVistedLinks.isEmpty) currentSitemap
      else {
        val link = unVistedLinks.head
        val (newUrisToVisit, assets) = fetch(link)
        
        _rec(
          visitedLinks + link,
          unVistedLinks.tail ++ newUrisToVisit -- visitedLinks,
          currentSitemap + (link -> (newUrisToVisit, assets))
        )
      }

    }

    _rec(Set.empty[URI.AbsoluteURI], Set(uri))
  }
}