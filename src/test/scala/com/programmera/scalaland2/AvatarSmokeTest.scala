package com.programmera.scalaland2

import org.scalatest.funsuite.AnyFunSuite

class AvatarSmokeTest extends AnyFunSuite {
  test("Elf preserves stats and derives hitpoints") {
    val e = new Elf("Legolas", 5, 5, 5)
    assert(e.name == "Legolas")
    assert(e.strength == 5)
    assert(e.hitpoints == 10)
  }
}
