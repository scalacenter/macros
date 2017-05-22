package scala.meta
package internal
package io

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.annotation.JSImport.Namespace

/** Facade for native nodejs module "path".
  *
  * @see https://nodejs.org/api/path.html
  */
@js.native
@JSImport("path", Namespace)
object JSPath extends js.Any {
  def isAbsolute(path: String): Boolean = js.native
}

object PlatformCorePathIO {
  def isAbsolutePath(s: String): Boolean =
    if (JSFs != null) JSPath.isAbsolute(s)
    else {
      val message = "isAbsolutePath(String) is not supported in this environment."
      throw new UnsupportedOperationException(message)
    }
}
