package scala.macros.internal
package prettyprinters

trait Prettyprinted {
  protected def syntax(p: Prettyprinter): Unit

  def syntax: String = {
    val p = new Prettyprinter
    syntax(p)
    p.toString
  }

  protected def structure(p: Prettyprinter): Unit

  def structure: String = {
    val p = new Prettyprinter
    structure(p)
    p.toString
  }

  final override def toString = this.syntax
}

private[macros] object Prettyprinted {
  def syntax(prettypinted: Prettyprinted, prettyprinter: Prettyprinter): Unit = {
    prettypinted.syntax(prettyprinter)
  }

  def structure(prettypinted: Prettyprinted, prettyprinter: Prettyprinter): Unit = {
    prettypinted.structure(prettyprinter)
  }
}
