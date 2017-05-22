package scala.meta
package config

import scala.meta.prettyprinters._

// NOTE: This class models the versioning strategy of scalameta.
// It is important to have Version in core to have an explicitly defined
// and programmatically accessible compatibility story.
//
// Stable builds are versioned using semver, e.g. 2.0.0.
// Backwards incompatible changes increment the major version (the first number).
// Other changes increment the minor version (the second number).
// Patch version (the third number) always stays at zero barring exceptional occasions.
//
// Prerelease builds are versioned using semver too, e.g. 2.0.0-707+51be4a51,
// where snapshot metadata (the number after the dash) is the distance from the 1.0.0 release
// and commit metadata (the number after the plus) is the SHA of the current commit.
// If the working copy is dirty, we additionally append the timestamp to build metadata,
// e.g. 2.0.0-707+51be4a51.1495325855697

final case class Version(
    major: Int,
    minor: Int,
    patch: Int,
    snapshot: String,
    commit: String
) extends Pretty {
  protected def syntax(p: Prettyprinter): Unit = {
    p.stx(s"$major.$minor.$patch")
    if (snapshot.nonEmpty) p.stx(s"-$snapshot")
    if (commit.nonEmpty) p.stx(s"+$commit")
  }
}

object Version {
  def parse(s: String): Option[Version] = {
    val rxVersion = """^(\d+)\.(\d+)\.(\d+)(?:-(.*?)(?:\+([0-9a-f]+(?:\.(?:\d+))?))?)?$""".r
    s match {
      case rxVersion(s_major, s_minor, s_patch, s_snapshot, s_commit) =>
        val major = s_major.toInt
        val minor = s_minor.toInt
        val patch = s_patch.toInt
        val snapshot = if (s_snapshot != null) s_snapshot else ""
        val commit = if (s_commit != null) s_commit else ""
        Some(Version(major, minor, patch, snapshot, commit))
      case _ =>
        None
    }
  }
}
