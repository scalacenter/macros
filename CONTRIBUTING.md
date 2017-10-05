# Contributor guide

This guide is for people who would like to be involved in contributing to
development of Scala Macros.

This guide assumes that you have some experience doing Scala
development. If you get stuck on any of these steps, please feel free
to [ask for help](#getting-help).

## Modules

- `core/` public API for macro system.
- `engines/` compiler implementations for abstract parts of the macro system API.
- `plugins/` compiler integrations.

## Setting up an IDE

The project should import in IntelliJ as any normal sbt project.
The project does use advanced and experimental Scala features which
causes IntelliJ to report false red squiggly marks in many places,
for example
- inside `macro` blocks
- for pattern matches on the abstract trees, example `case Term.Name`

Always compile/test from the sbt console.

## Testing

Tests are written using JUnit.
It's best to start the sbt once and keep the same sbt shell running between
test commands.
The following commands assume you are inside the sbt shell

```sh
> testsApi/test    # unit tests for public API
> testsMacros/test # integration tests for macro expansions
> enableDotty      # switch scalaVersion to dotty
> very test        # run all tests for all scalaVersion: 2.12, dotty, ...

# (temporary) you need to clean the entire project before running macro
# tests in Dotty to prevent
# [error] (scalamacros/compile:compileIncremental) java.lang.NoClassDefFoundError: scala/macros/trees/Trees
> enableDotty
> ; clean ; testsMacros/test
```

## Opening pull requests

Adapted from [lihaoyi/ammonite](https://github.com/lihaoyi/Ammonite).

For larger contributions, please first open a ticket to discuss design and make
sure your contribution fits the project's goals.

- **All code PRs should come with**: a meaningful description, inline-comments
  for important things, unit tests (positive and negative), and a green build
  in [CI](https://travis-ci.org/scalacenter/macros).
- **Format your code with scalafmt**. Run `./bin/scalafmt` from the project
  root directory. If you use IntelliJ, you can enable "Format on file save"
  under Tools > Scalafmt > for current project".
- **Be prepared to discuss/argue-for your changes if you want them merged**!
  You will probably need to refactor so your changes fit into the larger
  codebase - **If your code is hard to unit test, and you don't want to unit
  test it, that's ok**. But be prepared to argue why that's the case!
- **It's entirely possible your changes won't be merged**, or will get ripped
  out later.
- **Even a rejected/reverted PR is valuable**! It helps explore the solution
  space, and know what works and what doesn't. For every line in the repo, at
  least three lines were tried, committed, and reverted/refactored, and more
  than 10 were tried without committing.
- **Feel free to send Proof-Of-Concept PRs** that you don't intend to get merged.
  The title of such PRs should be prefixed with "WIP".

## Getting help

If you have questions about contributing to Scala Macros, don't
hesitate to ask in our
[gitter channel](https://gitter.im/scalacenter/macros-contributors).
