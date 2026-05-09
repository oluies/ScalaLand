ThisBuild / scalaVersion       := "3.3.5"
ThisBuild / organization       := "com.programmera"
ThisBuild / version            := "2.0-SNAPSHOT"
ThisBuild / semanticdbEnabled  := true
ThisBuild / semanticdbVersion  := scalafixSemanticdb.revision

lazy val root = (project in file("."))
  .settings(
    name := "ScalaLand",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    scalacOptions ++= Seq(
      "-source:3.3",
      "-deprecation",
      "-feature",
      "-Xfatal-warnings",
      "-Wunused:all",
      "-explain"
    )
  )
