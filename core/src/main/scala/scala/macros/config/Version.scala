package scala.macros
package config

// NOTE: This class models the versioning strategy of Scala macros.
// It is important to have Version in core to have an explicitly defined
// and programmatically accessible compatibility story.
//
// Stable builds are versioned using semver, e.g. 2.0.0.
// Backwards incompatible changes increment the major version (the first number).
// Other changes increment the minor version (the second number).
// Patch version (the third number) always stays at zero barring exceptional occasions.
//
// Prerelease builds are versioned using semver too, e.g. 2.0.0-707-51be4a51,
// where snapshot metadata (the number after the dash) is the distance from the 1.0.0 release
// concatenated with the SHA of the current commit.
// If the working copy is dirty, we additionally append the timestamp to snapshot metadata,
// e.g. 2.0.0-707-51be4a51.1495325855697

final case class Version(
    major: Int,
    minor: Int,
    patch: Int,
    snapshot: String,
    build: String
) {
  override def toString: String = {
    val sb = new StringBuilder
    sb.append(s"$major.$minor.$patch")
    if (snapshot.nonEmpty) sb.append(s"-$snapshot")
    if (build.nonEmpty) sb.append(s"+$build")
    sb.toString()
  }
}

object Version {
  def parse(s: String): Option[Version] = {
    val rxVersion = """^(\d+)\.(\d+)\.(\d+)(?:-([0-9A-Za-z-\.]+)(?:\+([0-9A-Za-z-\.]+))?)?$""".r
    s match {
      case rxVersion(s_major, s_minor, s_patch, s_snapshot, s_build) =>
        val major = s_major.toInt
        val minor = s_minor.toInt
        val patch = s_patch.toInt
        val snapshot = if (s_snapshot != null) s_snapshot else ""
        val build = if (s_build != null) s_build else ""
        Some(Version(major, minor, patch, snapshot, build))
      case _ =>
        None
    }
  }
}
