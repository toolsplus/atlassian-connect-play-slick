package io.toolsplus.atlassian.connect.play.slick.fixtures

import io.toolsplus.atlassian.connect.play.api.models.DefaultAtlassianHost
import io.toolsplus.atlassian.connect.play.slick.generators.AtlassianHostGen

trait AtlassianHostFixture extends AtlassianHostGen {
  val defaultHost: DefaultAtlassianHost = DefaultAtlassianHost(
    "a890cfe7-3518-3920-b0b5-6fa412a7f3d4",
    "io.toolsplus.atlassian.connect.play.scala.seed",
    None,
    Some(
      "ari:cloud:ecosystem::installation/e786c5a3-819e-43cd-9cc9-d38a470a5df6"
    ),
    "LkbauUXN71J8jxRi9Nbf+8dwGtXxqta+Fu6k86aF+0IIzxkZ/GlggElYVoCqQg",
    "https://example.atlassian.net",
    "https://example.atlassian.net",
    "https://example.atlassian.net",
    "jira",
    "Atlassian JIRA at https://example.atlassian.net",
    None,
    None,
    None,
    installed = true,
    ttl = None
  )
  val host: DefaultAtlassianHost =
    atlassianHostGen.sample.getOrElse(defaultHost)
}
