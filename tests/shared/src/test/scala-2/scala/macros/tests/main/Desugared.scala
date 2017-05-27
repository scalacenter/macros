// NOTE: This is a manually desugared version of the macro commented out below.
// `plugins/scalac` does this transformation automatically.
//
// import scala.macros._
// class main extends MacroAnnotation {
//   inline def apply(defn: Any): Any = meta {
//     val q"..$mods object $name { ..$stats }" = defn
//     val main = q"def main(arg: Array[String]): Unit = ..$stats"
//     q"..$mods object $name { $main }"
//   }
// }

package scala.macros.tests
package main
package desugared

import scala.macros._

class main extends MacroAnnotation {
  import _root_.scala.language.experimental.macros
  def macroTransform(annottees: Any*): Any = macro main$inline.apply$shim
}

object main$inline {
  // TODO: There's no blackbox.Context in 2.10, so codegen for 2.10 should be different.
  // In this environment, we can set up compatibility shims, but typically we won't be able to do that.
  def apply$shim(c: _root_.scala.reflect.macros.blackbox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    var foundEngine = "scalac " + _root_.scala.util.Properties.versionNumberString
    def failMacroEngine(): _root_.scala.Nothing = {
      val requiredEngine = "<engineVersion>"
      var msg = "macro cannot be expanded, because it was compiled by an incompatible engine"
      msg += (_root_.scala.meta.internal.prettyprinters.EOL + " found   : " + foundEngine)
      msg += (_root_.scala.meta.internal.prettyprinters.EOL + " required: " + requiredEngine)
      c.abort(c.enclosingPosition, msg)
    }
    def invokeBackendMethod(
        moduleName: _root_.java.lang.String,
        methodName: _root_.java.lang.String,
        args: _root_.scala.AnyRef*): _root_.scala.AnyRef = {
      try {
        val pluginClassLoader = this.getClass.getClassLoader
        val moduleClass = pluginClassLoader.loadClass(moduleName + "$")
        val moduleField = moduleClass.getDeclaredField("MODULE$")
        val module = moduleField.get(null)
        val methods = module.getClass.getDeclaredMethods().filter(_.getName == methodName).toList
        methods match {
          case List(method) => method.invoke(module, args: _*)
          case _ => failMacroEngine()
        }
      } catch {
        case _: _root_.java.lang.ClassNotFoundException => failMacroEngine()
        case _: _root_.java.lang.NoSuchFieldException => failMacroEngine()
        case _: _root_.java.lang.IllegalAccessException => failMacroEngine()
        case _: _root_.java.lang.IllegalArgumentException => failMacroEngine()
        case ex: _root_.java.lang.reflect.InvocationTargetException => throw ex
      }
    }
    invokeBackendMethod("scala.macros.internal.config.package", "engineVersion") match {
      case version: _root_.scala.macros.Version => foundEngine = version.toString
      case _ => failMacroEngine()
    }

    val ScalaUniverse = "scala.macros.internal.engines.scalac.Universe"
    val scalacUniverse = invokeBackendMethod(ScalaUniverse, "apply", c.universe) match {
      case scalacUniverse: _root_.scala.macros.Universe => scalacUniverse
      case _ => failMacroEngine()
    }
    _root_.scala.macros.internal.withUniverse(scalacUniverse) {
      val prefix$1 = c.macroApplication.asInstanceOf[_root_.scala.macros.Stat]
      val defn$1 = annottees.map(_.asInstanceOf[_root_.scala.macros.Stat]) match {
        case _root_.scala.Seq(tree) => tree
        case trees => _root_.scala.macros.Term.Block(trees.toList)
      }
      val dialect$1 = _root_.scala.macros.Dialect.current
      val ScalacExpansion = "scala.macros.backends.scalac.Expansion"
      val expansion$1 = invokeBackendMethod(ScalacExpansion, "apply", c) match {
        // TODO: We can't say `expansion: _root_.scala.macros.Expansion`,
        // because it produces a pattern matcher warning in 2.12.x.
        case expansion if expansion.isInstanceOf[_root_.scala.macros.Expansion] =>
          expansion.asInstanceOf[_root_.scala.macros.Expansion]
        case _ =>
          failMacroEngine()
      }
      val result = apply(prefix$1, defn$1)(dialect$1, expansion$1)
      c.Expr[_root_.scala.Any](result.asInstanceOf[c.Tree])
    }
  }

  def apply(prefix$1: _root_.scala.macros.Stat, defn: _root_.scala.macros.Stat)
           (implicit dialect$1: _root_.scala.macros.Dialect, expansion$1: _root_.scala.macros.Expansion): _root_.scala.macros.Stat = {
    // TODO: We need explicit qualification of `Template` and `Self`,
    // because otherwise Scala 2.x can't compile the pattern match.
    val Defn.Object(mods, name, scala.macros.Template(Nil, Nil, scala.macros.Self(Name.Anonymous(), None), stats)) = defn
    val main = Defn.Def(
      Nil, Term.Name("main"), Nil,
      List(List(Term.Param(Nil, Term.Name("args"), Some(Type.Apply(Type.Name("Array"), List(Type.Name("String")))), None))),
      Some(Type.Name("Unit")),
      Term.Block(stats))
    Defn.Object(mods, name, Template(Nil, Nil, Self(Name.Anonymous(), None), List(main)))
  }
}