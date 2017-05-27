package scala.macros.internal
package engines.scalac

import scala.tools.nsc.Global
import scala.{macros => m}

case class Universe(val g: Global) extends m.Universe {
  def abstracts: Abstracts = ???
}
