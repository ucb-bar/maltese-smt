// Copyright 2020 The Regents of the University of California
// released under BSD 3-Clause License
// author: Kevin Laeufer <laeufer@cs.berkeley.edu>

package maltese.smt.solvers

import com.sun.jna.Native
import org.scalatest.flatspec.AnyFlatSpec

class Yices2ApiSpec extends AnyFlatSpec {
  it should "read version information" in {
    val info = Yices2Api.getInfo()
    assert(info.Version.startsWith("2."))
    assert(info.BuildArch.contains("linux"))
  }

  it should "load yices library" in {
    val lib = Native.load("yices", classOf[Yices2Api])
    assert(lib.toString.contains("yices.so"))
  }

  def bitArrayToBigInt(a: Array[Int]): BigInt =
    BigInt(a.reverseIterator.map(_.toString).mkString(""), 2)

  it should "check a small bitvector example" ignore { // TODO: why is yices returning an error?
    val lib = Native.load("yices", classOf[Yices2Api])
    def assert_no_error: Unit = assert(lib.yices_error_code() == Yices2Api.NO_ERROR)

    lib.yices_init(); assert_no_error
    val bv8 = lib.yices_bv_type((8)); assert_no_error
    val a = lib.yices_new_uninterpreted_term(bv8); assert_no_error
    val b = lib.yices_new_uninterpreted_term(bv8); assert_no_error
    val a_gt_b = lib.yices_bvgt_atom(a, b); assert_no_error
    val bv8_2 = lib.yices_bvconst_int32(8, 2); assert_no_error
    val a_lt_2 = lib.yices_bvlt_atom(a, bv8_2); assert_no_error
    val b_gt_2 = lib.yices_bvgt_atom(b, bv8_2); assert_no_error

    val conf = lib.yices_new_config(); assert_no_error
    lib.yices_default_config_for_logic(conf, "QF_AUFBV"); assert_no_error
    val ctx = lib.yices_new_context(conf); assert_no_error
    val params = lib.yices_new_param_record(); assert_no_error

    // assert a > b and a < 2 and b > 2 --> UNSAT
    lib.yices_assert_formula(ctx, a_gt_b); assert_no_error
    lib.yices_assert_formula(ctx, a_lt_2); assert_no_error
    lib.yices_push(ctx)
    lib.yices_assert_formula(ctx, b_gt_2); assert_no_error

    val res0 = lib.yices_check_context(ctx, params); assert_no_error
    assert(res0 == Yices2Api.STATUS_UNSAT)

    // assert a > b and a < 2 --> SAT
    lib.yices_pop(ctx)
    val res = lib.yices_check_context(ctx, params); assert_no_error
    assert(res == Yices2Api.STATUS_SAT)

    // get the model
    val model = lib.yices_get_model(ctx, 0); assert_no_error
    // get a
    val value_array = new Array[Int](8)
    lib.yices_get_bv_value(model, a, value_array); assert_no_error
    val a_value = bitArrayToBigInt(value_array)
    // get b
    lib.yices_get_bv_value(model, b, value_array); assert_no_error
    val b_value = bitArrayToBigInt(value_array)

    assert(a_value < 2)
    assert(a_value > b_value)

    lib.yices_free_param_record(params); assert_no_error
    lib.yices_free_context(ctx); assert_no_error
    lib.yices_exit(); assert_no_error
  }
}
