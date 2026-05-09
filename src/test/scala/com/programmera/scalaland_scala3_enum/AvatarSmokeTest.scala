package com.programmera.scalaland_scala3_enum

import org.scalatest.funsuite.AnyFunSuite

class AvatarSmokeTest extends AnyFunSuite:

  test("Avatar factory produces fresh stats per race"):
    val a = Avatar("Bilbo", Race.Elf, Profession.Thief)
    assert(a.name == "Bilbo")
    assert(a.race == Race.Elf)
    assert(a.profession == Profession.Thief)
    assert(a.hitpoints > 0)

  test("withStrength returns a new instance (immutability)"):
    val a = Avatar("Gimli", Race.Dwarf, Profession.Warrior)
    val b = a.withStrength(99)
    assert(b.strength == 99)
    assert(a ne b)

  test("withHitpoints to 0 throws DeathException"):
    val a = Avatar("Doomed", Race.Elf, Profession.Wizard)
    intercept[DeathException](a.withHitpoints(0))

  test("Magical items add to the relevant CreatureFeature"):
    val a = Avatar("Loaded", Race.Elf, Profession.Thief)
      .addItem(MagicalItem("Sword",     Map(CreatureFeature.Strength -> 2)))
      .addItem(MagicalItem("WiseHat",   Map(CreatureFeature.Wisdom   -> 1)))
    assert(a.strength - a.features.strength == 2)
    assert(a.wisdom   - a.features.wisdom   == 1)

  test("Profession.climb is exhaustive (compile-time guarantee)"):
    // The compiler proves these three cases cover Profession - no `case _`.
    Profession.values.foreach: p =>
      val damage = p.climb(strength = 10)
      assert(damage >= 0)

  test("Wizard can magicAttack, Warrior cannot"):
    assert(Profession.Wizard.magicAttack(myWisdom = 10, foeWisdom = 0) > 0)
    assert(Profession.Warrior.magicAttack(myWisdom = 10, foeWisdom = 0) == 0)
