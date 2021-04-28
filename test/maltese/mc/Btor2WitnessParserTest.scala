package maltese.mc

import org.scalatest.flatspec.AnyFlatSpec

class Btor2WitnessParserTest extends AnyFlatSpec {
  behavior.of("Btor2WitnessParser")

  val noStateWitness =
    """sat
      |b0
      |@0
      |0 0 reset@0
      |1 11111011 a@0
      |2 00000101 b@0
      |.
      |
      |""".stripMargin

  it should "parse a witness without state" ignore { // TODO: fix witness parser
    Btor2WitnessParser.read(noStateWitness.split("\n"))
  }
}
