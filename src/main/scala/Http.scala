import java.net.{URL => JavaUrl}

object Http {
  type URL = String

  def splitURL(url : URL) = {
    val URL = new JavaUrl(url)
    ( URL.getProtocol,
      URL.getHost,
      URL.getPort,
      URL.getPath
    )
  }

  def isInternal(uri: URI, mainDomain: URI.Domain): Boolean = uri match {
    case URI.AbsoluteURI(_, domain, _, _, _) => domain == mainDomain
    case URI.ProtocolRelativeURI(domain, _,_,_) => domain == mainDomain
    case _ => true
  }

  def hostUrl(url:URL): URL = {
    val (_, host, port, _) = Http.splitURL(url)
    val portFormat = if (port == -1) "" else ":" + port
    s"$host$portFormat"
  }

  def baseURL(url:URL): URL = {
    val (protocol, host, port, path) = splitURL(url)
    val portFormat = if (port == -1) "" else ":" + port
    s"$protocol://$host$portFormat"
  }

  def reformateUrl(domain:URL, url:URL): URL = {
    val newUrl = if (url.startsWith("/")) s"$domain$url" else url

    val (_, _, _, path) = splitURL(newUrl)
    s"$domain$path"

  }

}
