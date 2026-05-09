package com.programmera.scalaland_generic

import org.scalatest.funsuite.AnyFunSuite

class AvatarSmokeTest extends AnyFunSuite {
  test("Avatar factory dispatches on creature + profession") {
    val a = Avatar("Bilbo", CreatureType.Elf, ProfessionalType.Thief)
    assert(a.name == "Bilbo")
    assert(a.isInstanceOf[Elf])
    assert(a.isInstanceOf[Thief])
  }

  test("Avatar climb is a positive Int for thieves") {
    val a = Avatar("Bilbo", CreatureType.Elf, ProfessionalType.Thief)
    assert(a.climb >= 0)
  }
}
