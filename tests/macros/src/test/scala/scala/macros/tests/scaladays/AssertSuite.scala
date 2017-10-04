package scala.macros.tests.scaladays

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
@RunWith(classOf[JUnit4])
class AssertSuite {
  @Test def litBoolean(): Unit = Assert.assert(true)
  val a = 1
  val b = 1
  @Test def eq(): Unit = Assert.assert(a == b)
}
