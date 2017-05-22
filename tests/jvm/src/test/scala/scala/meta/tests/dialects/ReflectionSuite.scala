package scala.meta.tests
package dialects

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.meta.Dialect

@RunWith(classOf[JUnit4])
class StandardSuite {
  @Test
  def exhaustivity: Unit = {
    val dialects = scala.meta.dialects.`package`
    def isDialectGetter(m: java.lang.reflect.Method) =
      m.getParameterTypes.isEmpty && m.getReturnType == classOf[Dialect]
    val dialectGetters = dialects.getClass.getDeclaredMethods.filter(isDialectGetter)
    val reflectiveStandards =
      dialectGetters.map(m => m.invoke(dialects).asInstanceOf[Dialect] -> m.getName).toMap
    assertEquals(Dialect.standards, reflectiveStandards)
  }
}
