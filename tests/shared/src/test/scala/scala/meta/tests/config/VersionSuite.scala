package scala.meta.tests
package config

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.meta.config._

@RunWith(classOf[JUnit4])
class VersionSuite {
  @Test
  def parseStable: Unit = {
    val s = "2.0.0"
    val version = Version.parse(s).get
    assertEquals(2, version.major)
    assertEquals(0, version.minor)
    assertEquals(0, version.patch)
    assertEquals("", version.snapshot)
    assertEquals("", version.commit)
    assertEquals(s, version.toString)
  }

  @Test
  def parseSnapshotWithoutCommit: Unit = {
    val s = "2.0.0-707"
    val version = Version.parse(s).get
    assertEquals(2, version.major)
    assertEquals(0, version.minor)
    assertEquals(0, version.patch)
    assertEquals("707", version.snapshot)
    assertEquals("", version.commit)
    assertEquals(s, version.toString)
  }

  @Test
  def parseSnapshotWithSha: Unit = {
    val s = "2.0.0-707+51be4a51"
    val version = Version.parse(s).get
    assertEquals(2, version.major)
    assertEquals(0, version.minor)
    assertEquals(0, version.patch)
    assertEquals("707", version.snapshot)
    assertEquals("51be4a51", version.commit)
    assertEquals(s, version.toString)
  }

  @Test
  def parseSnapshotWithShaAndTimestamp: Unit = {
    val s = "2.0.0-707+51be4a51.1495325855697"
    val version = Version.parse(s).get
    assertEquals(2, version.major)
    assertEquals(0, version.minor)
    assertEquals(0, version.patch)
    assertEquals("707", version.snapshot)
    assertEquals("51be4a51.1495325855697", version.commit)
    assertEquals(s, version.toString)
  }
}