package scala.macros
package internal
package dialects

import scala.macros.dialects.Dotty

trait InternalDialect {
  // NOTE: See https://github.com/scalameta/scalameta/issues/253 for discussion.
  implicit def current: Dotty.type = Dotty
}