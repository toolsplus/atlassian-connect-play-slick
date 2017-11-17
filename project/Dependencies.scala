import sbt._

object Dependencies {
  val root = Seq(
    Library.playSlick,
    Library.atlassianConnectApi,
    Library.playSlickEvolutions % "test",
    Library.scalaTest % "test",
    Library.scalaCheck % "test",
    Library.h2 % "test"
  )
}

object Version {
  val atlassianConnectPlay = "0.1.2"
  val playSlick = "3.0.2"
  val scalaTestPlusPlay = "3.1.2"
  val scalaCheck = "1.13.5"
  val h2 = "1.4.196"
}

object Library {
  val atlassianConnectApi = "io.toolsplus" %% "atlassian-connect-play-api" % Version.atlassianConnectPlay
  val playSlick = "com.typesafe.play" %% "play-slick" % Version.playSlick
  val playSlickEvolutions = "com.typesafe.play" %% "play-slick-evolutions" % Version.playSlick
  val scalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % Version.scalaTestPlusPlay
  val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
  val h2 = "com.h2database" % "h2" % Version.h2
}
