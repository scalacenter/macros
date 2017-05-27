package scala.meta
package prettyprinters

import scala.meta.internal.prettyprinters._

private[scala] trait Api {
  implicit class XtensionSyntax[T](x: T) {
    def syntax(implicit syntax: Syntax[T]): String = {
      val p = new Prettyprinter
      syntax.render(p, x)
      p.toString
    }
  }

  implicit class XtensionStructure[T: Structure](x: T) {
    def structure(implicit structure: Structure[T]): String = {
      val p = new Prettyprinter
      structure.render(p, x)
      p.toString
    }
  }
}

private[scala] trait Aliases {
  // NOTE: We don't expose any definitions inside scala.meta.prettyprinters
  // as part of this package's public API that will show up in scala.meta.
}
