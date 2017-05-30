// TODO: Not being in the `scala` package is crucial.
package other.macros.tests

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.macros.tests.helpers._
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
        "method abstracts in trait Expansions cannot be accessed in package macros",
        "abstracts")
      assertTypecheckError(
        "type Abstracts in trait Universe cannot be accessed in package macros",
        "??? : Abstracts")
      assertTypecheckError(
        "trait TreeAbstracts in trait Abstracts cannot be accessed in object scala.macros.package",
        "??? : TreeAbstracts")
      assertTypecheckError(
        "value treeCompanions in trait Companions cannot be accessed in object scala.macros.package",
        "treeCompanions")
      assertTypecheckError(
        "trait TreeCompanions in trait Companions cannot be accessed in object scala.macros.package",
        "??? : TreeCompanions")
      assertTypecheckError(
        "trait NameCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : NameCompanion")
      assertTypecheckError(
        "trait LitCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : LitCompanion")
      assertTypecheckError(
        "trait TermCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : TermCompanion")
      assertTypecheckError(
        "trait TypeCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : TypeCompanion")
      assertTypecheckError(
        "trait PatCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : PatCompanion")
      assertTypecheckError(
        "trait MemberCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : MemberCompanion")
      assertTypecheckError(
        "trait DeclCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : DeclCompanion")
      assertTypecheckError(
        "trait DefnCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : DefnCompanion")
      assertTypecheckError(
        "trait PkgCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : PkgCompanion")
      assertTypecheckError(
        "trait CtorCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : CtorCompanion")
      assertTypecheckError(
        "trait ModCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : ModCompanion")
      assertTypecheckError(
        "trait EnumeratorCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : EnumeratorCompanion")
      assertTypecheckError(
        "trait ImporteeCompanion in trait Trees cannot be accessed in object scala.macros.package",
        "??? : ImporteeCompanion")
    } else {
      assertTypecheckError("not found: type Universe", "??? : Universe")
      assertTypecheckError("not found: value abstracts", "abstracts")
      assertTypecheckError("not found: type Abstracts", "??? : Abstracts")
      assertTypecheckError("not found: type TreeAbstracts", "??? : TreeAbstracts")
      assertTypecheckError("not found: value treeCompanions", "treeCompanions")
      assertTypecheckError("not found: type TreeCompanions", "??? : TreeCompanions")
      assertTypecheckError("not found: type NameCompanion", "??? : NameCompanion")
      assertTypecheckError("not found: type LitCompanion", "??? : LitCompanion")
      assertTypecheckError("not found: type TermCompanion", "??? : TermCompanion")
      assertTypecheckError("not found: type TypeCompanion", "??? : TypeCompanion")
      assertTypecheckError("not found: type PatCompanion", "??? : PatCompanion")
      assertTypecheckError("not found: type MemberCompanion", "??? : MemberCompanion")
      assertTypecheckError("not found: type DeclCompanion", "??? : DeclCompanion")
      assertTypecheckError("not found: type DefnCompanion", "??? : DefnCompanion")
      assertTypecheckError("not found: type PkgCompanion", "??? : PkgCompanion")
      assertTypecheckError("not found: type CtorCompanion", "??? : CtorCompanion")
      assertTypecheckError("not found: type ModCompanion", "??? : ModCompanion")
      assertTypecheckError("not found: type EnumeratorCompanion", "??? : EnumeratorCompanion")
      assertTypecheckError("not found: type ImporteeCompanion", "??? : ImporteeCompanion")
    }
  }
}
