package scala.macros
package internal
package dialects

import scala.macros.dialects.Scala210

trait InternalDialect {
  def current: Scala210.type = Scala210
}