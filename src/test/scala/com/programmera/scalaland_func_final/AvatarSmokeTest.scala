package com.programmera.scalaland_func_final

import org.scalatest.funsuite.AnyFunSuite

class AvatarSmokeTest extends AnyFunSuite {
  test("Avatar factory dispatches on race + character") {
    val a = Avatar("Bilbo", RaceType.Elf, CharacterType.Thief)
    assert(a.name == "Bilbo")
    assert(a.isInstanceOf[Elf])
    assert(a.isInstanceOf[Thief])
  }

  test("Thief.climb returns a Some, Wizard.climb returns None") {
    val thief = Avatar("T", RaceType.Elf, CharacterType.Thief)
    val wizard = Avatar("W", RaceType.Elf, CharacterType.Wizard)
    assert(thief.climb.isDefined)
    assert(wizard.climb.isEmpty)
  }
}
