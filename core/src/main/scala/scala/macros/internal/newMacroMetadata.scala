package scala.macros
package internal

import scala.annotation.StaticAnnotation

class newMacroMetadata(val coreVersion: String, val engineVersion: String) extends StaticAnnotation
