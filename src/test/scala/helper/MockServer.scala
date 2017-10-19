package helper

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit.WireMockRule

class MockServer(val host: String ) {

  def getPort:Int = {
    val wireMockRule = new WireMockRule(wireMockConfig.dynamicPort)
    wireMockRule.start()
    val port = wireMockRule.port
    wireMockRule.stop()

    port
  }

  val port = getPort

  val wireMockServer = new WireMockServer(wireMockConfig().port(port))

  def start = {
    wireMockServer.start()
    WireMock.configureFor(host, port)
  }

  def stop = wireMockServer.stop
}
