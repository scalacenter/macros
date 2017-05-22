package scala.meta
package internal
package dialects

import scala.meta.dialects.Dotty

trait InternalDialect {
  // NOTE: See https://github.com/scalameta/scalameta/issues/253 for discussion.
  implicit def current: Dotty.type = Dotty
}