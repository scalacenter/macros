package org.scalameta

import org.scalameta.build._

trait ScalametaBuild extends Ci with Versions
package object build extends ScalametaBuild
