package io.toolsplus.atlassian.connect.play.slick

import javax.inject.Singleton

import io.toolsplus.atlassian.connect.play.api.repositories.AtlassianHostRepository
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject._
import play.api.{Configuration, Environment}

@Singleton
final class SlickModule extends Module {
  def bindings(environment: Environment,
               configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[AtlassianHostRepository].to[SlickAtlassianHostRepository]
    )
  }
}

trait SlickComponents {

  def dbConfigProvider: DatabaseConfigProvider

  lazy val hostRepository: AtlassianHostRepository =
    new SlickAtlassianHostRepository(dbConfigProvider)
}
