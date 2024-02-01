package io.toolsplus.atlassian.connect.play.slick

import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.scalatest.{TestData, TestSuite}
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import org.testcontainers.utility.DockerImageName
import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.{ClassLoaderEvolutionsReader, Evolutions}
import play.api.inject.guice.GuiceApplicationBuilder

trait PostgresContainerTest
    extends GuiceOneAppPerTest
    with TestContainerForAll { self: TestSuite =>

  val postgresVersion = "15.5"

  override val containerDef: PostgreSQLContainer.Def =
    PostgreSQLContainer.Def(
      DockerImageName.parse(s"postgres:$postgresVersion"),
      databaseName = "intercom",
      username = "test",
      password = "test",
    )

  override def newAppForTest(td: TestData): Application = withContainers {
    container =>
      GuiceApplicationBuilder()
        .configure(ContainerDbConfiguration.configuration(container))
        .build()
  }

  def dbApi(implicit app: Application): DBApi =
    Application.instanceCache[DBApi].apply(app)

  def withEvolutions[T](block: => T): T =
    Evolutions.withEvolutions(
      dbApi.database("default"),
      ClassLoaderEvolutionsReader.forPrefix("evolutions/")) {
      block
    }

}
