import akka.actor.{Actor, ActorSystem}
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.{ExecutionContext, Future}

class Crawler {
  type SiteMap = Map[URI.AbsoluteURI, (Set[URI.AbsoluteURI], Set[Asset])]

  implicit val system = ActorSystem()
  implicit val actor = Actor
  implicit val materializer = ActorMaterializer()

  val wsClient = StandaloneAhcWSClient()

  def close()(implicit ec: ExecutionContext):Future[Unit] = {
    wsClient.close()
    system.terminate().map(_ => ())
  }

  def fetch(uri: URI.AbsoluteURI)(implicit ec: ExecutionContext): Future[(Set[URI.AbsoluteURI], Set[Asset])] = {
    Scraper.getHTML(uri.toURL(), wsClient).map { body =>
      val html = Scraper.parseHtml(body)
      val (maybeBase, assets, newLinksToVisit) = Scraper.allExtractor(html, uri.domain)
      val base =
        maybeBase
          .map(base => URI.absolutify(base.uri, uri))
          .getOrElse(uri)

      val newUrisToVisit = newLinksToVisit
        .map(link => URI.absolutify(link.uri, base))

      (newUrisToVisit, assets)
    }
  }

  def crawling(uri: URI.AbsoluteURI)(implicit ec: ExecutionContext): Future[SiteMap] = {
    val domain = uri.domain

    def _rec(visitedLinks: Set[URI.AbsoluteURI],
             toVisitLinks: Set[URI.AbsoluteURI],
             currentSitemap: SiteMap = Map.empty): Future[SiteMap] = {

      val unVistedLinks = toVisitLinks -- visitedLinks

      if (unVistedLinks.isEmpty) Future.successful(currentSitemap)
      else {
        val link = unVistedLinks.head
        fetch(link).flatMap {
          case (newUrisToVisit, assets) =>
            _rec(
              visitedLinks + link,
              unVistedLinks.tail ++ newUrisToVisit -- visitedLinks,
              currentSitemap + (link -> (newUrisToVisit, assets))
            )
        }
      }

    }

    _rec(Set.empty[URI.AbsoluteURI], Set(uri))
  }
}