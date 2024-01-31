package io.toolsplus.atlassian.connect.play.slick

import io.toolsplus.atlassian.connect.play.slick.generators.AtlassianHostGen
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.{ClassLoaderEvolutionsReader, Evolutions}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

trait TestSpec
    extends PlaySpec
    with ScalaCheckDrivenPropertyChecks
    with FutureAwaits
    with DefaultAwaitTimeout
    with AtlassianHostGen
    with GuiceOneAppPerTest {

  override def fakeApplication(): Application = {
    val config = TestData.configuration
    GuiceApplicationBuilder(configuration = config).build()
  }

  def withEvolutions[T](block: => T): T =
    Evolutions.withEvolutions(
      dbApi.database("default"),
      ClassLoaderEvolutionsReader.forPrefix("evolutions/")) {
      block
    }

  def dbApi(implicit app: Application): DBApi =
    Application.instanceCache[DBApi].apply(app)
}
