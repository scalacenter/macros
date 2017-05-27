package scala.meta.internal
package trees

import java.util.concurrent.atomic._
import scala.meta.trees.Trees

trait Gensym { self: Trees =>
  private val atomicInteger = new AtomicInteger()

  def gensym(prefix: String): String = {
    val normalizedPrefix = if (!prefix.endsWith("$")) prefix + "$" else prefix
    normalizedPrefix + atomicInteger.incrementAndGet()
  }
}
