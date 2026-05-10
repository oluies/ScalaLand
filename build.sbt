ThisBuild / scalaVersion := "3.3.7"
ThisBuild / organization := "com.programmera"
ThisBuild / version := "2.0-SNAPSHOT"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

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
    ),
    // Exclude package-object files from scalafix. ExplicitResultTypes
    // annotates re-exports as `val X: X.type = ...` which Scala 2.13
    // (and the linter's diff check) reports as illegal cyclic
    // references when type alias and val share a name. See
    // .scalafix.conf for the same caveat.
    Compile / scalafix / unmanagedSources :=
      (Compile / unmanagedSources).value.filterNot(_.getName == "package.scala"),
    Test / scalafix / unmanagedSources :=
      (Test / unmanagedSources).value.filterNot(_.getName == "package.scala")
  )
