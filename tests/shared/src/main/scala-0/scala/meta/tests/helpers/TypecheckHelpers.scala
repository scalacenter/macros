package scala.meta.tests
package helpers

import org.junit.Assume._

trait TypecheckHelpers {
  def assertTypecheckError(error: String, code: String): Unit = {
    assumeTrue("assertTypecheckError can be implemented in Dotty", false)
    ???
  }
}
