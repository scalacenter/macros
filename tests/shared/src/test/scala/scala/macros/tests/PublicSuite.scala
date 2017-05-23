// TODO: Not being in the `scala` package is crucial.
package other.macros.tests

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.meta.tests.helpers._
import scala.macros._

@RunWith(classOf[JUnit4])
class PublicSuite extends TypecheckHelpers {
  @Test
  def encodingDoesntLeak: Unit = {
    if (scalaVersion.major == 2 && scalaVersion.minor == 10) {
      assertTypecheckError(
        "trait Universe in package macros cannot be accessed in package macros",
        "??? : Universe")
      assertTypecheckError(
        "method abstracts in trait Abstracts cannot be accessed in package macros",
        "abstracts")
      assertTypecheckError(
        "trait Abstracts in trait Abstracts cannot be accessed in object scala.macros.package",
        "??? : Abstracts")
      assertTypecheckError(
        "object companions in trait Companions cannot be accessed in object scala.macros.package",
        "companions")
      assertTypecheckError(
        "trait NameHack in trait Trees cannot be accessed in object scala.macros.package",
        "??? : NameHack")
      assertTypecheckError(
        "trait TermHack in trait Trees cannot be accessed in object scala.macros.package",
        "??? : TermHack")
      assertTypecheckError(
        "trait TypeHack in trait Trees cannot be accessed in object scala.macros.package",
        "??? : TypeHack")
      assertTypecheckError(
        "trait PatHack in trait Trees cannot be accessed in object scala.macros.package",
        "??? : PatHack")
      assertTypecheckError(
        "trait MemberHack in trait Trees cannot be accessed in object scala.macros.package",
        "??? : MemberHack")
      assertTypecheckError(
        "trait DeclHack in trait Trees cannot be accessed in object scala.macros.package",
        "??? : DeclHack")
      assertTypecheckError(
        "trait DefnHack in trait Trees cannot be accessed in object scala.macros.package",
        "??? : DefnHack")
      assertTypecheckError(
        "trait PkgHack in trait Trees cannot be accessed in object scala.macros.package",
        "??? : PkgHack")
      assertTypecheckError(
        "trait CtorHack in trait Trees cannot be accessed in object scala.macros.package",
        "??? : CtorHack")
    } else {
      assertTypecheckError("not found: type Universe", "??? : Universe")
      assertTypecheckError("not found: value abstracts", "abstracts")
      assertTypecheckError("not found: type Abstracts", "??? : Abstracts")
      assertTypecheckError("not found: value companions", "companions")
      assertTypecheckError("not found: type NameHack", "??? : NameHack")
      assertTypecheckError("not found: type TermHack", "??? : TermHack")
      assertTypecheckError("not found: type TypeHack", "??? : TypeHack")
      assertTypecheckError("not found: type PatHack", "??? : PatHack")
      assertTypecheckError("not found: type MemberHack", "??? : MemberHack")
      assertTypecheckError("not found: type DeclHack", "??? : DeclHack")
      assertTypecheckError("not found: type DefnHack", "??? : DefnHack")
      assertTypecheckError("not found: type PkgHack", "??? : PkgHack")
      assertTypecheckError("not found: type CtorHack", "??? : CtorHack")
    }
  }
}
