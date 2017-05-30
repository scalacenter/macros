package scala.macros
package internal
package dialects

import scala.macros.dialects.Scala211

trait InternalDialect {
  def current: Scala211.type = Scala211
}