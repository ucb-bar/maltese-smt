import mill._
import mill.scalalib._
import mill.scalalib.publish._
import mill.scalalib.scalafmt._
import coursier.maven.MavenRepository

object maltese extends mill.Cross[malteseCrossModule]("2.12.13", "2.13.5")

class malteseCrossModule(val crossScalaVersion: String) extends CrossScalaModule with ScalafmtModule with PublishModule {
  override def repositoriesTask = T.task {
    super.repositoriesTask() ++ Seq(
      MavenRepository("https://oss.sonatype.org/content/repositories/snapshots")
    )
  }

  override def millSourcePath = super.millSourcePath / os.up

  // 2.12.12 -> Array("2", "12", "12") -> "12" -> 12
  private def majorVersion = crossScalaVersion.split('.')(1).toInt

  def publishVersion = "0.5-SNAPSHOT"

  private def javacCrossOptions = if(majorVersion == 12) Seq("-source", "1.8", "-target", "1.8") else Nil

  override def scalacOptions = T {
    super.scalacOptions() ++ Seq(
      "-deprecation",
      "-feature",
      "-unchecked"
    )
  }

  def treadleModule: Option[PublishModule] = None

  def treadleIvyDeps = if (treadleModule.isEmpty) Agg(
    ivy"edu.berkeley.cs::treadle:1.5-SNAPSHOT"
  ) else Agg.empty[Dep]

  def firrtlModule: Option[PublishModule] = None

  def firrtlIvyDeps = if (treadleModule.isEmpty) Agg(
    ivy"edu.berkeley.cs::firrtl:1.5-SNAPSHOT"
  ) else Agg.empty[Dep]

  override def javacOptions = T {
    super.javacOptions() ++ javacCrossOptions
  }

  override def moduleDeps = super.moduleDeps ++ firrtlModule ++ treadleModule

  override def ivyDeps = T {
    super.ivyDeps() ++ Agg(
      ivy"net.java.dev.jna:jna:5.4.0",
      ivy"net.java.dev.jna:jna-platform:5.4.0",
      ivy"com.github.com-github-javabdd:com.github.javabdd:1.0.1",
    ) ++ firrtlIvyDeps ++ treadleIvyDeps
  }

  object test extends Tests {
    override def millSourcePath = super.millSourcePath / os.up

    override def ivyDeps = T {
      Agg(
        ivy"org.scalatest::scalatest:3.2.6"
      )
    }

    override def testFramework = T {
      "org.scalatest.tools.Framework"
    }
  }

  def pomSettings = T {
    PomSettings(
      description = artifactName(),
      organization = "edu.berkeley.cs",
      url = "https://github.com/ucb-bar/maltese-smt",
      licenses = Seq(License.`BSD-3-Clause`),
      versionControl = VersionControl.github("ucb-bar", "maltese-smt"),
      developers = Seq(
        Developer("ekiwi", "Kevin Laeufer", "https://github.com/ekiwi/")
      )
    )
  }

  // make mill publish sbt compatible package
  override def artifactName = "maltese-smt"
}
