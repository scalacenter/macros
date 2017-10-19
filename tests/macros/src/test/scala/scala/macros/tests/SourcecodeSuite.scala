package scala.macros.tests

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert._
import org.junit.runners.JUnit4

@RunWith(classOf[JUnit4])
class SourcecodeSuite {
  @Test def file(): Unit = {
    val obtained = File.generate.value
    assertTrue(obtained, obtained.endsWith("SourcecodeSuite.scala"))
    val implicitObtained = implicitly[File].value
    assertEquals(implicitObtained, obtained)
  }

  @Test def line(): Unit = {
    assertEquals(18, Line.generate.value)
    assertEquals(19, implicitly[Line].value)
  }

  @Test def name(): Unit = {
    val a = Name.generate.value
    assertEquals("a", a)
    def b = Name.generate.value
    assertEquals("b", b)
    var c = Name.generate.value
    assertEquals("c", c)
    class d(implicit val name: Name)
    object e extends d
    assertEquals("e", e.name.value)
  }

  @Test def fullName(): Unit = {
    val a = FullName.generate.value
    val prefix = "scala.macros.tests.SourcecodeSuite.fullName"
    assertEquals(s"$prefix.a", a)
    def b = FullName.generate.value
    assertEquals(s"$prefix.b", b)
    var c = FullName.generate.value
    assertEquals(s"$prefix.c", c)
    c = null // silence warnings about c being var but never getting
    class d(implicit val name: FullName)
    object e extends d
    assertEquals(s"$prefix.e", e.name.value)
  }

  @Test def text(): Unit = {
    val a = 1
    val aText = Text.generate(a)
    assertEquals(s"a", aText.source)
    val bText = Text.generate(a + a)
    // lihaoyi/sourcecode will produce `a + a` here.
    assertEquals(s"a.+(a)", bText.source)
    def log[T](a: Text[T]) = a
    val b = log(a - a)
    assertEquals(0, b.value)
    assertEquals(s"a.-(a)", b.source)
  }
}
