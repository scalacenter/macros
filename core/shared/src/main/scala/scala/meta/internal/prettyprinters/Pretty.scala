package scala.meta.internal
package prettyprinters

trait Pretty extends Prettyprinted with Product {
  protected def structure(p: Prettyprinter): Unit = {
    val ev = Structure.structureProduct[Product]
    ev.render(p, this)
  }
}