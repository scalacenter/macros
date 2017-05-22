package scala.meta
package classifiers

private[scala] trait Api {
  implicit class XtensionClassifiable[T](x: T) {
    def is[U](implicit classifiable: Classifiable[T], classifier: Classifier[T, U]): Boolean = {
      classifier.apply(x)
    }

    def isNot[U](implicit classifiable: Classifiable[T], classifier: Classifier[T, U]): Boolean = {
      !this.is(classifiable, classifier)
    }
  }
}

private[scala] trait Aliases {
  // NOTE: We don't expose any definitions inside scala.meta.classifiers
  // as part of this package's public API that will show up in scala.meta.
}
