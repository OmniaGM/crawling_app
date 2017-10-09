import Http.URL

final case class Asset(src: URL) extends AnyVal
final case class Link(uri: URI) extends AnyVal
final case class Base(uri: URI) extends AnyVal