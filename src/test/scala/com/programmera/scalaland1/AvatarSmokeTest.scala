package com.programmera.scalaland1

import org.scalatest.funsuite.AnyFunSuite

class AvatarSmokeTest extends AnyFunSuite {
  test("Elf carries its name") {
    assert(new Elf("Bilbo").name == "Bilbo")
  }

  test("Dwarf toString includes race") {
    assert(new Dwarf("Gimli").toString.contains("dwarf"))
  }
}
