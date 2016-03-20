import crylio.{Dependencies, CrylioBuild}

CrylioBuild.defaultSettings

libraryDependencies ++= Dependencies.akka ++ Dependencies.config
