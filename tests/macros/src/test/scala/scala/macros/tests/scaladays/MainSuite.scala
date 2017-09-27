package scala.macros.tests
package scaladays

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.macros.config._

@RunWith(classOf[JUnit4])
class MainSuite {
  @Test
  def simple: Unit = {
    // TODO: Scalac produces an error if we try to create a main method in a local object.
    // No idea why it does that, but that actually prevents us from testing the @main annotation.
    //
    // var printed = false
    // def println(s: String): Unit = { printed = true }
    // @main
    // object Test {
    //   println("hello world")
    // }
    // Test.main(Array[String]())
    // assertEquals(true, printed)
  }
}
