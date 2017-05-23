package scala.macros.internal
package plugins.scalac
package parser

import scala.tools.nsc.interpreter.{ReplGlobal => NscReplGlobal, _}
import parser.{SyntaxAnalyzer => PluginSyntaxAnalyzer}

trait ReplGlobal extends NscReplGlobal {
  // TODO: classloader happy meal!!
  // can't cast analyzer to PluginSyntaxAnalyzer and use newUnitScanner/newUnitParser because of a classloader mismatch :O
  import syntaxAnalyzer.{UnitScanner, UnitParser}
  override def newUnitParser(unit: CompilationUnit): UnitParser = {
    val m_newUnitParser = syntaxAnalyzer.getClass.getMethods.find(_.getName == "newUnitParser").get
    m_newUnitParser.invoke(syntaxAnalyzer, unit).asInstanceOf[UnitParser]
  }
}
