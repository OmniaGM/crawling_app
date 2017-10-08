###Design 
- **Scraper:** To get html for any url, parse it and extract all links and assets
- **Http:** Just a helper for reformatting urls and extract based url
- **Crawler:** Build the sitemap, which is map between link and it's deps (assets and linked pages)

### Run the crawling app
To run the app 
```sbtshell
sbt run <protocol>://<host>/>
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