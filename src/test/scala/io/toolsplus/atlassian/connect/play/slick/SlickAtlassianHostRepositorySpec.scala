package io.toolsplus.atlassian.connect.play.slick

import io.toolsplus.atlassian.connect.play.slick.fixtures.AtlassianHostFixture
import org.scalacheck.Gen._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.{ClassLoaderEvolutionsReader, Evolutions}
import play.api.inject.guice.GuiceApplicationBuilder

class SlickAtlassianHostRepositorySpec
    extends TestSpec
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
        forAll(alphaStr) { clientKey =>
          await {
            hostRepo.findByClientKey(clientKey)
          } mustBe None
        }
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

      "find the inserted host by installation id" in new AtlassianHostFixture {
        withEvolutions {
          await {
            hostRepo.save(host)
          }

          await {
            hostRepo.findByInstallationId(host.installationId.get)
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

  }

}
