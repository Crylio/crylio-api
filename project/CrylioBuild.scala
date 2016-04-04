package crylio

import crylio.Dependencies.testKit
import sbt.Keys._
import sbt._

object CrylioBuild extends Build {
  lazy val buildSettings = Seq(
    organization := "com.crylio",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := Dependencies.Versions.scalaVersion
  )

  lazy val root = Project(
    id = "crylio",
    base = file("."),
    aggregate = Seq(common, rest, app, data))
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
    .settings(
      sourceDirectory in SmokeTest <<= sourceDirectory in Test,
      sourceDirectory in IntegrationTest <<= sourceDirectory in Test)
    .settings(
      run := {
        (run in app in Compile).evaluated
      }
    )


  lazy val rest = Project(
    id = "crylio-rest",
    base = file("rest"),
    dependencies = Seq(data, common % "compile->compile;test->test"))
    .configs(IntegrationTest, SmokeTest)
    .settings(Defaults.itSettings: _*)
    .settings(inConfig(SmokeTest)(Defaults.testSettings): _*)
    .settings(
      testOptions in Test := Seq(Tests.Filter(unitFilter)),
      testOptions in IntegrationTest := Seq(Tests.Filter(itFilter)),
      testOptions in SmokeTest := Seq(Tests.Filter(smokeFilter))
    )
    .settings(
      sourceDirectory in SmokeTest <<= sourceDirectory in Test,
      sourceDirectory in IntegrationTest <<= sourceDirectory in Test)

  lazy val common = Project(
    id = "crylio-common",
    base = file("common"),
    dependencies = Seq())
    .configs(IntegrationTest, SmokeTest)
    .settings(Defaults.itSettings: _*)
    .settings(inConfig(SmokeTest)(Defaults.testSettings): _*)
    .settings(
      testOptions in Test := Seq(Tests.Filter(unitFilter)),
      testOptions in IntegrationTest := Seq(Tests.Filter(itFilter)),
      testOptions in SmokeTest := Seq(Tests.Filter(smokeFilter))
    )

  lazy val data = Project(
    id = "crylio-data",
    base = file("data"),
    dependencies = Seq(common))
    .configs(IntegrationTest, SmokeTest)
    .settings(Defaults.itSettings: _*)
    .settings(inConfig(SmokeTest)(Defaults.testSettings): _*)
    .settings(
      testOptions in Test := Seq(Tests.Filter(unitFilter)),
      testOptions in IntegrationTest := Seq(Tests.Filter(itFilter)),
      testOptions in SmokeTest := Seq(Tests.Filter(smokeFilter))
    )

  lazy val app = Project(
    id = "crylio-app",
    base = file("app"),
    dependencies = Seq(common, rest, data % "compile->compile;test->test"))
    .configs(IntegrationTest, SmokeTest)
    .settings(Defaults.itSettings: _*)
    .settings(inConfig(SmokeTest)(Defaults.testSettings): _*)
    .settings(
      testOptions in Test := Seq(Tests.Filter(unitFilter)),
      testOptions in IntegrationTest := Seq(Tests.Filter(itFilter)),
      testOptions in SmokeTest := Seq(Tests.Filter(smokeFilter))
    )
    .settings(
      sourceDirectory in SmokeTest <<= sourceDirectory in Test,
      sourceDirectory in IntegrationTest <<= sourceDirectory in Test)

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
    shellPrompt := { state => ("[" + scala.Console.CYAN + "%s" + scala.Console.RESET + "] " + scala.Console.GREEN + "%s" + scala.Console.RESET + "$ ").format(Project.extract(state).currentProject.id, currBranch) },
    fork in run := true,
    crossVersion := CrossVersion.binary,

    retrieveManaged := true,

    homepage := Some(url("http://crylio.com")),

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