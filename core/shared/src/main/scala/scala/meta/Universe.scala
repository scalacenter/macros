package scala.meta

import scala.meta.trees._

private[scala] trait Universe extends Trees {
  private[scala] type Abstracts <: TreeAbstracts
  private[scala] def abstracts: Abstracts
}
