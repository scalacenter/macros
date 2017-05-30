package scala.macros
package inputs

private[macros] trait Api {
  // NOTE: We don't expose any extension methods
  // as part of the package's public API that will show up in scala.macros.
}

private[macros] trait Aliases {
  type Input = scala.macros.inputs.Input
  object Input {
    val None = scala.macros.inputs.Input.None
    type String = scala.macros.inputs.Input.String
    val String = scala.macros.inputs.Input.String
    type Stream = scala.macros.inputs.Input.Stream
    val Stream = scala.macros.inputs.Input.Stream
    type LabeledString = scala.macros.inputs.Input.LabeledString
    val LabeledString = scala.macros.inputs.Input.LabeledString
    type File = scala.macros.inputs.Input.File
    val File = scala.macros.inputs.Input.File
    type Slice = scala.macros.inputs.Input.Slice
    val Slice = scala.macros.inputs.Input.Slice
  }

  type Position = scala.macros.inputs.Position
  object Position {
    val None = scala.macros.inputs.Position.None
    type Range = scala.macros.inputs.Position.Range
    val Range = scala.macros.inputs.Position.Range
  }
}
