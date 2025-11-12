import ReleaseTransformations.*
import xerial.sbt.Sonatype.sonatypeCentralHost

val commonSettings = Seq(
  organization := "io.toolsplus",
  scalaVersion := "3.3.6"
)

val integrationTestSettings = Seq(
  Test / fork := true,
  Test / testOptions += Tests.Argument(
    TestFrameworks.ScalaTest,
    "-u",
    "target/test-reports"
  )
)

lazy val publishSettings = Seq(
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  homepage := Some(
    url("https://github.com/toolsplus/atlassian-connect-play-slick")
  ),
  licenses := Seq(
    "Apache 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")
  ),
  publishMavenStyle := true,
  Test / publishArtifact := false,
  pomIncludeRepository := { _ =>
    false
  },
  ThisBuild / publishTo := sonatypePublishToBundle.value,
  ThisBuild / sonatypeCredentialHost := sonatypeCentralHost,
  autoAPIMappings := true,
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/toolsplus/atlassian-connect-play-slick"),
      "scm:git:git@github.com:toolsplus/atlassian-connect-play-slick.git"
    )
  ),
  developers := List(
    Developer(
      "tbinna",
      "Tobias Binna",
      "tobias.binna@toolsplus.io",
      url("https://twitter.com/tbinna")
    )
  )
)

lazy val noPublishSettings = Seq(
  publish / skip := true,
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  publishTo := Some(
    Resolver.file("Unused transient repository", file("target/dummyrepo"))
  )
)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommand("publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

def moduleSettings(project: Project) = {
  Seq(
    description := project.id.split("-").map(_.capitalize).mkString(" "),
    name := project.id
  )
}

lazy val `atlassian-connect-play-slick` = project
  .in(file("."))
  .settings(libraryDependencies ++= Dependencies.root)
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(moduleSettings(project))

lazy val `integration` = project
  .in(file("integration"))
  .settings(libraryDependencies ++= Dependencies.integration)
  .settings(commonSettings)
  .settings(integrationTestSettings)
  .settings(
    publish / skip := true
  )
  .dependsOn(`atlassian-connect-play-slick`)
