package crylio

import sbt._

object Dependencies {

  object Versions {
    val scalaVersion = "2.11.7"
    val scalatestVersion = "2.2.6"
    val jodaTimeVersion = "2.9.1"
  }

  object Compile {
    val jodaTime =    "joda-time" % "joda-time"    % Versions.jodaTimeVersion
  }

  object Test {
    val scalatest = "org.scalatest" %% "scalatest" % Versions.scalatestVersion % "it,smoke,test"
  }

  lazy val jodaTime = Seq(Compile.jodaTime)

  lazy val testKit = Seq(Test.scalatest)


}