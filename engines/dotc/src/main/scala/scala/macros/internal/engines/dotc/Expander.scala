package scala.macros.internal.engines.dotc

import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.ast.untpd
import dotty.tools.dotc.core.Constants.Constant
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Names.Name
import dotty.tools.dotc.core.Types.NamedType
import dotty.tools.dotc.core.Types.TermRef
import dotty.tools.dotc.core.Decorators._
import dotty.tools.dotc.core.Symbols.Symbol

object Expander {
  object ExtractApply {
    def unapply(
        tree: tpd.Tree
    ): Option[(tpd.Tree, List[tpd.Tree], List[List[tpd.Tree]])] =
      tree match {
        case tree: tpd.TypeApply =>
          Some((tree.fun, tree.args, Nil))
        case tree: tpd.Apply =>
          val Some((f, targs, argss)) = unapply(tree.fun)
          Some((f, targs, argss :+ tree.args))
        case _ =>
          Some((tree, Nil, Nil))
      }
  }

  object MethodSelect {
    def unapply(tree: tpd.Tree): Option[(tpd.Tree, Name)] = tree match {
      case tree: tpd.Select => Some((tree.qualifier, tree.name))
      case tree: tpd.Ident => Some((null, tree.name))
      case _ => None
    }
  }

  def javaClassName(classSymbol: Symbol)(implicit ctx: Context): String = {
    val enclosingPackage = classSymbol.enclosingPackageClass
    if (enclosingPackage.isEffectiveRoot) {
      classSymbol.flatName.toString
    } else {
      enclosingPackage.showFullName + "." + classSymbol.flatName
    }
  }

  /** Expand def macros */
  def expandDefMacro(tree: tpd.Tree)(implicit ctx: Context): untpd.Tree =
    tree match {
      case ExtractApply(methodSelect @ MethodSelect(prefix, method), targs, argss) =>
        println("expanding {" + tree.show + "}")
        val methodOwner = methodSelect.symbol.owner
        val className = if (methodOwner.isPackageObject) {
          // if macro is defined in a package object
          // the implementation is located relative to the package not the `package$` module
          methodOwner.owner.showFullName + "$" + "$inline"
        } else {
          javaClassName(methodOwner) + "$inline"
        }

        // reflect macros definition
        val moduleClass = ctx.classloader.loadClass(className)
        val impl = moduleClass
          .getDeclaredMethods()
          .find(_.getName == method.encode.show)
          .get
        impl.setAccessible(true)

        val tb = new Universe(tree)
        val prefix2 =
          if (prefix == null)
            tpd.ref(
              methodSelect.tpe
                .asInstanceOf[TermRef]
                .prefix
                .asInstanceOf[NamedType]
            )
          else prefix
        val trees = prefix2 :: targs ++ argss.flatten
        try {
          val res = macros.internal.withUniverse(tb) {
            impl.invoke(null, trees: _*).asInstanceOf[untpd.Tree]
          }
          println(" => {" + res.show + "}")
          res
        } catch {
          case e: Exception =>
            ctx.error("error occurred while expanding macro: \n" + e.getMessage, tree.pos)
            e.printStackTrace()
            untpd.Literal(Constant(null)).withPos(tree.pos)
        }
      case _ =>
        ctx.warning(s"Unknown macro expansion: $tree")
        tree
    }
}
