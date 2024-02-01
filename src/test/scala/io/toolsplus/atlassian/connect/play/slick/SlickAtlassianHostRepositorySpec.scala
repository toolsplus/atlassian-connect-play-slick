package io.toolsplus.atlassian.connect.play.slick

import io.toolsplus.atlassian.connect.play.slick.fixtures.AtlassianHostFixture
import org.scalacheck.Gen._
import org.scalatest.DoNotDiscover
import play.api.Application

@DoNotDiscover
class SlickAtlassianHostRepositorySpec extends TestSpec {

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
