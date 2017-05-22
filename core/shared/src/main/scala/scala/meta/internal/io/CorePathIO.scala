package scala.meta
package internal
package io

object CorePathIO {
  def isAbsolutePath(s: String) =
    PlatformCorePathIO.isAbsolutePath(s)
}