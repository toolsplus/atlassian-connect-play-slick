package io.toolsplus.atlassian.connect.play.slick

import play.api.Configuration
import scala.jdk.CollectionConverters._

trait WithReferenceConfig {
  val ref: Configuration = Configuration.reference
  def enabledModules(c: Configuration): List[String] = {
    ref.underlying.getStringList("play.modules.enabled").asScala.toList
  }
}
