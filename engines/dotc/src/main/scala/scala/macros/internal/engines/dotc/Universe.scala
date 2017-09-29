package scala.macros.internal
package engines.dotc

import scala.tools.nsc.Global
import scala.macros.internal.engines.dotc.semantic._
import scala.macros.internal.engines.dotc.trees._

case class Universe(val g: Global)
    extends scala.macros.Universe
    with Semantic
    with Trees
    with Expansions {
  type Abstracts = TreeAbstracts with MirrorAbstracts with ExpansionAbstracts
  object abstracts extends TreeAbstracts with MirrorAbstracts with ExpansionAbstracts
}
