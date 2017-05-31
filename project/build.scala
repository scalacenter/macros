package org.scalamacros

import org.scalamacros.build._

trait ScalamacrosBuild extends Ci with Products with Versions
package object build extends ScalamacrosBuild
