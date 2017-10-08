import Http.URL

final case class Asset(src: URL) extends AnyVal
final case class Link(url: URL, baseUrl: URL = "")