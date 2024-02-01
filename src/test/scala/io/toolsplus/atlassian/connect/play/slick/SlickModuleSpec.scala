package io.toolsplus.atlassian.connect.play.slick

import io.toolsplus.atlassian.connect.play.api.repositories.{
  AtlassianHostRepository,
  ForgeInstallationRepository
}
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder

class SlickModuleSpec extends PlaySpec {

  "SlickModule" when {

    "using reference.conf" should {

      "have slick module enabled" in new WithReferenceConfig {
        enabledModules(ref) must contain(classOf[SlickModule].getName)
      }

    }

    "loaded from reference.conf" should {

      val appBuilder =
        GuiceApplicationBuilder(
          configuration = Configuration.reference ++ TestData.configurationWithoutEvolutions)
      val injector = appBuilder.injector()

      "bind AtlassianHostRepository to SlickAtlassianHostRepository" in {
        val hostRepository = injector.instanceOf[AtlassianHostRepository]
        hostRepository mustBe a[SlickAtlassianHostRepository]
      }

      "bind AtlassianHostRepository as a singleton" in {
        val hostRepository1 = injector.instanceOf[AtlassianHostRepository]
        val hostRepository2 = injector.instanceOf[AtlassianHostRepository]
        hostRepository1 mustEqual hostRepository2
      }

      "bind ForgeInstallationRepository to SlickForgeInstallationRepository" in {
        val forgeInstallationRepository =
          injector.instanceOf[ForgeInstallationRepository]
        forgeInstallationRepository mustBe a[SlickForgeInstallationRepository]
      }

      "bind ForgeInstallationRepository as a singleton" in {
        val forgeInstallationRepository1 =
          injector.instanceOf[ForgeInstallationRepository]
        val forgeInstallationRepository2 =
          injector.instanceOf[ForgeInstallationRepository]
        forgeInstallationRepository1 mustEqual forgeInstallationRepository2
      }

    }

  }

}
