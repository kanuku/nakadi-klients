import com.typesafe.sbt.less.Import.LessKeys
import com.typesafe.sbt.web.Import._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import play.sbt.routes.RoutesKeys._
import playscalajs.PlayScalaJS.autoImport._
import sbt.Keys._
import sbt._

object Settings {
  val applicationName = "scalajs-play-demo"
  val applicationVersion = "1.0.0"
  lazy val elideOptions = settingKey[Seq[String]]("Set limit for elidable functions")

  lazy val applicationSettings = Seq(
    name := applicationName,
    version := applicationVersion
  )

  val sharedSettings = Seq(
    scalaVersion := versions.common.scala,
    scalacOptions ++= Seq(
      "-Xlint",
      "-unchecked",
      "-deprecation",
      "-feature"
    ),
    resolvers ++= Seq(Resolver.jcenterRepo)
  )

  lazy val clientSettings = applicationSettings ++ sharedSettings ++ Seq(
    libraryDependencies ++= dependencies.clientDependencies.value,
    elideOptions := Seq(),
    scalacOptions ++= elideOptions.value,
    jsDependencies ++= dependencies.jsDependencies.value,
    skip in packageJSDependencies := false,
    persistLauncher := true,
    persistLauncher in Test := false,
    testFrameworks += new TestFramework("utest.runner.Framework")
  )

  lazy val serverSettings = applicationSettings ++ sharedSettings ++ Seq(
    libraryDependencies ++= dependencies.serverDependencies.value,
    commands += ReleaseCmd,
    pipelineStages := Seq(scalaJSProd),
    LessKeys.compress in Assets := true,
    includeFilter in(Assets, LessKeys.less) := "*.less",
    excludeFilter in(Assets, LessKeys.less) := "_*.less",
    routesGenerator := InjectedRoutesGenerator
  )

  // Command for building a release
  lazy val ReleaseCmd = Command.command("release") {
    state => "set elideOptions in client := Seq(\"-Xelide-below\", \"WARNING\")" ::
      "client/clean" ::
      "client/test" ::
      "server/clean" ::
      "server/test" ::
      "server/dist" ::
      "set elideOptions in client := Seq()" ::
      state
  }
}

object dependencies {
  val sharedDependencies = Def.setting(Seq(
    "com.lihaoyi" %%% "autowire" % versions.common.autowire,
    "me.chrons" %%% "boopickle" % versions.common.booPickle,
    "com.lihaoyi" %%% "scalarx" % versions.common.scalaRx,
    "com.lihaoyi" %%% "utest" % versions.common.uTest
  ))

  val serverDependencies = Def.setting(Seq(
    "com.softwaremill.macwire" %% "macros" % versions.server.macwire % "provided",
    "com.softwaremill.macwire" %% "util" % versions.server.macwire,
    "com.softwaremill.macwire" %% "proxy" % versions.server.macwire,

    "com.mohiva" %% "play-silhouette" % versions.server.silhouette,
    "com.mohiva" %% "play-silhouette-testkit" % versions.server.silhouette % "test",

    "com.vmunier" %% "play-scalajs-scripts" % versions.server.playScripts
  ))

  val clientDependencies = Def.setting(Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % versions.client.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "extra" % versions.client.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "ext-scalaz71" % versions.client.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "ext-monocle" % versions.client.scalajsReact,
    "com.github.japgolly.scalacss" %%% "ext-react" % versions.client.scalaCSS,
    "org.scala-js" %%% "scalajs-dom" % versions.client.scalaDom
  ))

  val jsDependencies = Def.setting(Seq(
    "org.webjars.npm" % "react" % versions.js.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.npm" % "react-dom" % versions.js.react / "react-dom.js" commonJSName "ReactDOM" minified "react-dom.min.js" dependsOn "react-with-addons.js",
    "org.webjars" % "jquery" % versions.js.jQuery / "jquery.js" minified "jquery.min.js",
    RuntimeDOM % "test"
  ))
}


object versions {

  object common {
    val scala = "2.11.7"
    val scalaRx = "0.2.8"
    val autowire = "0.2.5"
    val booPickle = "1.1.0"
    val uTest = "0.3.1"
  }

  object client {
    val scalaDom = "0.8.2"
    val scalajsReact = "0.10.1"
    val scalaCSS = "0.3.1"
  }

  object js {
    val jQuery = "2.1.4"
    val react = "0.14.2"
  }

  object server {
    val silhouette = "3.0.4"
    val macwire = "2.1.0"
    val playScripts = "0.3.0"
  }

}
