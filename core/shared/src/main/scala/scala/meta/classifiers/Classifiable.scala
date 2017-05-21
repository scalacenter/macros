package scala.meta
package classifiers

// NOTE: Marker trait that signifies that the author of the data structure
// allows usage of `.is[T]` and `.isNot[T]` on it.
@scala.annotation.implicitNotFound("don't know how to classify ${T}")
trait Classifiable[T]
