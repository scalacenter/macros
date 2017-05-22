package scala.meta
package prettyprinters

trait Pretty extends Product {
  protected def syntax(p: Prettyprinter): Unit

  def syntax: String = {
    val p = new Prettyprinter
    syntax(p)
    p.toString
  }

  protected def structure(p: Prettyprinter): Unit = {
    val ev = Structure.structureProduct[Product]
    ev.render(p, this)
  }

  def structure: String = {
    val p = new Prettyprinter
    structure(p)
    p.toString
  }

  final override def toString = this.syntax
}

private[meta] object Pretty {
  def syntax(pretty: Pretty, prettyprinter: Prettyprinter): Unit = {
    pretty.syntax(prettyprinter)
  }

  def structure(pretty: Pretty, prettyprinter: Prettyprinter): Unit = {
    pretty.structure(prettyprinter)
  }
}
