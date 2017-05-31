### Scala macros
[![Join the chat at https://gitter.im/scalamacros/scalamacros](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/scalamacros/scalamacros?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Def macros and macro annotations have become an integral part of the Scala 2.x ecosystem. Well-known libraries like Play, Sbt, Scala.js, ScalaTest, Shapeless, Slick, Spark, Spire and others use macros to achieve previously unreachable standards of terseness and type safety.

Unfortunately, Scala macros have also gained notoriety as an arcane and brittle technology. The most common criticisms of Scala macros concern their subpar tool support and overcomplicated metaprogramming API based on compiler internals. Even five years after their introduction, macros still can't expand in Intellij, leading to proliferation of spurious red squiggles - sometimes in pretty simple code. As a result, the language committee has decided to retire the current macro system in Scala 3.x.

During the last couple years, we've been working on a new macro system that will support both Scala 2.x and Scala 3.x. New-style macros are based on a platform-independent metaprogramming API that was designed  to be easy to use and easy to support in multiple implementations of the language.

### Team

The current maintainers (people who can merge pull requests) are:

* Eugene Burmako - [`@xeno-by`](https://github.com/xeno-by)

An up-to-date list of contributors is available here: https://github.com/scalamacros/scalamacros/graphs/contributors.

### Credits

Over the years, many contributors influenced the design of Scala macros. Check out [Eugene Burmako's dissertation](https://infoscience.epfl.ch/record/226166?ln=en) for more information.
