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
    type File = scala.macros.inputs.Input.File
    val File = scala.macros.inputs.Input.File
    type VirtualFile = scala.macros.inputs.Input.VirtualFile
    val VirtualFile = scala.macros.inputs.Input.VirtualFile
  }

  type Position = scala.macros.inputs.Position
  object Position {
    val None = scala.macros.inputs.Position.None
    type Range = scala.macros.inputs.Position.Range
    val Range = scala.macros.inputs.Position.Range
  }
}
