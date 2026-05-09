ThisBuild / scalaVersion := "2.13.14"
ThisBuild / organization := "com.programmera"
ThisBuild / version      := "2.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaLand",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    scalacOptions ++= Seq(
      "-Xsource:3",
      "-deprecation",
      "-feature",
      "-Xfatal-warnings"
    )
  )
