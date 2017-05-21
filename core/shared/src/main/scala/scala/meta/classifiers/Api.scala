package scala.meta
package classifiers

private[meta] trait Api {
  implicit class XtensionClassifiable[T](x: T) {
    def is[U](implicit classifiable: Classifiable[T], classifier: Classifier[T, U]): Boolean = {
      classifier.apply(x)
    }

    def isNot[U](implicit classifiable: Classifiable[T], classifier: Classifier[T, U]): Boolean = {
      !this.is(classifiable, classifier)
    }
  }
}

private[meta] trait Aliases {
}
