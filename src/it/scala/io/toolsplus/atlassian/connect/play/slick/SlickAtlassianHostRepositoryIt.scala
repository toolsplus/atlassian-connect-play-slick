package io.toolsplus.atlassian.connect.play.slick

import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import io.toolsplus.atlassian.connect.play.slick.fixtures.AtlassianHostFixture
import org.scalatest.TestData
import org.scalatest.concurrent.Eventually
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import org.testcontainers.utility.DockerImageName
import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.{ClassLoaderEvolutionsReader, Evolutions}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

class SlickAtlassianHostRepositoryIt
    extends PlaySpec
    with GuiceOneAppPerTest
    with FutureAwaits
    with Eventually
    with DefaultAwaitTimeout
    with TestContainerForAll {

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

  def hostRepo(implicit app: Application): SlickAtlassianHostRepository =
    Application.instanceCache[SlickAtlassianHostRepository].apply(app)

  "Using a Slick host repository" when {

    "repository is empty" should {

      "not find any hosts when fetching all" in {
        await {
          hostRepo.all()
        } mustEqual Seq.empty
      }

      "return None when trying to find a non existent host by client key" in {
        await {
          hostRepo.findByClientKey("fake-client-key")
        } mustBe None
      }

    }

    "saving a Atlassian hosts to the repository" should {

      "successfully save the host" in new AtlassianHostFixture {
        withEvolutions {
          await {
            hostRepo.save(host)
          } mustEqual host

          await {
            hostRepo.all()
          } mustEqual Seq(host)
        }
      }

      "find the inserted host by client key" in new AtlassianHostFixture {
        withEvolutions {
          await {
            hostRepo.save(host)
          }

          await {
            hostRepo.findByClientKey(host.clientKey)
          } mustBe Some(host)
        }
      }

    }

    "saving the same Atlassian hosts twice" should {

      "not duplicate the host" in new AtlassianHostFixture {
        withEvolutions {
          await {
            hostRepo.save(host)
          } mustBe host

          await {
            hostRepo.save(host)
          } mustBe host

          await {
            hostRepo.all()
          } mustBe Seq(host)
        }
      }

    }

    "updating an Atlassian host" should {

      "successfully store the updated version" in new AtlassianHostFixture {
        withEvolutions {
          val updated = host.copy(installed = !host.installed)
          await {
            hostRepo.save(host)
          } mustBe host

          await {
            hostRepo.save(updated)
          } mustBe updated

          await {
            hostRepo.all()
          } mustBe Seq(updated)
        }
      }

    }

    "saving the same Atlassian base URL twice" should {
      /*
       * This test case checks that an installation record can be saved even if a record with the same base URL
       * but different client key already exists.
       *
       * This case appears in the following scenarios:
       * - someone migrates to a new Cloud instance and tries to re-install the app again
       * - sandbox instances which have been installed before
       */
      "duplicate the host" in new AtlassianHostFixture {
        withEvolutions {
          await {
            hostRepo.save(host)
          } mustBe host

          val updated = host.copy(clientKey = "some-other-client-key")

          await {
            hostRepo.save(updated)
          } mustBe updated

          await {
            hostRepo.all()
          } mustBe Seq(host, updated)
        }
      }

    }

  }

}
