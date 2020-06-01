package io.toolsplus.atlassian.connect.play.slick

import io.toolsplus.atlassian.connect.play.slick.generators.AtlassianHostGen
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

trait TestSpec
    extends PlaySpec
    with ScalaCheckDrivenPropertyChecks
    with FutureAwaits
    with DefaultAwaitTimeout
    with AtlassianHostGen
