package scala.macros.internal
package plugins.scalac
package reflect

trait Names { self: ReflectToolkit =>
  import global._

  implicit class XtensionNames(name: Name) {
    def inlineModuleName: TermName = TermName(name.toString + "$inline")
    def inlineMacroName: TermName = TermName(name.toString)
    def inlineShimName: TermName = TermName(name.toString + "$shim")
    def inlineImplName: TermName = TermName(name.toString + "$macroExpandWithRuntime")
  }

  val InlineAnnotationParentName = TypeName("MacroAnnotation")
  val InlineAnnotationMethodName = TermName("apply")
}
