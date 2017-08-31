organization in ThisBuild := "fr.hoshi"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val `lagom-learning` = (project in file("."))
  .aggregate(`lagom-learning-api`, `lagom-learning-impl`, `lagom-learning-stream-api`, `lagom-learning-stream-impl`)

lazy val `lagom-learning-api` = (project in file("lagom-learning-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      "com.typesafe.play" %% "play-json-joda" % "2.6.2"
    )
  )

lazy val `lagom-learning-impl` = (project in file("lagom-learning-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`lagom-learning-api`)

lazy val `lagom-learning-stream-api` = (project in file("lagom-learning-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-learning-stream-impl` = (project in file("lagom-learning-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`lagom-learning-stream-api`, `lagom-learning-api`)
