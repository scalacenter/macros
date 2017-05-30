package scala.macros
package internal
package dialects

import scala.macros.dialects.Scala213

trait InternalDialect {
  def current: Scala213.type = Scala213
}