package scala.meta

package object prettyprinters extends Api {
  // NOTE: It is unusual to have the API of scala.meta.XXX different from scala.meta.XXX.Api,
  // but I decided to make an exception here because of the extreme importance of EOL.
  // In 2.13, scala.compat may go away and with it scala.compat.Platform.EOL,
  // so we need a better home for this little guy.
  val EOL = System.getProperty("line.separator")
}
