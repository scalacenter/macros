package scala.meta.internal
package io

object CorePathIO {
  def isAbsolutePath(s: String) =
    PlatformCorePathIO.isAbsolutePath(s)
}