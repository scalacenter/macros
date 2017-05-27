package scala.macros.internal
package engines.scalac
package inputs

import scala.language.implicitConversions
import scala.macros.{inputs => m}

trait Positions { self: Universe =>
  implicit def mpostoGpos(mpos: m.Position): g.Position = {
    ???
  }

  implicit def gposToMpos(gpos: g.Position): m.Position = {
    ???
  }
}