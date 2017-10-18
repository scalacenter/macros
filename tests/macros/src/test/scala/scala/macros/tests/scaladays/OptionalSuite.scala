package scala.macros.tests.scaladays

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert._

@RunWith(classOf[JUnit4])
class OptionalSuite {
  def test(msg: String)(body: => Unit): Unit = body

  class C {
    override def toString: String = "C"
  }

  @Test def t1(): Unit =
    test("getOrElse") {
      val opt = new Optional[String]("hello")
      assert(opt.getOrElse("world") == "hello")

      val opt2 = new Optional[String](null)
      assert(opt2.getOrElse("hello") == "hello")
    }

  @Test def t2(): Unit =
    test("map") {
      val opt = new Optional[String]("hello")
      assert(opt.map(_ + " world") == new Optional("hello world"))

      val opt2 = new Optional[String](null)
      assert(opt2.map(_ + " world") == new Optional(null))
    }

  @Test def t3(): Unit =
    test("simple getOrElse") {
      val c1 = new C
      val c2 = new C
      val c3 = new C
      var sideEffect = 0

      val x = new Optional(c1)
      val x1 = x.getOrElse(c2)
      assert(x1 == c1)
      assert(sideEffect == 0)

      val y = new Optional[C](null)
      val y1 = y.getOrElse({ sideEffect += 1; c3 })
      assert(y1 == c3)
      assert(sideEffect == 1)
    }

  @Test def t4(): Unit =
    test("don't duplicate side-effects of the prefix") {
      val c1 = new C
      val c2 = new C
      var sideEffect = 0

      def x = { sideEffect += 1; new Optional(c1) }
      val x1 = x.getOrElse(c2)
      assert(sideEffect == 1)
    }

  @Test def t5(): Unit =
    test("hygiene") {
      val temp = 100
      new Optional(if (temp < 100) new C else null).getOrElse(new C)
    }

  @Test def t6(): Unit =
    test("owner chain corruption") {
      def foo(x: => Optional[C]) = x
      foo({ val y = new Optional(null); y }).getOrElse(new C)
    }

  @Test def t7(): Unit =
    test("typed/untyped mixup") {
      val x1 = new Optional(new C)
      val x2 = x1.map(_.toString)
      assertEquals("C", x2.value)
    }

//  @Test def t8(): Unit = test("the final thing") {
//    def foo(f: => C): C = f
//    val x1 = new Optional(new C)
//    val x2 = x1.map(x => foo({ val y = x; y }))
//  }
}
