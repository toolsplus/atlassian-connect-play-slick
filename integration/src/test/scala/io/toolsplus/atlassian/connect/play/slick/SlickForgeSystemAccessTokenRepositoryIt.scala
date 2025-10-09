package io.toolsplus.atlassian.connect.play.slick

import io.toolsplus.atlassian.connect.play.api.models.DefaultForgeSystemAccessToken
import org.scalatest.DoNotDiscover
import org.scalatest.concurrent.Eventually
import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@DoNotDiscover
class SlickForgeSystemAccessTokenRepositoryIt
    extends PlaySpec
    with PostgresContainerTest
    with FutureAwaits
    with Eventually
    with DefaultAwaitTimeout {

  val fakeSystemAccessToken: DefaultForgeSystemAccessToken =
    DefaultForgeSystemAccessToken(
      "fake-installation-id",
      "fake-api-base-url",
      "fake-access-token",
      Instant.now().truncatedTo(ChronoUnit.MICROS)
    )

  def fakeSystemAccessTokens(now: Instant): Seq[DefaultForgeSystemAccessToken] =
    Seq(
      "ari-installation-id-1",
      "ari-installation-id-2",
      "ari-installation-id-3",
      "ari-installation-id-4",
      "ari-installation-id-5"
    ).zip(1 to 5).map { case (id, index) =>
      DefaultForgeSystemAccessToken(
        id,
        s"fake-api-base-url-$index",
        s"fake-access-token-$index",
        now.plus(index, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MICROS)
      )
    }

  def forgeSystemAccessTokenRepo(implicit
      app: Application
  ): SlickForgeSystemAccessTokenRepository =
    Application.instanceCache[SlickForgeSystemAccessTokenRepository].apply(app)

  "Using a Slick Forge system access token repository" when {

    "repository is empty" should {

      "not find any hosts when fetching all" in {
        await {
          forgeSystemAccessTokenRepo.all()
        } mustEqual immutable.Seq.empty
      }

      "return None when trying to find a non existent installation by installation id" in {
        await {
          forgeSystemAccessTokenRepo.findByInstallationId(
            "fake-installation-id"
          )
        } mustBe None
      }

    }

    "saving a Forge system access token to the repository" should {

      "successfully save the token" in {
        withEvolutions {
          await {
            forgeSystemAccessTokenRepo.save(fakeSystemAccessToken)
          } mustEqual fakeSystemAccessToken

          await {
            forgeSystemAccessTokenRepo.all()
          } mustEqual Seq(fakeSystemAccessToken)

          await {
            forgeSystemAccessTokenRepo.findByInstallationId(
              fakeSystemAccessToken.installationId
            )
          } mustBe Some(fakeSystemAccessToken)
        }
      }

      "update a token if the installation id already exists" in {
        val fakeTokenWith20sExpiry = fakeSystemAccessToken.copy(
          expirationTime =
            Instant.now().plusSeconds(20).truncatedTo(ChronoUnit.MICROS)
        )

        val fakeTokenWithTwoMinExpiry = fakeSystemAccessToken.copy(
          accessToken = "new-fake-access-token",
          expirationTime = Instant
            .now()
            .plus(2, ChronoUnit.MINUTES)
            .truncatedTo(ChronoUnit.MICROS)
        )

        withEvolutions {
          await {
            forgeSystemAccessTokenRepo.save(fakeTokenWith20sExpiry)
          } mustEqual fakeTokenWith20sExpiry

          await {
            forgeSystemAccessTokenRepo.findByInstallationId(
              fakeTokenWith20sExpiry.installationId
            )
          } mustBe Some(fakeTokenWith20sExpiry)

          await {
            forgeSystemAccessTokenRepo.save(fakeTokenWithTwoMinExpiry)
          } mustEqual fakeTokenWithTwoMinExpiry

          await {
            forgeSystemAccessTokenRepo.findByInstallationId(
              fakeTokenWith20sExpiry.installationId
            )
          } mustBe Some(fakeTokenWithTwoMinExpiry)

          await {
            forgeSystemAccessTokenRepo.all()
          } mustEqual Seq(fakeTokenWithTwoMinExpiry)
        }
      }

      "not duplicate the token when saving the same token twice" in {
        withEvolutions {
          await {
            forgeSystemAccessTokenRepo.save(fakeSystemAccessToken)
          } mustBe fakeSystemAccessToken

          await {
            forgeSystemAccessTokenRepo.save(fakeSystemAccessToken)
          } mustBe fakeSystemAccessToken

          await {
            forgeSystemAccessTokenRepo.all()
          } mustBe Seq(fakeSystemAccessToken)
        }
      }

    }

    "finding tokens by installation id and expiration time after" should {

      "find a token after the given expiry time" in {
        val fakeTokenWithTwoMinExpiry = fakeSystemAccessToken.copy(
          expirationTime = Instant
            .now()
            .plus(2, ChronoUnit.MINUTES)
            .truncatedTo(ChronoUnit.MICROS)
        )

        val leeway30s =
          Instant.now().plusSeconds(30).truncatedTo(ChronoUnit.MICROS)

        withEvolutions {
          await {
            forgeSystemAccessTokenRepo.save(fakeTokenWithTwoMinExpiry)
          }

          await {
            forgeSystemAccessTokenRepo
              .findByInstallationIdAndExpirationTimeAfter(
                fakeSystemAccessToken.installationId,
                leeway30s
              )
          } mustBe Some(fakeTokenWithTwoMinExpiry)
        }
      }

      "return a token if the given expiration time equals the token expiry" in {
        val fakeTokenWithTwoMinExpiry = fakeSystemAccessToken.copy(
          expirationTime = Instant
            .now()
            .plus(2, ChronoUnit.MINUTES)
            .truncatedTo(ChronoUnit.MICROS)
        )

        withEvolutions {
          await {
            forgeSystemAccessTokenRepo.save(fakeTokenWithTwoMinExpiry)
          }

          await {
            forgeSystemAccessTokenRepo
              .findByInstallationIdAndExpirationTimeAfter(
                fakeSystemAccessToken.installationId,
                fakeTokenWithTwoMinExpiry.expirationTime
              )
          } mustBe Some(fakeTokenWithTwoMinExpiry)

          await {
            forgeSystemAccessTokenRepo
              .findByInstallationIdAndExpirationTimeAfter(
                fakeSystemAccessToken.installationId,
                fakeTokenWithTwoMinExpiry.expirationTime.plusMillis(1)
              )
          } mustBe None
        }
      }

      "not return a token if the given expiration time is after the token expiry" in {
        val fakeTokenWithTwoMinExpiry = fakeSystemAccessToken.copy(
          expirationTime = Instant
            .now()
            .plus(2, ChronoUnit.MINUTES)
            .truncatedTo(ChronoUnit.MICROS)
        )

        withEvolutions {
          await {
            forgeSystemAccessTokenRepo.save(fakeTokenWithTwoMinExpiry)
          }

          await {
            forgeSystemAccessTokenRepo
              .findByInstallationIdAndExpirationTimeAfter(
                fakeSystemAccessToken.installationId,
                fakeTokenWithTwoMinExpiry.expirationTime.plusSeconds(60)
              )
          } mustBe None

          await {
            forgeSystemAccessTokenRepo
              .all()
          } mustBe Seq(fakeTokenWithTwoMinExpiry)
        }
      }

    }

    "deleting all tokens with expiration time before" should {
      val now = Instant.now().truncatedTo(ChronoUnit.MICROS)
      val tokens = fakeSystemAccessTokens(now)
      val nowPlusTwoMin30s = now.plusSeconds(150)

      "clean up the repository" in {
        withEvolutions {
          await {
            Future.sequence(tokens.map(forgeSystemAccessTokenRepo.save))
          } mustEqual tokens

          await {
            forgeSystemAccessTokenRepo
              .all()
          } mustBe tokens

          val nRemovedTokens = await(
            forgeSystemAccessTokenRepo.deleteAllByExpirationTimeBefore(
              nowPlusTwoMin30s
            )
          )

          nRemovedTokens mustEqual 2

          await {
            forgeSystemAccessTokenRepo
              .all()
          } must contain theSameElementsAs tokens.drop(2)
        }
      }

    }

  }

}
