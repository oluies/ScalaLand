package com.programmera.scalaland1a

import org.scalatest.funsuite.AnyFunSuite

class Avatar1aSmokeTest extends AnyFunSuite {
  test("Elf toString announces race") {
    assert(new Elf("Legolas").toString.contains("elf"))
  }
}
