import Version._

version in ThisBuild ~= (_.replace('+', '-'))
name := {
  println(s"[info] Welcome to Scala Macros ${version.value}")
  "scalamacrosRoot"
}
noPublish

lazy val scalamacros = project
  .in(file("core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    moduleName := "scalamacros",
    description := "Platform-independent interfaces for new-style Scala macros"
  )

lazy val enginesScalac = project
  .in(file("engines/scalac"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    crossScalaVersions := List(scala212),
    moduleName := "scalac-engine",
    description := "Scalac implementation of interfaces for new-style Scala macros",
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
  )
  .dependsOn(scalamacros)

lazy val pluginsScalac = project
  .in(file("plugins/scalac"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    pluginSettings,
    crossScalaVersions := List(scala212),
    moduleName := "scalac-plugin",
    description := "Scalac integration for new-style Scala macros",
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
    libraryDependencies += "org.scalameta" %% "scalameta" % "2.0.1"
  )
  .dependsOn(enginesScalac)

lazy val testsApi = project
  .in(file("tests/api"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    noPublish,
    description := "Tests of interfaces for new-style Scala macros",
    libraryDependencies += "junit" % "junit" % "4.12",
    libraryDependencies ++= (
      if (isDotty.value) Nil
      else Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
    )
  )
  .dependsOn(scalamacros)

lazy val testsMacros = project
  .in(file("tests/macros"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    noPublish,
    description := "Tests of new-style Scala macros",
    crossScalaVersions := List(scala212),
    libraryDependencies += "junit" % "junit" % "4.12",
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full),
    scalacOptions ++= {
      val jar = Keys.`package`.in(pluginsScalac).in(Compile).value
      val addPlugin = "-Xplugin:" + jar.getAbsolutePath
      // Thanks Jason for this cool idea (taken from https://github.com/retronym/boxer)
      // add plugin timestamp to compiler options to trigger recompile of
      // main after editing the plugin. (Otherwise a 'clean' is needed.)
      val dummy = "-Jdummy=" + jar.lastModified
      Seq(addPlugin, dummy)
    }
  )
  .dependsOn(scalamacros)
