### Components 
- **URI:** It's a parser for the 5 different types of the url and generate the absolute url for them so we can use it later to get the content of the page. 
     * URL Types are    
        1. Absolute URL example: `href="http://example.com/page/"`
        2. Relative URL examples: `href="t-shirts/t-shirt-life-is-good/"` or `href="../../"`
        3. Protocol-relative URL example: `href="//example.com/page/"`
        4. Root-relative URL example: `href="/page/1"`
        5. Scraper.Base-relative URL (home-page-relative) example: `href="post/post1/"`

- **Scraper:** To get html for any url, parse it and extract all internal links and assets
    
- **Crawler:** Build the sitemap, which is map between link and it's deps (assets and linked pages)
    * the crawler get the sitmap for every page and it's linked pages in order to speed performance

### Run the crawling app
To run the app 
```sbtshell
sbt run <protocol>://<host>>
```

###Test
Then run 
```sbtshell
cd crawling_app
sbt test
```