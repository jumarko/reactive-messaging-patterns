name := "reactive-messaging-patterns"

version := "1.0"

scalaVersion := "2.11.7"

lazy val akkaVersion = "2.4.0"

libraryDependencies ++= Seq(
  // akka core
  "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit"    % akkaVersion,

  "org.scalatest"     %% "scalatest"       % "2.2.4" % "test"
)
