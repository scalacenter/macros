package scala.macros

import scala.meta._

private[scala] trait Api
    extends classifiers.Api
    with config.Api
    with dialects.Api
    with inputs.Api
    with scala.meta.io.Api
    with prettyprinters.Api

private[scala] trait Aliases
    extends classifiers.Aliases
    with config.Aliases
    with dialects.Aliases
    with inputs.Aliases
    with scala.meta.io.Aliases
    with prettyprinters.Aliases
