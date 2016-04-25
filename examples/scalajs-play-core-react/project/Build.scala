import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import play.sbt.{PlayLayoutPlugin, PlayScala}
import play.twirl.sbt.SbtTwirl
import playscalajs.PlayScalaJS.autoImport._
import playscalajs.ScalaJSPlay
import sbt.Keys._
import sbt._

object ScalaJSPlayCore extends Build {

  lazy val root = project.in(file("."))
    .aggregate(
      sharedJVM,
      sharedJS,
      client,
      server
    )
    .settings(
      publish := {},
      publishLocal := {},
      onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
    )

  lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
    .settings(
      scalaVersion := versions.common.scala,
      libraryDependencies ++= dependencies.sharedDependencies.value
    )
    .jsConfigure(_ enablePlugins ScalaJSPlay)
    .jvmSettings()
    .jsSettings()

  lazy val sharedJVM = shared.jvm
  lazy val sharedJS = shared.js

  lazy val client = project.in(file("client"))
    .settings(Settings.clientSettings ++ Seq(
      name := """scalajs-play-core-react"""
    ))
    .enablePlugins(ScalaJSPlugin, ScalaJSPlay)
    .dependsOn(sharedJS)

  lazy val clients = Seq(client)

  lazy val server = project.in(file("server"))
    .settings(Settings.serverSettings ++ Seq(
      name := "server",
      scalaJSProjects := clients
    ))
    .enablePlugins(SbtTwirl, PlayScala)
    .disablePlugins(PlayLayoutPlugin)
    .aggregate(client)
    .dependsOn(sharedJVM)

  // loads the Play server project at sbt startup

}
