package io.toolsplus.atlassian.connect.play.slick

import io.toolsplus.atlassian.connect.play.api.models.DefaultForgeSystemAccessToken
import org.scalacheck.Gen.alphaStr
import org.scalatest.DoNotDiscover
import play.api.Application

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@DoNotDiscover
class SlickForgeSystemAccessTokenRepositorySpec extends TestSpec {

  val fakeSystemAccessToken: DefaultForgeSystemAccessToken =
    DefaultForgeSystemAccessToken("fake-installation-id",
                                  "fake-api-base-url",
                                  "fake-access-token",
                                  Instant.now())

  def fakeSystemAccessTokens(now: Instant): Seq[DefaultForgeSystemAccessToken] =
    Seq(
      "ari-installation-id-1",
      "ari-installation-id-2",
      "ari-installation-id-3",
      "ari-installation-id-4",
      "ari-installation-id-5"
    ).zip(1 to 5).map {
      case (id, index) =>
        DefaultForgeSystemAccessToken(id,
                                      s"fake-api-base-url-$index",
                                      s"fake-access-token-$index",
                                      now.plus(index, ChronoUnit.MINUTES))
    }

  // Note that the underlying H2 database does not properly capture the instant value
  // because we use the SQL 'TIMESTAMP' type for the expiry time column in the evolutions
  // file.
  // According to the Slick docs, with H2, we would have to use the type
  // 'TIMESTAMP(9) WITH TIME ZONE' for the expiry time column.
  // https://scala-slick.org/doc/3.3.0/upgrade.html#support-for-java.time-columns
  def forgeSystemAccessTokenRepo(
      implicit app: Application): SlickForgeSystemAccessTokenRepository =
    Application.instanceCache[SlickForgeSystemAccessTokenRepository].apply(app)

  "Using a Slick Forge system access token repository" when {

    "repository is empty" should {

      "not find any installations when fetching all" in {
        await {
          forgeSystemAccessTokenRepo.all()
        } mustBe Seq.empty
      }

      "return None when trying to find a non existent installation by installation id" in {
        forAll(alphaStr) { installationId =>
          await {
            forgeSystemAccessTokenRepo.findByInstallationId(installationId)
          } mustBe None
        }
      }

      "return empty Seq when trying to find a non existent tokens by installation id" in {
        forAll(alphaStr) { installationId =>
          await {
            forgeSystemAccessTokenRepo.findByInstallationId(installationId)
          } mustBe None
        }
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
          }.size mustBe 1
        }
      }

      "successfully save many tokens" in {
        val tokens = fakeSystemAccessTokens(Instant.now())
        withEvolutions {
          await {
            Future.sequence(tokens.map(forgeSystemAccessTokenRepo.save))
          } mustEqual tokens

          await {
            forgeSystemAccessTokenRepo.all()
          }.size mustBe tokens.size
        }
      }

      "find the inserted token by installation id" in {
        withEvolutions {
          await {
            forgeSystemAccessTokenRepo.save(fakeSystemAccessToken)
          }

          await {
            forgeSystemAccessTokenRepo.findByInstallationId(
              fakeSystemAccessToken.installationId)
          }.get.installationId mustBe fakeSystemAccessToken.installationId
        }
      }

      "update a token if the installation id already exists" in {
        val fakeTokenWith20sExpiry = fakeSystemAccessToken.copy(
          expirationTime = Instant.now().plusSeconds(20))

        val fakeTokenWithTwoMinExpiry = fakeSystemAccessToken.copy(
          accessToken = "new-fake-access-token",
          expirationTime = Instant.now().plus(2, ChronoUnit.MINUTES))

        withEvolutions {
          await {
            forgeSystemAccessTokenRepo.save(fakeTokenWith20sExpiry)
          } mustEqual fakeTokenWith20sExpiry

          await {
            forgeSystemAccessTokenRepo.findByInstallationId(
              fakeTokenWith20sExpiry.installationId)
          }.get.accessToken mustBe fakeTokenWith20sExpiry.accessToken

          await {
            forgeSystemAccessTokenRepo.save(fakeTokenWithTwoMinExpiry)
          } mustEqual fakeTokenWithTwoMinExpiry

          await {
            forgeSystemAccessTokenRepo.findByInstallationId(
              fakeTokenWith20sExpiry.installationId)
          }.get.accessToken mustBe fakeTokenWithTwoMinExpiry.accessToken

          await {
            forgeSystemAccessTokenRepo.all()
          }.size mustEqual 1
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
          }.size mustBe 1
        }
      }

    }

  }

}
