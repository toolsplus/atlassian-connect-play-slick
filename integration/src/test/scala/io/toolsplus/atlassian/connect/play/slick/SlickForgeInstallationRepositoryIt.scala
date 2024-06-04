package io.toolsplus.atlassian.connect.play.slick

import io.toolsplus.atlassian.connect.play.api.models.{
  DefaultAtlassianHost,
  DefaultForgeInstallation
}
import io.toolsplus.atlassian.connect.play.slick.generators.AtlassianHostGen
import org.scalatest.DoNotDiscover
import org.scalatest.concurrent.Eventually
import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@DoNotDiscover
class SlickForgeInstallationRepositoryIt
    extends PlaySpec
    with PostgresContainerTest
    with FutureAwaits
    with Eventually
    with DefaultAwaitTimeout
    with AtlassianHostGen {

  val hostA: DefaultAtlassianHost =
    atlassianHostGen
      .retryUntil(_ => true)
      .sample
      .get
      .copy(installationId = Some("ari-installation-id-a"))
  val hostB: DefaultAtlassianHost =
    atlassianHostGen
      .retryUntil(_ => true)
      .sample
      .get
      .copy(installationId = Some("ari-installation-id-b"))

  val fakeInstallationHostA: DefaultForgeInstallation =
    DefaultForgeInstallation("fake-installation-id", hostA.clientKey)

  val fakeInstallationsHostB: Seq[DefaultForgeInstallation] = Seq(
    "ari-installation-id-1",
    "ari-installation-id-2",
    "ari-installation-id-3",
    "ari-installation-id-4",
    "ari-installation-id-5"
  ).map(DefaultForgeInstallation(_, hostB.clientKey))

  def hostRepo(implicit app: Application): SlickAtlassianHostRepository =
    Application.instanceCache[SlickAtlassianHostRepository].apply(app)

  def forgeInstallationRepo(
      implicit app: Application): SlickForgeInstallationRepository =
    Application.instanceCache[SlickForgeInstallationRepository].apply(app)

  "Using a Slick Forge installation repository" when {

    "repository is empty" should {

      "not find any hosts when fetching all" in {
        await {
          forgeInstallationRepo.all()
        } mustEqual immutable.Seq.empty
      }

      "return None when trying to find a non existent installation by installation id" in {
        await {
          forgeInstallationRepo.findByInstallationId("fake-installation-id")
        } mustBe None
      }

      "return empty Seq when trying to find a installations by client key" in {
        await {
          forgeInstallationRepo.findByClientKey("fake-client-key")
        } mustBe immutable.Seq.empty
      }

    }

    "saving a Forge installation to the repository" should {

      "successfully save the installation" in {
        withEvolutions {
          await(hostRepo.save(hostA))
          await {
            forgeInstallationRepo.save(fakeInstallationHostA)
          } mustEqual fakeInstallationHostA

          await {
            forgeInstallationRepo.all()
          } mustEqual Seq(fakeInstallationHostA)
        }
      }

      "find the inserted installation by installation id" in {
        withEvolutions {
          await(hostRepo.save(hostA))
          await {
            forgeInstallationRepo.save(fakeInstallationHostA)
          }

          await {
            forgeInstallationRepo.findByInstallationId(
              fakeInstallationHostA.installationId)
          } mustBe Some(fakeInstallationHostA)
        }
      }

      "find the inserted installation by client key" in {
        withEvolutions {
          await(hostRepo.save(hostA))
          await {
            forgeInstallationRepo.save(fakeInstallationHostA)
          }

          await {
            forgeInstallationRepo.findByClientKey(
              fakeInstallationHostA.clientKey)
          } mustBe Seq(fakeInstallationHostA)
        }
      }

    }

    "saving different Forge installation with the same client key" should {

      "save all installations" in {
        withEvolutions {
          await(hostRepo.save(hostB))
          await {
            Future.sequence(
              fakeInstallationsHostB.map(forgeInstallationRepo.save))
          }

          await {
            forgeInstallationRepo.all()
          } mustBe fakeInstallationsHostB
        }
      }

    }

    "saving the same Forge installation twice" should {

      "not duplicate the installation" in {
        await(hostRepo.save(hostA))
        withEvolutions {
          await {
            forgeInstallationRepo.save(fakeInstallationHostA)
          } mustBe fakeInstallationHostA

          await {
            forgeInstallationRepo.save(fakeInstallationHostA)
          } mustBe fakeInstallationHostA

          await {
            forgeInstallationRepo.all()
          } mustBe Seq(fakeInstallationHostA)
        }
      }

    }

    "updating a Forge installation" should {

      /**
        * This scenario appears if there is site import (migration)
        * from on Atlassian instance to another. The installation id
        * remains the same but the client key changes.
        */
      "successfully store the updated version" in {
        withEvolutions {
          await(hostRepo.save(hostA))
          await(hostRepo.save(hostB))
          val updated =
            fakeInstallationHostA.copy(clientKey = hostB.clientKey)
          await {
            forgeInstallationRepo.save(fakeInstallationHostA)
          } mustBe fakeInstallationHostA

          await {
            forgeInstallationRepo.save(updated)
          } mustBe updated

          await {
            forgeInstallationRepo.all()
          } mustBe Seq(updated)
        }
      }

    }

    "deleting a Forge installation by client key" should {

      "successfully delete all installations" in {
        withEvolutions {
          await(hostRepo.save(hostA))
          await(hostRepo.save(hostB))
          await {
            Future.sequence(
              fakeInstallationsHostB.map(forgeInstallationRepo.save))
          }

          await {
            forgeInstallationRepo.save(fakeInstallationHostA)
          } mustBe fakeInstallationHostA

          await {
            forgeInstallationRepo.all()
          }.size mustBe (fakeInstallationsHostB ++ Seq(fakeInstallationHostA)).size

          await {
            forgeInstallationRepo.deleteByClientKey(
              fakeInstallationsHostB.head.clientKey)
          } mustBe fakeInstallationsHostB.size

          await {
            forgeInstallationRepo.all()
          } mustBe Seq(fakeInstallationHostA)
        }
      }

    }

  }

}
