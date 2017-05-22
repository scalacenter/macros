package scala.meta.tests
package prettyprinters

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.meta.config._
import scala.meta.prettyprinters._

@RunWith(classOf[JUnit4])
class VersionSuite {
  @Test
  def noSnapshot: Unit = {
    val v = Version.parse("2.0.0").get
    assertEquals("""2.0.0""", v.syntax)
    assertEquals("""Version(2, 0, 0, "", "")""", v.structure)
  }

  @Test
  def yesSnapshotNoBuild: Unit = {
    val v = Version.parse("2.0.0-707").get
    assertEquals("""2.0.0-707""", v.syntax)
    assertEquals("""Version(2, 0, 0, "707", "")""", v.structure)
  }

  @Test
  def yesSnapshotYesBuildShort: Unit = {
    val v = Version.parse("2.0.0-707+51be4a51").get
    assertEquals("""2.0.0-707+51be4a51""", v.syntax)
    assertEquals("""Version(2, 0, 0, "707", "51be4a51")""", v.structure)
  }

  @Test
  def yesSnapshotYesBuildFull: Unit = {
    val v = Version.parse("2.0.0-707+51be4a51.1495325855697").get
    assertEquals("""2.0.0-707+51be4a51.1495325855697""", v.syntax)
    assertEquals("""Version(2, 0, 0, "707", "51be4a51.1495325855697")""", v.structure)
  }
}