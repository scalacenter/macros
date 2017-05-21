package org.scalameta
package build

import sbt._

trait Ci { self: ScalametaBuild =>

  // NOTE: Invokes given commands in a CI environment, meaning:
  //   * Commands are invoked with scalaVersion set to ScalaVersion
  //   * Commands are aggregated across projects that support ScalaVersion
  //   * Commands are enabled only if DRONE_$command environment variable is set to true
  def CiCommand(name: String, commands: List[String]): Command = Command.command(name) { state0 =>
    val enabledCommands = commands.filter(command => {
      val envVar = "DRONE_" + command.toUpperCase
      sys.env.getOrElse(envVar, "false") == "true"
    })
    enabledCommands.foldLeft(state0) {
      case (state, command) => s"plz $ScalaVersion $command" :: state
    }
  }
}
