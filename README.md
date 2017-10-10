###Design 
- **URI:** It's a parser for the 5 different types of the url and generate the absolute url for them so we can use it later to get the content of the page. 
     * URL Types are    
        1. Absolute URL example: `href="http://example.com/page/"`
        2. Relative URL examples: `href="t-shirts/t-shirt-life-is-good/"` or `href="../../"`
        3. Protocol-relative URL example: `href="//example.com/page/"`
        4. Root-relative URL example: `href="/page/1"`
        5. Base-relative URL (home-page-relative) example: `href="post/post1/"`

- **Scraper:** To get html for any url, parse it and extract all links and assets

- **Net:** Validate if the url is internal or not
    
- **Crawler:** Build the sitemap, which is map between link and it's deps (assets and linked pages)
    * the crawler get the sitmap for every page and it's linked pages in order to speed performance

### Run the crawling app
To run the app 
```sbtshell
sbt run <protocol>://<host>>
```

###Test
To run the test you will need first to run first **website_for_crawling_app**, it's simple web server for testing

to run **website_for_crawling_app** just run 
```sbtshell
cd website_for_crawling_app
sbt
jetty:start
```

Then run 
```sbtshell
cd crawling_app
sbt test
```