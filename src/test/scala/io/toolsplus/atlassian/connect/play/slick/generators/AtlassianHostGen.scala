package io.toolsplus.atlassian.connect.play.slick.generators

import io.toolsplus.atlassian.connect.play.api.models.Predefined.ClientKey
import io.toolsplus.atlassian.connect.play.api.models.StandardAtlassianHost
import org.scalacheck.Gen
import org.scalacheck.Gen.{alphaStr, numStr, option, _}

trait AtlassianHostGen {

  def clientKeyGen: Gen[ClientKey] = alphaNumStr

  def pluginVersionGen: Gen[String] =
    listOfN(3, posNum[Int]).map(n => n.mkString("."))

  def productTypeGen: Gen[String] = oneOf("jira", "confluence")

  def atlassianHostGen: Gen[StandardAtlassianHost] =
    for {
      key <- alphaStr
      clientKey <- clientKeyGen
      publicKey <- alphaNumStr
      oauthClientId <- option(alphaNumStr)
      sharedSecret <- alphaNumStr.suchThat(s => s.length >= 32 && !s.isEmpty)
      serverVersion <- numStr
      pluginsVersion <- pluginVersionGen
      baseUrl <- alphaStr
      productType <- productTypeGen
      description <- alphaStr
      serviceEntitlementNumber <- option(numStr)
      installed <- oneOf(true, false)
    } yield
      StandardAtlassianHost(clientKey,
                            key,
                            publicKey,
                            oauthClientId,
                            sharedSecret,
                            serverVersion,
                            pluginsVersion,
                            baseUrl,
                            productType,
                            description,
                            serviceEntitlementNumber,
                            installed)

}
