import java.net.{URI => JavaURI}

import scala.util.Try

sealed trait URI

object URI {
  type Scheme = String
  type Domain = String
  type Segment = String
  type QueryString = String

  // <scheme>://<domain>/[...<path>]/[?<page>]
  final case class AbsoluteURI(
    scheme: Scheme,
    domain: Domain,
    path: Seq[Segment],
    page: Option[Segment],
    query: Option[QueryString]
  ) extends URI {

    def toURL(): Http.URL = {
      val schemeStr = s"$scheme://"
      val pathStr = path.map(s => s"/$s").mkString
      val pageStr = page.map(p => s"/$p").getOrElse("")
      val queryStr = query.map(q => s"?$q").getOrElse("")


      schemeStr + domain + pathStr + pageStr + queryStr
    }
  }

  final case class RelativeURI(
    path: Seq[Segment],
    page: Option[Segment],
    query: Option[QueryString]
  ) extends URI

  final case class ProtocolRelativeURI(
    domain: Domain,
    path: Seq[Segment],
    page: Option[Segment],
    query: Option[QueryString]
  ) extends URI

  final case class RootRelativeURI(
    path: Seq[Segment],
    page: Option[Segment],
    query: Option[QueryString]
  ) extends URI


  def parseRaw(rawUrl: String): Try[URI] =
    Try(new JavaURI(rawUrl)).map { uri =>
      val (segments, maybePage) = uri.getPath.split("/", -1).toSeq match {
        case Seq() => (Seq.empty[Segment], None)
        case segments :+ "" => (segments, None)
        case segments :+ page => (segments, Some(page))
      }

      ( Option(uri.getScheme),
        Option(uri.getHost),
        segments
      ) match {
        case ( Some(scheme), Some(host), Seq()) =>
          val domain: Domain =
            host + Option(uri.getPort).filter(_ > 0).map(p => s":$p").getOrElse("")
          AbsoluteURI(scheme, domain, Seq.empty, maybePage, Option(uri.getQuery))

        case ( Some(scheme), Some(host), "" +: path) =>
          val domain: Domain =
            host + Option(uri.getPort).filter(_ > 0).map(p => s":$p").getOrElse("")
          AbsoluteURI(scheme, domain, path, maybePage, Option(uri.getQuery))

        case ( None, Some(host), "" +: path) =>
          val domain: Domain =
            host + Option(uri.getPort).filter(_ > 0).map(p => s":$p").getOrElse("")
          ProtocolRelativeURI(domain, path, maybePage, Option(uri.getQuery))

        case ( None, None, "" +: path) =>
          RootRelativeURI(path, maybePage, Option(uri.getQuery))

        case ( None, None, path) =>
          RelativeURI(path, maybePage, Option(uri.getQuery))
      }
    }

  def absolutify(uri: URI, base: AbsoluteURI): AbsoluteURI = uri match {
    case uri: AbsoluteURI => uri
    case RelativeURI(Seq(), None, query) =>
      AbsoluteURI(base.scheme, base.domain, base.path, base.page, query.orElse(base.query))
    case RelativeURI(path, page, query) =>
      //no need to calculate path for something like ../.. as ws client handle it
      AbsoluteURI(base.scheme, base.domain, base.path ++ path, page, query)
    case ProtocolRelativeURI(domain, path, page, query) =>
      AbsoluteURI(base.scheme, domain, path, page, query)
    case RootRelativeURI(path, page, query) =>
      AbsoluteURI(base.scheme, base.domain, path, page, query)
  }
}
