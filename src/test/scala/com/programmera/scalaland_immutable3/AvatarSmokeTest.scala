package com.programmera.scalaland_immutable3

import org.scalatest.funsuite.AnyFunSuite

class AvatarSmokeTest extends AnyFunSuite {
  test("Avatar factory dispatches on race + profession") {
    val a = Avatar("Bilbo", CreatureType.Elf, ProfessionalType.Thief)
    assert(a.name == "Bilbo")
    assert(a.isInstanceOf[Elf])
    assert(a.isInstanceOf[Thief])
  }

  test("withStrength returns a new instance") {
    val a = Avatar("Bilbo", CreatureType.Dwarf, ProfessionalType.Warrior)
    val b = a.withStrength(99)
    assert(b.strength == 99)
    assert(a ne b)
  }
}
