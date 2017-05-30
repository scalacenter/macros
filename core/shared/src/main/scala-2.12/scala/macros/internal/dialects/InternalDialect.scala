package scala.macros
package internal
package dialects

import scala.macros.dialects.Scala212

trait InternalDialect {
  def current: Scala212.type = Scala212
}