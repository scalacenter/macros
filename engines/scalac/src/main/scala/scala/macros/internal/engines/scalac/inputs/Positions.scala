package scala.macros.internal
package engines.scalac
package inputs

import scala.language.implicitConversions
import scala.reflect.internal.{util => gu}
import scala.macros.{inputs => m}

trait Positions { self: Universe =>
  private lazy val cache = new WeakCache[m.Input, gu.SourceFile]()

  implicit def mpostoGpos(mpos: m.Position): g.Position = {
    ???
  }

  implicit def gposToMpos(gpos: g.Position): m.Position = {
    ???
  }
}