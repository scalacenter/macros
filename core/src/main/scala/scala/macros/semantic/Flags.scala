package scala.macros
package semantic

private[macros] trait Flags {
  final val VAL: Long = 1 << 0
  final val VAR: Long = 1 << 1
  final val DEF: Long = 1 << 2
  final val PRIMARYCTOR: Long = 1 << 3
  final val SECONDARYCTOR: Long = 1 << 4
  final val MACRO: Long = 1 << 5
  final val TYPE: Long = 1 << 6
  final val PARAM: Long = 1 << 7
  final val TYPEPARAM: Long = 1 << 8
  final val OBJECT: Long = 1 << 9
  final val PACKAGE: Long = 1 << 10
  final val PACKAGEOBJECT: Long = 1 << 11
  final val CLASS: Long = 1 << 12
  final val TRAIT: Long = 1 << 13
  final val PRIVATE: Long = 1 << 14
  final val PROTECTED: Long = 1 << 15
  final val ABSTRACT: Long = 1 << 16
  final val FINAL: Long = 1 << 17
  final val SEALED: Long = 1 << 18
  final val IMPLICIT: Long = 1 << 19
  final val LAZY: Long = 1 << 20
  final val CASE: Long = 1 << 21
  final val COVARIANT: Long = 1 << 22
  final val CONTRAVARIANT: Long = 1 << 23
}
