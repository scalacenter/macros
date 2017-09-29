package scala.macros.internal
package engines.dotc
package inputs

import java.lang.ref.WeakReference
import scala.collection.mutable.WeakHashMap
import scala.macros.internal._

class WeakCache[T, U] {
  private val t2u = new WeakHashMap[T, WeakReference[U]]
  private val u2t = new WeakHashMap[U, WeakReference[T]]

  private object CacheEntry {
    def unapply[A](optRef: Option[WeakReference[A]]): Option[A] = {
      if (optRef.nonEmpty) Option(optRef.get.get)
      else None
    }
  }

  private def cache(t: T, u: U) = {
    t2u(t) = new WeakReference(u)
    u2t(u) = new WeakReference(t)
  }

  def getOrElse(t: T)(fn: => U)(implicit ev1: Evidence1): U = {
    t2u.get(t) match {
      case CacheEntry(u) =>
        u
      case _ =>
        val u = fn
        cache(t, u)
        u
    }
  }

  def getOrElse(u: U)(fn: => T)(implicit ev2: Evidence2): T = {
    u2t.get(u) match {
      case CacheEntry(t) =>
        t
      case _ =>
        val t = fn
        cache(t, u)
        t
    }
  }
}
