### Scala macros
[![Build Status](https://platform-ci.scala-lang.org/api/badges/scalamacros/scalamacros/status.svg)](https://platform-ci.scala-lang.org/scalamacros/scalamacros)
[![Join the chat at https://gitter.im/scalamacros/scalamacros](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/scalamacros/scalamacros?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Def macros and macro annotations have become an integral part of the Scala 2.x ecosystem. Well-known libraries like Play, Sbt, Scala.js, ScalaTest, Shapeless, Slick, Spark, Spire and others use macros to achieve previously unreachable standards of terseness and type safety.

Unfortunately, Scala macros have also gained notoriety as an arcane and brittle technology. The most common criticisms of Scala macros concern their subpar tool support and overcomplicated metaprogramming API based on compiler internals. Even five years after their introduction, macros still can't expand in Intellij, leading to proliferation of spurious red squiggles - sometimes in pretty simple code. As a result, the language committee has decided to retire the current macro system in Scala 3.x.

During the last couple years, we've been working on a new macro system that will support both Scala 2.x and Scala 3.x. New-style macros are based on a platform-independent metaprogramming API that was designed  to be easy to use and easy to support in multiple implementations of the language.

### Status

This repository contains a technology preview of the new macro system that features:
  * Scalameta-based syntactic and semantic APIs that cross-compile against Scala 2.10, 2.11, 2.12, 2.13 and Dotty for both JVM and JS. The corresponding library is quite slim, being less than 500Kb in size.
  * A prototype implementation of the new macro engine for Scala 2.11.11 that supports macro annotations and def macros.
  * Examples of new-style macros, including [the @main annotation](tests/macros/shared/src/main/scala/scala/macros/tests/scaladays/main.scala) and [a new-style materializer](tests/macros/shared/src/main/scala/scala/macros/tests/scaladays/Serialize.scala).

### Roadmap

Our first order of business is to get a Dotty implementation up and running. Next, we will be publishing documentation, including detailed descriptions of the APIs and example projects to get started. Follow [our issue tracker](https://github.com/scalamacros/scalamacros/issues/) for more information.

### Team

The current maintainers (people who can merge pull requests) are:

* Eugene Burmako - [`@xeno-by`](https://github.com/xeno-by)

An up-to-date list of contributors is available here: https://github.com/scalamacros/scalamacros/graphs/contributors.

### Credits

Over the years, many contributors influenced the design of Scala macros. Check out [Eugene Burmako's dissertation](https://infoscience.epfl.ch/record/226166?ln=en) for more information.

The latest iteration of this project is the result of collaboration between:
  * Eugene Burmako, who led the development of new-style macros based on [Scalameta](https://github.com/scalameta), implemented the new macro APIs and prototype support for Scala 2.x.
  * Fengyun Liu, who experimented with the extractor-based approach to macro APIs, convincingly demonstrated its practical benefits and implemented prototype support for Scala 3.x in [liufengyun/gestalt](https://github.com/liufengyun/gestalt). This was a groundbreaking contribution that allowed us to deliver the new macro system much faster than initially planned.
  * Olafur Pall Geirsson, who worked on the converter-based approach together with Eugene, exposed and highlighted the shortcomings of the converter-based approach to macro APIs.
