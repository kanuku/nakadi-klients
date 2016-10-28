import sbt._

object Configs {
  val IntegrationTest = config("it") extend(Runtime)
  val all = Seq(IntegrationTest)
}
