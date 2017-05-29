package scala.macros

package object semantic extends Api with scala.meta.semantic.Aliases {
  private[scala] type Denotations = scala.meta.semantic.Denotations
  private[scala] type Flags = scala.meta.semantic.Flags
  private[scala] type Mirrors = scala.meta.semantic.Mirrors
  private[scala] type Operations = scala.meta.semantic.Operations
  private[scala] type Semantic = scala.meta.semantic.Semantic
  private[scala] type Symbols = scala.meta.semantic.Symbols
  private[scala] type Types = scala.meta.semantic.Types
}
