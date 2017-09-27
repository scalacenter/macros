# Contributor guide

## About this document

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
Autocompletion should work fine for the core modules.
IntelliJ may report false red squiggly marks inside `macro` blocks, which will
hopefully get fixed once the def macro syntax is stabilizes.

I recommend to run the tests from the sbt console.

## Testing

Tests are written using JUnit.

```sh
$ sbt testsApi/test    # unit tests for public API
$ sbt testsMacros/test # integration tests for macro expansions
```

## Opening pull requests

Adapted from [lihaoyi/ammonite](https://github.com/lihaoyi/Ammonite).

- **All code PRs should come with**: a meaningful description, inline-comments
  for important things, unit tests (positive and negative), and a green build
  in [CI](https://travis-ci.org/scalacenter/macros).
- **Format your code with scalafmt**. Run `./bin/scalafmt` from the project
  root directory.
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

## Getting help

If you are unsure about anything, don't hesitate to ask in the
[gitter channel](https://gitter.im/scalacenter/scalafix).
