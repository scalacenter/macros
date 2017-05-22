package scala.meta
package io

private[meta] trait Api {}

private[meta] trait Aliases {
  type AbsolutePath = scala.meta.io.AbsolutePath
  lazy val AbsolutePath = scala.meta.io.AbsolutePath
}
