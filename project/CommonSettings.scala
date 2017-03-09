import sbt._
import sbt.Keys._

object CommonSettings extends AutoPlugin {

  override def projectSettings = Seq(
    parallelExecution in Test := false,
    fork in Test := true
  )
}