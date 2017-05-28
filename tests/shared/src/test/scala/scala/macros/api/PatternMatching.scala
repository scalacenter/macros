package scala.macros.tests
package api

// NOTE: This file doesn't contain any test suites.
// It just checks that we're unaffected by https://github.com/scalameta/scalameta/issues/900
// and other pattern matching gotchas.

object PatternMatching {
  import scala.macros._
  (null: AnyRef) match { case _: Version => }
  (null: AnyRef) match { case Version(_, _, _, _, _) => }
  (null: AnyRef) match { case _: Dialect => }
  // (null: AnyRef) match { case Dialect(_, _, _, ...) => }
  (null: AnyRef) match { case _: Input => }
  (null: AnyRef) match { case Input.None => }
  (null: AnyRef) match { case _: Input.String => }
  (null: AnyRef) match { case Input.String(_) => }
  (null: AnyRef) match { case _: Input.Stream => }
  (null: AnyRef) match { case Input.Stream(_, _) => }
  (null: AnyRef) match { case _: Input.LabeledString => }
  (null: AnyRef) match { case Input.LabeledString(_, _) => }
  (null: AnyRef) match { case _: Input.File => }
  (null: AnyRef) match { case Input.File(_, _) => }
  (null: AnyRef) match { case _: Input.Slice => }
  (null: AnyRef) match { case Input.Slice(_, _, _) => }
  (null: AnyRef) match { case _: Position => }
  (null: AnyRef) match { case Position.None => }
  (null: AnyRef) match { case _: Position.Range => }
  (null: AnyRef) match { case Position.Range(_, _, _) => }
  (null: AnyRef) match { case _: AbsolutePath => }
  // (null: AnyRef) match { case _: Term.This => }
  (null: AnyRef) match { case Term.This(_) => }
}
