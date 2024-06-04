import sbt._

object Dependencies {
  val root = Seq(
    Library.playSlick,
    Library.atlassianConnectApi,
    Library.playSlickEvolutions % "test",
    Library.scalaTest % "test",
    Library.scalaCheck % "test",
    Library.scalaTestPlusScalaCheck % "test",
    Library.h2 % "test",
  )

  val integration = Seq(
    Library.playSlickEvolutions % "test",
    Library.scalaTest % "test",
    Library.scalaCheck % "test",
    Library.postgres % "test",
    Library.testcontainersScala % "test",
    Library.testcontainersScalaPostgresql % "test",
    Library.scalaTestPlusScalaCheck % "test",
  )
}

object Version {
  val atlassianConnectPlay = "0.8.0"
  val playSlick = "6.1.0"
  val scalaTestPlusPlay = "7.0.1"
  val scalaCheck = "1.18.0"
  val scalaTestPlusScalaCheck = "3.2.18.0"
  val h2 = "1.4.197"
  val postgres = "42.6.0"
  val testcontainersScala = "0.41.3"
}

object Library {
  val atlassianConnectApi = "io.toolsplus" %% "atlassian-connect-play-api" % Version.atlassianConnectPlay
  val playSlick = "org.playframework" %% "play-slick" % Version.playSlick
  val playSlickEvolutions = "org.playframework" %% "play-slick-evolutions" % Version.playSlick
  val scalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % Version.scalaTestPlusPlay
  val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
  val scalaTestPlusScalaCheck = "org.scalatestplus" %% "scalacheck-1-17" % Version.scalaTestPlusScalaCheck
  val h2 = "com.h2database" % "h2" % Version.h2
  val postgres = "org.postgresql" % "postgresql" % Version.postgres
  val testcontainersScala = "com.dimafeng" %% "testcontainers-scala-scalatest" % Version.testcontainersScala
  val testcontainersScalaPostgresql = "com.dimafeng" %% "testcontainers-scala-postgresql" % Version.testcontainersScala
}
