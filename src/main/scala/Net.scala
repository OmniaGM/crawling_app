object Net {
  type URL = String

  def isInternal(uri: URI, mainDomain: URI.Domain): Boolean = uri match {
    case URI.AbsoluteURI(_, domain, _, _, _) => domain == mainDomain
    case URI.ProtocolRelativeURI(domain, _,_,_) => domain == mainDomain
    case _ => true
  }
}
