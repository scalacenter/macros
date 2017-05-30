package scala.macros
package internal
package dialects

import scala.macros.dialects.Scala211

trait InternalDialect {
  // NOTE: See https://github.com/scalameta/scalameta/issues/253 for discussion.
  implicit def current: Scala211.type = Scala211
}