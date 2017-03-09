package io.toolsplus.atlassian.connect.play.slick

import play.api.Configuration

trait WithReferenceConfig {
  val ref = Configuration.reference
  def enabledModules(c: Configuration): List[String] = {
    import scala.collection.JavaConverters._
    ref.getStringList("play.modules.enabled") match {
      case None => Nil
      case Some(jlist) => jlist.asScala.toList
    }
  }
}
