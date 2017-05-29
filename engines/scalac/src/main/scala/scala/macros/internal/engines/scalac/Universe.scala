package scala.macros.internal
package engines.scalac

import scala.tools.nsc.Global
import scala.macros.internal.engines.scalac.semantic._
import scala.macros.internal.engines.scalac.trees._

case class Universe(val g: Global)
    extends scala.macros.Universe
    with Semantic
    with Trees
    with Expansions {
  type Abstracts = TreeAbstracts with SemanticAbstracts with ExpansionAbstracts
  object abstracts extends TreeAbstracts with SemanticAbstracts with ExpansionAbstracts
}
