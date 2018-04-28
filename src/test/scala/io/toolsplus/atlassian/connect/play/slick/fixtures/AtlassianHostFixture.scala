package io.toolsplus.atlassian.connect.play.slick.fixtures

import io.toolsplus.atlassian.connect.play.api.models.StandardAtlassianHost
import io.toolsplus.atlassian.connect.play.slick.generators.AtlassianHostGen

trait AtlassianHostFixture extends AtlassianHostGen {
  val defaultHost = StandardAtlassianHost(
    "a890cfe7-3518-3920-b0b5-6fa412a7f3d4",
    "io.toolsplus.atlassian.connect.play.scala.seed",
    "MIGfMA0GCSqGDc10pQ4Xo+l/BaWhmiHXDDQ/tOjgfqaDxiXuIi/Jhk4D73aHbL9FwIDAQAB",
    None,
    "LkbauUXN71J8jxRi9Nbf+8dwGtXxqta+Fu6k86aF+0IIzxkZ/GlggElYVoCqQg",
    "100035",
    "1.2.35",
    "https://example.atlassian.net",
    "jira",
    "Atlassian JIRA at https://example.atlassian.net",
    None,
    installed = true
  )
  val host = atlassianHostGen.sample.getOrElse(defaultHost)
}
