package scala.macros.internal

package object prettyprinters {
  // NOTE: In 2.13, scala.compat may go away and with it scala.compat.Platform.EOL,
  // so we need a better home for this little guy.
  val EOL = System.getProperty("line.separator")
}
