package scala.macros.tests
package main
package desugared

// TODO: @compileTimeOnly lives elsewhere in 2.10, so codegen for 2.10 should be different.
// In this environment, we can set up compatibility shims, but typically we won't be able to do that.
@_root_.scala.annotation.compileTimeOnly("missing required modules to expand new-style macros")
class main extends _root_.scala.annotation.StaticAnnotation {
  import _root_.scala.language.experimental.macros
  def macroTransform(annottees: Any*): Any = macro main$inline.reflect
}

object main$inline {
  // TODO: There's no blackbox.Context in 2.10, so codegen for 2.10 should be different.
  // In this environment, we can set up compatibility shims, but typically we won't be able to do that.
  def reflect(c: _root_.scala.reflect.macros.blackbox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    var availableMacroEngine = "scalac " + _root_.scala.util.Properties.versionNumberString
    def failMacroEngine(): _root_.scala.Nothing = {
      val requiredMacroEngine = "<scala.macros.config.EngineVersion.toString at the time when this code is generated>"
      var message = "macro cannot be expanded, because it was compiled by an incompatible macro engine"
      message += (_root_.scala.compat.Platform.EOL + " available: " + availableMacroEngine)
      message += (_root_.scala.compat.Platform.EOL + " required : " + requiredMacroEngine)
      c.abort(c.enclosingPosition, message)
    }
    def invokeBackendMethod(moduleName: _root_.java.lang.String, methodName: _root_.java.lang.String, args: _root_.scala.AnyRef*): _root_.scala.AnyRef = {
      try {
        val pluginClassLoader = this.getClass.getClassLoader
        val moduleClass = _root_.java.lang.Class.forName(moduleName, true, pluginClassLoader)
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
      }
    }
    availableMacroEngine = invokeBackendMethod("scala.macros.config.EngineVersion", "toString") match {
      case availableMacroEngine: _root_.java.lang.String => availableMacroEngine
      case _ => failMacroEngine()
    }

    val scalacUniverse = invokeBackendMethod("scala.macros.backends.scalac.ScalacUniverse", "apply", c.universe) match {
      case scalacUniverse: _root_.scala.macros.Universe => scalacUniverse
      case _ => failMacroEngine()
    }
    _root_.scala.macros.internal.withUniverse(scalacUniverse) {
      val this$n = c.macroApplication.asInstanceOf[_root_.scala.macros.Stat]
      val defn = annottees.map(_.asInstanceOf[_root_.scala.macros.Stat]).toList match {
        case _root_.scala.List(tree) => tree
        case trees => _root_.scala.macros.Term.Block(trees)
      }
      val dialect = _root_.scala.macros.Dialect.current
      val expansion = invokeBackendMethod("scala.macros.backends.scalac.ScalacExpansion", "apply", c) match {
        // TODO: We can't say `expansion: _root_.scala.macros.Expansion`,
        // because it produces a pattern matcher warning in 2.12.x.
        case expansion if expansion.isInstanceOf[_root_.scala.macros.Expansion] => expansion.asInstanceOf[_root_.scala.macros.Expansion]
        case _ => failMacroEngine()
      }
      val result = meta(this$n, defn)(dialect, expansion)
      c.Expr[Any](result.asInstanceOf[c.Tree])
    }
  }

  def meta(this$n: _root_.scala.macros.Stat, defn: _root_.scala.macros.Stat)
          (implicit dialect: _root_.scala.macros.Dialect, expansion: _root_.scala.macros.Expansion): _root_.scala.macros.Stat = {
    // TODO: We need explicit qualification of `Template` and `Self`,
    // because otherwise Scala 2.x can't compile the pattern match.
    import scala.macros._
    val Defn.Object(mods, name, scala.macros.Template(Nil, Nil, scala.macros.Self(Name.Anonymous(), None), stats)) = defn
    val main = Defn.Def(
      Nil, Term.Name("main"), Nil,
      List(List(Term.Param(Nil, Term.Name("args"), Some(Type.Apply(Type.Name("Array"), List(Type.Name("String")))), None))),
      Some(Type.Name("Unit")),
      Term.Block(stats))
    Defn.Object(mods, name, Template(Nil, Nil, Self(Name.Anonymous(), None), List(main)))
  }
}