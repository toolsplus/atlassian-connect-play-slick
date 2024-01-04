package io.toolsplus.atlassian.connect.play.slick.generators

import io.toolsplus.atlassian.connect.play.api.models.Predefined.ClientKey
import io.toolsplus.atlassian.connect.play.api.models.DefaultAtlassianHost
import org.scalacheck.Gen
import org.scalacheck.Gen.{alphaStr, numStr, option, _}

trait AtlassianHostGen {

  def clientKeyGen: Gen[ClientKey] = alphaNumStr

  def productTypeGen: Gen[String] = oneOf("jira", "confluence")

  def atlassianHostGen: Gen[DefaultAtlassianHost] =
    for {
      key <- alphaStr
      clientKey <- clientKeyGen
      oauthClientId <- option(alphaNumStr)
      sharedSecret <- alphaNumStr.suchThat(s => s.length >= 32 && s.nonEmpty)
      baseUrl <- alphaStr
      productType <- productTypeGen
      description <- alphaStr
      serviceEntitlementNumber <- option(numStr)
      entitlementId <- option(numStr)
      entitlementNumber <- option(numStr)
      installed <- oneOf(true, false)
    } yield
      DefaultAtlassianHost(
        clientKey,
        key,
        oauthClientId,
        sharedSecret,
        baseUrl,
        baseUrl,
        baseUrl,
        productType,
        description,
        serviceEntitlementNumber,
        entitlementId,
        entitlementNumber,
        installed
      )

}
