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
          } must contain theSameElementsAs Seq(host)
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
          } must contain theSameElementsAs Seq(host)
        }
      }

    }

    "finding uninstalled hosts" should {

      "return all uninstalled hosts" in new AtlassianHostFixture {
        withEvolutions {
          val uninstalledHost = host.copy(installed = false)
          await {
            hostRepo.save(uninstalledHost)
          } mustBe uninstalledHost

          val extraHosts =
            listOfN(5, atlassianHostGen).retryUntil(_ => true).sample.get
          for (host <- extraHosts) {
            await {
              hostRepo.save(host)
            } mustBe host
          }

          val expectedUninstalledHosts =
            (Seq(uninstalledHost) ++ extraHosts).filterNot(_.installed)

          val uninstalledHosts = await {
            hostRepo.findUninstalled()
          }

          expectedUninstalledHosts.size mustBe uninstalledHosts.size
          expectedUninstalledHosts must contain theSameElementsAs uninstalledHosts
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
          } must contain theSameElementsAs Seq(updated)
        }
      }

    }

    "deleting an Atlassian host" should {
      "successfully delete the host" in new AtlassianHostFixture {
        withEvolutions {
          val uninstalledHost = host.copy(installed = false)
          await {
            hostRepo.save(uninstalledHost)
          } mustBe uninstalledHost

          await {
            hostRepo.all()
          } must contain theSameElementsAs Seq(uninstalledHost)

          await {
            hostRepo.delete(uninstalledHost.clientKey)
          } mustBe 1

          await {
            hostRepo.all()
          } mustBe Seq.empty
        }
      }

      "not delete the host if it is installed" in new AtlassianHostFixture {
        withEvolutions {
          val installedHost = host.copy(installed = true)
          await {
            hostRepo.save(installedHost)
          } mustBe installedHost

          await {
            hostRepo.all()
          } must contain theSameElementsAs Seq(installedHost)

          await {
            hostRepo.delete(installedHost.clientKey)
          } mustBe 0

          await {
            hostRepo.all()
          } must contain theSameElementsAs Seq(installedHost)
        }
      }

      "return 0 if the host to delete does not exist" in new AtlassianHostFixture {
        await {
          hostRepo.all()
        } mustBe Seq.empty

        await {
          hostRepo.delete(host.clientKey)
        } mustBe 0

        await {
          hostRepo.all()
        } mustBe Seq.empty
      }
    }

  }

}
