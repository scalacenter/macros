package scala.meta
package internal
package io

import java.nio.charset._
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.annotation.JSImport.Namespace
import scala.meta.io._

/** Facade for native nodejs module "fs".
  *
  * @see https://nodejs.org/api/fs.html
  */
@js.native
@JSImport("fs", Namespace)
object JSFs extends js.Any {

  /** Returns the file contents using blocking apis */
  def readFileSync(path: String, encoding: String): js.Any = js.native
}

object PlatformCoreFileIO {
  def slurp(path: AbsolutePath, charset: Charset): String =
    if (JSFs != null) JSFs.readFileSync(path.toString, charset.toString).toString
    else {
      val message = "slurp(AbsolutePath, Charset) is not supported in this environment."
      throw new UnsupportedOperationException(message)
    }
}
