import Version._
import sbt.Def

version in ThisBuild ~= (_.replace('+', '-'))
name := "scalamacrosRoot"
onLoadMessage := s"Welcome to Scala Macros ${version.value}"
noPublish

lazy val scalamacros = project
  .in(file("core"))
  .settings(
    moduleName := "scalamacros",
    description := "Platform-independent interfaces for new-style Scala macros"
  )
  .enablePlugins(BuildInfoPlugin)

lazy val enginesScalac = project
  .in(file("engines/scalac"))
  .settings(
    moduleName := "scalac-engine",
    description := "Scalac implementation of interfaces for new-style Scala macros",
    crossScalaVersions := List(scala212, scala213),
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
  )
  .dependsOn(scalamacros)

lazy val enginesDotc = project
  .in(file("engines/dotc"))
  .settings(
    moduleName := "dotc-engine",
    description := "Dotc implementation of interfaces for new-style Scala macros",
    scalaVersion := dotty,
    crossScalaVersions := List(dotty),
    libraryDependencies += "ch.epfl.lamp" %% "dotty-compiler" % dotty
  )
  .dependsOn(scalamacros)

lazy val pluginsScalac = project
  .in(file("plugins/scalac"))
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
  .settings(
    noPublish,
    description := "Tests of interfaces for new-style Scala macros",
    libraryDependencies += "junit" % "junit" % "4.12",
    libraryDependencies ++= (
      if (isDotty.value) Seq("ch.epfl.lamp" %% "dotty-compiler" % scalaVersion.value)
      else Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
    )
  )
  .dependsOn(scalamacros)

lazy val testsMacros = project
  .in(file("tests/macros"))
  .settings(
    noPublish,
    description := "Tests of new-style Scala macros",
    crossScalaVersions := List(scala212, dotty),
    scalacOptions += "-language:experimental.macros",
    libraryDependencies ++= {
      if (isDotty.value) {
        Nil
      } else {
        "org.scala-lang" % "scala-reflect" % scalaVersion.value ::
          compilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full) ::
          Nil
      }
    },
    dependencyClasspath.in(Test) ++= dotcEngineClasspath.value,
    scalacOptions ++= usesMacroSettings.value
  )
  .dependsOn(scalamacros)

lazy val usesMacroSettings: Def.Initialize[Task[Seq[String]]] = Def.taskDyn {
  if (isDotty.value) {
    Def.task {
      if (sys.env.contains("CI")) {
        // assert transformations respect Dotty compiler invariants
        "-Ycheck:all" ::
          Nil
      } else Nil
    }
  } else {
    Def.task {
      val jar = Keys.`package`.in(pluginsScalac).in(Compile).value
      val addPlugin = "-Xplugin:" + jar.getAbsolutePath
      // Thanks Jason for this cool idea (taken from https://github.com/retronym/boxer)
      // add plugin timestamp to compiler options to trigger recompile of
      // main after editing the plugin. (Otherwise a 'clean' is needed.)
      val dummy = "-Jdummy=" + jar.lastModified
      Seq(addPlugin, dummy)
    }
  }
}

lazy val dotcEngineClasspath = Def.taskDyn[Classpath] {
  if (isDotty.value) {
    Def.task {
      val _ = compile.in(enginesDotc, Compile).value
      val cp =
        classDirectory.in(enginesDotc, Compile).value
      streams.value.log.info(cp.getAbsolutePath)
      Attributed(cp)(AttributeMap.empty) :: Nil
    }
  } else {
    Def.task(Nil)
  }
}
