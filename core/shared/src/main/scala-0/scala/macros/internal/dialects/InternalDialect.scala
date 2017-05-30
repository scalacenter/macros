package scala.macros
package internal
package dialects

import scala.macros.dialects.Dotty

trait InternalDialect {
  def current: Dotty.type = Dotty
}