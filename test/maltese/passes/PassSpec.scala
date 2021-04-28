// Copyright 2020 The Regents of the University of California
// released under BSD 3-Clause License
// author: Kevin Laeufer <laeufer@cs.berkeley.edu>

package maltese.passes

import maltese.mc.{Btor2, TransitionSystem}
import org.scalatest.flatspec.AnyFlatSpec

abstract class PassSpec(pass: Pass) extends AnyFlatSpec {
  protected def prereqs: Iterable[Pass] = Seq()

  def compile(src: String): TransitionSystem = {
    val sys = Btor2.read(src, inlineSignals = false)
    val passes = prereqs ++ Iterator(pass)
    passes.foldLeft(sys) { case (s, p) => p.run(s) }
  }

  def check(src: String, expected: String): Unit = {
    val sys = compile(src)
    val sysString = sys.serialize.split('\n').drop(1).mkString("\n").trim
    assert(sysString == expected.trim)
  }
}
