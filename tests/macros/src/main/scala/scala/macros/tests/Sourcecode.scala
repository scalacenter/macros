package scala.macros.tests

import scala.macros._
import scala.language.implicitConversions

// Unification of macro impl/defn forces us to put utilities in separate object
object SourceCodeUtil {
  // TODO(olafur) replace with more typesafe typeOf[Line]
  private def root: Term =
    Term.Name("_root_").select("scala" :: "macros" :: "tests" :: Nil)
  def prefix(name: String): Term =
    root.select(name :: Nil)

  // Should this be handled by engines? I would claim that this is application specific
  // for sourcecode.
  def isSynthetic(s: Symbol)(implicit m: Mirror): Boolean = {
    val name = s.name
    name == "<init>" ||
    name == "<root>" ||
    (name.startsWith("<local ") && name.endsWith(">"))
  }

  def symbolName(s: Symbol)(implicit m: Mirror): String = s.name.stripSuffix("$")
}

case class File(value: String)
object File {
  implicit def generate: File = macro {
    val path = enclosingPosition.input.path.toAbsolutePath.toString
    SourceCodeUtil.prefix("File").apply(Lit.String(path) :: Nil)
  }
}

case class Line(value: Int)
object Line {
  implicit def generate: Line = macro {
    SourceCodeUtil.prefix("Line").apply(Lit.Int(enclosingPosition.line) :: Nil)
  }
}

case class Name(value: String)
object Name {
  implicit def generate: Name = macro {
    def loop(s: Symbol): Symbol =
      if (SourceCodeUtil.isSynthetic(s)) s.owner.fold(s)(loop)
      else s
    val owner = loop(enclosingOwner)
    SourceCodeUtil.prefix("Name").apply(Lit.String(SourceCodeUtil.symbolName(owner)) :: Nil)
  }
}

case class FullName(value: String)
object FullName {
  implicit def generate: FullName = macro {
    def loop(sym: Symbol): Vector[Symbol] = {
      val owner = sym.owner.fold(Vector.empty[Symbol])(loop)
      if (!SourceCodeUtil.isSynthetic(sym)) {
        owner :+ sym
      } else owner
    }
    val names = loop(enclosingOwner)
    val fullName = names.map(SourceCodeUtil.symbolName).mkString(".")
    SourceCodeUtil.prefix("FullName").apply(Lit.String(fullName) :: Nil)
  }
}

case class Text[T](source: String, value: T)
object Text {
  implicit def generate[T](e: T): Text[T] = macro {
    // NOTE(olafur): lihaoyi/sourcecode uses unit parser and slices
    // start/end offsets from input.content:
    // https://github.com/lihaoyi/sourcecode/blob/420bea7941d3219e2f1200b14b11010843aea39c/sourcecode/shared/src/main/scala/sourcecode/SourceContext.scala#L137-L140
    // This produces more accurate/portable results.
    val source = e.syntax
    SourceCodeUtil.prefix("Text").apply(Lit.String(source) :: e.splice :: Nil)
  }
}
