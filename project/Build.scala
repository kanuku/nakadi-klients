import sbt._
import Keys._
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseCreateSrc
import Dependencies._

object NakadiClient extends Build {

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

EclipseKeys.withSource := true

private val commonSettings = net.virtualvoid.sbt.graph.DependencyGraphSettings.graphSettings

def whereToPublishTo(isItSnapshot:Boolean) = {
  val nexus = "https://maven.zalando.net/"
  if (isItSnapshot)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "content/repositories/releases")
  }


val defaultOptions= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)

lazy val root = project.in(file("."))
  .settings(publishTo := whereToPublishTo(isSnapshot.value))
  .aggregate(api, client)

lazy val api = withDefaults(
    "nakadi-klients-api",
    project.in(file("api"))
  ).settings(libraryDependencies ++= apiDeps)

lazy val client = withDefaults(
    "nakadi-klients",
    project.in(file("client")).dependsOn(api)
  ).settings(libraryDependencies ++= clientDeps)

  lazy val it = withDefaults(
      "nakadi-klients-integration-test",
      project.in(file("it")).dependsOn(api, client)
    ).settings(libraryDependencies ++= itDeps)

  def withDefaults(projectName:String, project:sbt.Project)={
    project.settings(
        name := projectName,
        organization := "org.zalando.nakadi.client",
        version := "2.0.0-pre-alpha.13",
        crossPaths := false,
        scalaVersion := "2.11.8",
        publishTo := whereToPublishTo(isSnapshot.value),
        resolvers += Resolver.mavenLocal,
        resolvers += "Maven Central Server" at "http://repo1.maven.org/maven2",
        scalacOptions ++= defaultOptions,
        publishArtifact in (Test, packageBin) := false)
        .configs(Configs.all: _*)
        /*.settings(
          publishArtifact in (Compile, packageDoc) := true //To Publish or Not to Publish scala doc jar
          ,publishArtifact in (Compile, packageSrc) := true //To Publish or Not to Publish src jar
          ,publishArtifact := publish // To Publish or Not to Publish
          ,publishArtifact in Test := false //To Publish or Not to Publish test jar
          ,sources in (Compile,doc) := Seq.empty
          )
          */
  }


}
