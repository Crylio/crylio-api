package crylio

import sbt._

object Dependencies {

  object Versions {
    val scalaVersion = "2.11.7"
    val scalatestVersion = "2.2.6"
    val jodaTimeVersion = "2.9.1"
    val typesafeVersion = "1.3.0"
    val akkaVersion = "2.3.14"
    val akkaKryoSerializationVersion = "0.3.3"
    val kryoSerializersVersion = "0.30"
    val sprayVersion = "1.3.3"
  }

  object Compile {
    val jodaTime =    "joda-time" % "joda-time"    % Versions.jodaTimeVersion
    val config = "com.typesafe" % "config" % Versions.typesafeVersion
    val akka =        "com.typesafe.akka" %% "akka-actor"   % Versions.akkaVersion
    val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % Versions.akkaVersion
    val akkaContrib = "com.typesafe.akka" %% "akka-contrib" % Versions.akkaVersion
    val akkaSlf4j =   "com.typesafe.akka" %% "akka-slf4j"   % Versions.akkaVersion
    //SEE: https://github.com/romix/akka-kryo-serialization
    val kryoSerialization = "com.github.romix.akka" %% "akka-kryo-serialization" % Versions.akkaKryoSerializationVersion
    val kryoSerializers = "de.javakaffee" % "kryo-serializers" % Versions.kryoSerializersVersion
    val logback = "ch.qos.logback" % "logback-classic" % "1.1.2"
    val sprayCan =       "io.spray" %% "spray-can" % Versions.sprayVersion
    val sprayRouting =   "io.spray" %% "spray-routing-shapeless2" % Versions.sprayVersion
    val sprayJson =      "io.spray" %% "spray-json" % "1.3.1"
    val sprayWebsocket = "com.wandoulabs.akka" %% "spray-websocket" % "0.1.4"

  }

  object Test {
    val scalatest = "org.scalatest" %% "scalatest" % Versions.scalatestVersion % "it,smoke,test"
  }

  lazy val jodaTime = Seq(Compile.jodaTime)

  lazy val testKit = Seq(Test.scalatest)

  lazy val config = Seq(Compile.config)

  lazy val akka = Seq(Compile.akka, Compile.akkaCluster, Compile.akkaContrib, Compile.kryoSerialization, Compile.kryoSerializers, Compile.akkaSlf4j, Compile.logback)

  lazy val spray = Seq(Compile.sprayCan, Compile.sprayRouting, Compile.sprayJson, Compile.sprayWebsocket)
}