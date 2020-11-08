import sbt._

object Dependencies {
  val root = Seq(
    Library.playSlick,
    Library.atlassianConnectApi,
    Library.playSlickEvolutions % "test",
    Library.scalaTest % "test",
    Library.scalaCheck % "test",
    Library.scalaTestPlusScalaCheck % "test",
    Library.h2 % "test"
  )
}

object Version {
  val atlassianConnectPlay = "0.2.0"
  val playSlick = "5.0.0"
  val scalaTestPlusPlay = "5.1.0"
  val scalaCheck = "1.14.3"
  val scalaTestPlusScalaCheck = "3.1.2.0"
  val h2 = "1.4.200"
}

object Library {
  val atlassianConnectApi = "io.toolsplus" %% "atlassian-connect-play-api" % Version.atlassianConnectPlay
  val playSlick = "com.typesafe.play" %% "play-slick" % Version.playSlick
  val playSlickEvolutions = "com.typesafe.play" %% "play-slick-evolutions" % Version.playSlick
  val scalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % Version.scalaTestPlusPlay
  val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
  val scalaTestPlusScalaCheck = "org.scalatestplus" %% "scalacheck-1-14" % Version.scalaTestPlusScalaCheck
  val h2 = "com.h2database" % "h2" % Version.h2
}
