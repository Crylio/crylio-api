import crylio.{Dependencies, CrylioBuild}

CrylioBuild.defaultSettings

libraryDependencies ++= Dependencies.config ++ Dependencies.spray ++ Dependencies.akka

