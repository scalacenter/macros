package scala.macros.tests
package config

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.macros.config._

@RunWith(classOf[JUnit4])
class MetadataSuite {
  @Test
  def scalaVersionOk: Unit = {
    assertNotEquals("", scalaVersion)
  }

  @Test
  def coreVersionOk: Unit = {
    assertNotEquals("", coreVersion)
  }
}