package com.programmera.scalaland_trait

import org.scalatest.funsuite.AnyFunSuite

class AvatarSmokeTest extends AnyFunSuite {
  test("Mixin Elf+Thief produces a usable avatar") {
    val a = new Avatar("Bilbo") with Elf with Thief
    assert(a.name == "Bilbo")
    assert(a.hitpoints > 0)
    assert(a.toString.contains("elf"))
    assert(a.toString.contains("thief"))
  }

  test("Avatar without race throws on init") {
    intercept[IllegalArgumentException] {
      Avatar("RogueWithoutRace")
    }
  }
}
