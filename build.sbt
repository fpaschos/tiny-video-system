name := "tiny-video-system"

version := "0.1"
organization in ThisBuild := "gr.fpas"
scalaVersion in ThisBuild := "2.12.9"


// PROJECTS
lazy val root = (project in file("."))


lazy val `tiny-video-server` = project
  .settings(
    name := "tiny-video-server",
    mainClass in Compile := Some("gr.fpas.srv.Runner"),
    dockerBaseImage := "openjdk:12.0.1-jdk-oraclelinux7",
    dockerExposedPorts := Seq(8080),
    libraryDependencies ++= tinyVideoServerDeps,
    resolvers += "xuggler-repo" at "https://www.dcm4che.org/maven2/"
  )
  .enablePlugins(DockerPlugin)
  .enablePlugins(JavaAppPackaging)

lazy val akkaVersion = "2.5.23"
lazy val akkaHttpVersion = "10.1.9"
lazy val circeVersion = "0.11.1"
lazy val akkaPersistenceJdbcVersion = "2.4.0"
lazy val akkaManagementVersion = "1.0.3"

lazy val tinyVideoServerDeps = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,

  // Akka management
  "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-http" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % akkaManagementVersion,
  //  "com.lightbend.akka.discovery" %% "akka-discovery-dns" % akkaManagementVersion,

  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,

  // Logging
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",


  // Akka persistence
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
  "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.5.2",
  "org.postgresql" % "postgresql" % "9.4.1208",

  // Video processing
  "org.boofcv" % "xuggler" % "0.23",

  // Testing
  "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion
)


