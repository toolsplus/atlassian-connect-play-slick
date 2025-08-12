package io.toolsplus.atlassian.connect.play.slick.generators

import com.fortysevendeg.scalacheck.datetime.GenDateTime.genDateTimeWithinRange
import com.fortysevendeg.scalacheck.datetime.instances.jdk8._
import io.toolsplus.atlassian.connect.play.api.models.DefaultAtlassianHost
import io.toolsplus.atlassian.connect.play.api.models.Predefined.ClientKey
import org.scalacheck.Gen
import org.scalacheck.Gen.{alphaStr, numStr, option, _}

import java.time.temporal.ChronoUnit
import java.time.{Duration, Instant}

trait AtlassianHostGen {

  def clientKeyGen: Gen[ClientKey] = alphaNumStr

  def productTypeGen: Gen[String] = oneOf("jira", "confluence")

  def atlassianHostGen: Gen[DefaultAtlassianHost] =
    for {
      key <- alphaStr
      clientKey <- clientKeyGen
      oauthClientId <- option(alphaNumStr)
      installationId <- option(alphaNumStr)
      sharedSecret <- alphaNumStr.suchThat(s => s.length >= 32 && s.nonEmpty)
      baseUrl <- alphaStr
      productType <- productTypeGen
      description <- alphaStr
      serviceEntitlementNumber <- option(numStr)
      entitlementId <- option(numStr)
      entitlementNumber <- option(numStr)
      installed <- oneOf(true, false)
      ttl <-
        if (installed) const(None)
        else
          genDateTimeWithinRange(Instant.now(), Duration.ofDays(30))
            .map(_.truncatedTo(ChronoUnit.MICROS))
            .map(Some(_))
    } yield DefaultAtlassianHost(
      clientKey,
      key,
      oauthClientId,
      installationId,
      sharedSecret,
      baseUrl,
      baseUrl,
      baseUrl,
      productType,
      description,
      serviceEntitlementNumber,
      entitlementId,
      entitlementNumber,
      installed,
      ttl
    )

}
