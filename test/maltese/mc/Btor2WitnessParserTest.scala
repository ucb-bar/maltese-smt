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

  it should "parse a witness without state" in {
    val witnesses = Btor2WitnessParser.read(noStateWitness.split("\n"))

    assert(witnesses.length == 1, "there is only a single counter example")
    val w = witnesses.head
    assert(w.memInit.isEmpty, "there are no memories in the design")
    assert(w.regInit.isEmpty, "there are no states in the design")
    assert(w.inputs.head == Map(0 -> 0, 1 -> BigInt("11111011", 2), 2 -> BigInt("101", 2)))
    assert(w.inputs.length == 1, "there is only a single cycle")
  }

  val fsmWitness =
    """sat
      |b0
      |#0
      |0 00 state#0
      |@0
      |0 1 reset@0
      |1 1 in@0
      |@1
      |0 0 reset@1
      |1 1 in@1
      |.
      |
      |""".stripMargin

  it should "parse a witness with state" in {
    val witnesses = Btor2WitnessParser.read(fsmWitness.split("\n"))

    assert(witnesses.length == 1, "there is only a single counter example")
    val w = witnesses.head
    assert(w.memInit.isEmpty, "there are no memories in the design")
    assert(w.regInit(0) == 0, "state register is initialized to zero")
    assert(w.inputs(0) == Map(0 -> 1, 1 -> 1), "both reset (0) and in (1) are high in the first cycle")
    assert(w.inputs(1) == Map(0 -> 0, 1 -> 1), "reset is low in the second cycle")
  }
}
