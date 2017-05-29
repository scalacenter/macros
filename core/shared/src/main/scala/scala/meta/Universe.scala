package scala.meta

import scala.meta.semantic._
import scala.meta.trees._

private[scala] trait Universe extends Semantic with Trees {
  private[scala] type Abstracts <: TreeAbstracts with MirrorAbstracts
  private[scala] def abstracts: Abstracts
}
