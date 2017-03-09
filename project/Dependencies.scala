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
  val playSlick = "2.1.0"
  val scalaTestPlusPlay = "2.0.0"
  val scalaCheck = "1.13.4"
  val h2 = "1.4.193"

  val atlassianConnectPlay = "0.0.1"
}

object Library {
  val atlassianConnectApi = "io.toolsplus" %% "atlassian-connect-play-api" % Version.atlassianConnectPlay
  val playSlick = "com.typesafe.play" %% "play-slick" % Version.playSlick
  val playSlickEvolutions = "com.typesafe.play" %% "play-slick-evolutions" % Version.playSlick
  val scalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % Version.scalaTestPlusPlay
  val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
  val h2 = "com.h2database" % "h2" % Version.h2
}
