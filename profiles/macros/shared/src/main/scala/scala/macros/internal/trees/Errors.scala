package scala.macros.internal
package trees

object Errors {
  final val QuasiquotesRequireCompilerSupport =
    "new-style quasiquotes require " +
    """addCompilerPlugin("org.scalamacros" %% "scalac-plugin" % "..." cross CrossVersion.full);""" +
    " consult http://scalamacros.org for more information."
}