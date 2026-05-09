ThisBuild / scalaVersion       := "2.13.14"
ThisBuild / crossScalaVersions := Seq("2.13.14", "3.3.5")
ThisBuild / organization       := "com.programmera"
ThisBuild / version            := "2.0-SNAPSHOT"
ThisBuild / semanticdbEnabled  := true
ThisBuild / semanticdbVersion  := scalafixSemanticdb.revision

lazy val root = (project in file("."))
  .settings(
    name := "ScalaLand",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings"),
    scalacOptions ++= {
      if (scalaVersion.value.startsWith("2.")) Seq("-Xsource:3")
      else                                     Seq("-source:3.3")
    }
  )
