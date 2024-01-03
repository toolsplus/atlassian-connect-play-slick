import sbt._

object Dependencies {
  val root = Seq(
    Library.playSlick,
    Library.atlassianConnectApi,
    Library.playSlickEvolutions % "test, it",
    Library.scalaTest % "test, it",
    Library.scalaCheck % "test, it",
    Library.scalaTestPlusScalaCheck % "test, it",
    Library.h2 % "test",
    Library.postgres % "it",
    Library.testcontainersScala % "it",
    Library.testcontainersScalaPostgresql % "it"
  )
}

object Version {
  val atlassianConnectPlay = "0.4.2"
  val playSlick = "5.1.0"
  val scalaTestPlusPlay = "5.1.0"
  val scalaCheck = "1.14.3"
  val scalaTestPlusScalaCheck = "3.1.2.0"
  val h2 = "1.4.197"
  val postgres = "42.6.0"
  val testcontainersScala = "0.41.0"
}

object Library {
  val atlassianConnectApi = "io.toolsplus" %% "atlassian-connect-play-api" % Version.atlassianConnectPlay
  val playSlick = "com.typesafe.play" %% "play-slick" % Version.playSlick
  val playSlickEvolutions = "com.typesafe.play" %% "play-slick-evolutions" % Version.playSlick
  val scalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % Version.scalaTestPlusPlay
  val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
  val scalaTestPlusScalaCheck = "org.scalatestplus" %% "scalacheck-1-14" % Version.scalaTestPlusScalaCheck
  val h2 = "com.h2database" % "h2" % Version.h2
  val postgres = "org.postgresql" % "postgresql" % Version.postgres
  val testcontainersScala = "com.dimafeng" %% "testcontainers-scala-scalatest" % Version.testcontainersScala
  val testcontainersScalaPostgresql = "com.dimafeng" %% "testcontainers-scala-postgresql" % Version.testcontainersScala
}
