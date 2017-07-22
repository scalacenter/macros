package scala.macros
package internal

import scala.annotation.StaticAnnotation

class inlineMetadata(val coreVersion: String, val engineVersion: String) extends StaticAnnotation
