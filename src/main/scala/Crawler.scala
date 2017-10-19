import scraper._
import uri.URI
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
      val (maybeBase, assets, links) = scraper.Scraper.allExtractor(scraper.Scraper.parseHtml(body), uri.domain)
      val base =
        maybeBase
          .map(base => URI.absolutify(base.uri, uri))
          .getOrElse(uri)

      (links.map(link => URI.absolutify(link.uri, base)), assets)
    }
  }

  def crawling(uri: URI.AbsoluteURI)(implicit ec: ExecutionContext): Future[SiteMap] = {
    def _rec(visitedUris: Set[URI.AbsoluteURI],
             toVisitUris: Set[URI.AbsoluteURI],
             currentSitemap: SiteMap = Map.empty): Future[SiteMap] = {

      val unVistedLinks = toVisitUris -- visitedUris

      if (unVistedLinks.isEmpty) Future.successful(currentSitemap)
      else {
        Future.sequence(for (link <- unVistedLinks) yield {
          fetch(link).map {
            case (links, assets) =>
              (link -> (links, assets))
          }
        }).flatMap { stage =>
          val sitemap: SiteMap = stage.toMap
          val visited = visitedUris ++ sitemap.keys

          _rec(
            visited,
            sitemap.values.flatMap(_._1).to[Set] -- visited,
            currentSitemap ++ sitemap)
        }
      }
    }

    _rec(Set.empty[URI.AbsoluteURI], Set(uri))
  }
}