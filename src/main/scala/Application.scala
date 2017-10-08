object Application extends App{
  override def main(args: Array[String]) = {
      if (args.length == 0) {
        println("enter root url, should start with valid protocol")
      }
      val url = args(0)

      println(s"Crawling $url ... ")
      val siteMap = Crawler.crawling(url)

      println("Found " + siteMap.size + " internal page")

      for ((page,(internalLinks, assets)) <- siteMap) {
        val assetsSrc = assets.map(_.src)
        val linkedPages = internalLinks.map(_.url)

        printf(s"Page: %s \n =========== \n", page.url)
        printf("      ===>> depend on assets \n")
        assetsSrc.map( printf(s"            %s\n", _))
        printf("      ===>> and linked to: \n")
        linkedPages.map( printf(s"            %s\n", _))
      }
  }
}
