package scala.macros.internal
package plugins.scalac
package reflect

trait Names { self: ReflectToolkit =>
  import global._

  implicit class XtensionNames(name: Name) {
    def newMacroModuleName: TermName = TermName(name.toString + "$newmacro")
    def newMacroDefName: TermName = TermName(name.toString)
    def newMacroShimName: TermName = TermName(name.toString + "$shim")
    def newMacroImplName: TermName = TermName(name.toString + "$macroExpandWithRuntime")
    def newMacroAbiName: TermName = TermName(name.toString)
  }

  val NewMacroAnnotationParentName = TypeName("MacroAnnotation")
  val NewMacroAnnotationMethodName = TermName("apply")
}
