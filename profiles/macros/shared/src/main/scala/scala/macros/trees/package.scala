package scala.macros

package object trees extends Api with scala.meta.trees.Aliases {
  private[scala] type Abstracts = scala.meta.trees.Abstracts
  private[scala] type Companions = scala.meta.trees.Companions
  private[scala] type Extensions = scala.meta.trees.Extensions
  private[scala] type Trees = scala.meta.trees.Trees
}
