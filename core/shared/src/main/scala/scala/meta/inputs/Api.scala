package scala.meta
package inputs

private[scala] trait Api {
  // NOTE: We don't expose any extension methods
  // as part of the package's public API that will show up in scala.meta.
}

private[scala] trait Aliases {
  type Input = scala.meta.inputs.Input
  object Input {
    val None = scala.meta.inputs.Input.None
    type String = scala.meta.inputs.Input.String
    val String = scala.meta.inputs.Input.String
    type Stream = scala.meta.inputs.Input.Stream
    val Stream = scala.meta.inputs.Input.Stream
    type LabeledString = scala.meta.inputs.Input.LabeledString
    val LabeledString = scala.meta.inputs.Input.LabeledString
    type File = scala.meta.inputs.Input.File
    val File = scala.meta.inputs.Input.File
    type Slice = scala.meta.inputs.Input.Slice
    val Slice = scala.meta.inputs.Input.Slice
  }

  type Position = scala.meta.inputs.Position
  object Position {
    val None = scala.meta.inputs.Position.None
    type Range = scala.meta.inputs.Position.Range
    val Range = scala.meta.inputs.Position.Range
  }
}
