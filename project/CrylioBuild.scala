package crylio

import crylio.Dependencies.testKit
import sbt._
import sbt.Keys._

object CrylioBuild extends Build {
  lazy val buildSettings = Seq(
    organization := "com.crylio",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := Dependencies.Versions.scalaVersion
  )

  lazy val root = Project(
    id = "crylio",
    base = file("."),
    aggregate = Seq(common, rest))
      .configs(IntegrationTest, SmokeTest)
      .settings(Defaults.itSettings: _*)
      .settings(defaultSettings: _*)
      .settings(inConfig(SmokeTest)(Defaults.testSettings): _*)
      .settings(
        parallelExecution := false,
        libraryDependencies ++= testKit,
        testOptions in Test := Seq(Tests.Filter(unitFilter)),
        testOptions in IntegrationTest := Seq(Tests.Filter(itFilter)),
        testOptions in SmokeTest := Seq(Tests.Filter(smokeFilter)))



  lazy val rest = Project(
    id = "crylio-rest",
    base = file("rest"),
    dependencies = Seq(common % "compile->compile;test->test"))
      .configs(IntegrationTest, SmokeTest)
      .settings(Defaults.itSettings: _*)
      .settings(inConfig(SmokeTest)(Defaults.testSettings): _*)
      .settings(
        testOptions in Test := Seq(Tests.Filter(unitFilter)),
        testOptions in IntegrationTest := Seq(Tests.Filter(itFilter)),
        testOptions in SmokeTest := Seq(Tests.Filter(smokeFilter))
      )

  lazy val common = Project(
    id = "crylio-common",
    base = file("common"))
      .configs(IntegrationTest, SmokeTest)
      .settings(Defaults.itSettings: _*)
      .settings(inConfig(SmokeTest)(Defaults.testSettings): _*)
      .settings(
        testOptions in Test := Seq(Tests.Filter(unitFilter)),
        testOptions in IntegrationTest := Seq(Tests.Filter(itFilter)),
        testOptions in SmokeTest := Seq(Tests.Filter(smokeFilter))
      )

  lazy val SmokeTest = config("smoke").extend(Test).copy()

  def itFilter(name: String): Boolean = name endsWith "IntegrationTest"

  def smokeFilter(name: String): Boolean = name endsWith "SmokeTest"

  def unitFilter(name: String): Boolean = (name endsWith "Test") && !itFilter(name) && !smokeFilter(name)

  override lazy val settings =
    super.settings ++ buildSettings ++ resolverSettings

  lazy val baseSettings = Defaults.coreDefaultSettings

  lazy val parentSettings = baseSettings

  lazy val resolverSettings = {
    Seq(resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.sonatypeRepo("releases"),
      Resolver.typesafeRepo("releases"),
      "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"))
  }

  lazy val defaultSettings = baseSettings ++ settings ++ Seq(
    scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.8", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    javacOptions in Compile ++= Seq("-encoding", "UTF-8", "-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation"),
    javacOptions in doc ++= Seq("-encoding", "UTF-8", "-target", "1.8"),
    //javaOptions in (Compile,run) ++= Seq("-Xdebug", "-Xnoagent", "-Xrunjdwp:transport=dt_socket,address=13298,server=y,suspend=n"),
    shellPrompt := { state => ("[" + scala.Console.CYAN + "%s" + scala.Console.RESET + "] " + scala.Console.GREEN + "%s" + scala.Console.RESET + "$ ").format(Project.extract(state).currentProject.id, currBranch)},
    fork in run := true,
    crossVersion := CrossVersion.binary,

    retrieveManaged := true,

    homepage := Some(url("http://distractify.com")),

    parallelExecution in ThisBuild := false
  )

  object devnull extends ProcessLogger {
    def info(s: => String) {}

    def error(s: => String) {}

    def buffer[T](f: => T): T = f
  }

  def currBranch = (
    ("git rev-parse --abbrev-ref HEAD" lines_! devnull headOption)
      getOrElse "-")
}