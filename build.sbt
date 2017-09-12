name := """sbr-sql-connector"""

version := "1.0"

scalaVersion := "2.12.3"

val Versions = new {
  val config = "1.3.1"
  val h2 = "1.4.196"
  val play = "2.6.3"
  val qos = "1.2.3"
  val scalike = "3.0.2"
  val slick = "3.2.1"
  val slf4j = "1.6.4"
  val test = "3.0.1"
}

libraryDependencies += "com.typesafe" % "config" % Versions.config

// Use Play JSON parsers
libraryDependencies += "com.typesafe.play" %% "play-json" % Versions.play % "provided"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalactic" %% "scalactic" % Versions.test
libraryDependencies += "org.scalatest" %% "scalatest" % Versions.test % "test"

// https://mvnrepository.com/artifact/javax.inject/javax.inject
libraryDependencies += "javax.inject" % "javax.inject" % "1"

// https://mvnrepository.com/artifact/com.h2database/h2
libraryDependencies += "com.h2database" % "h2" % Versions.h2 % "test"

// ScalikeJDBC for easy SQL access:  http://scalikejdbc.org/
libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc"         % Versions.scalike,
  "org.scalikejdbc" %% "scalikejdbc-config"  % Versions.scalike,
  "org.scalikejdbc" %% "scalikejdbc"       % Versions.scalike,
  "org.scalikejdbc" %% "scalikejdbc-test"   % Versions.scalike  % "test",
  "ch.qos.logback"  %  "logback-classic"    % Versions.qos
)

// Stop tests running in parallel because the DB schema gets messed up
parallelExecution in Test := false

// Resolvers
resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"


