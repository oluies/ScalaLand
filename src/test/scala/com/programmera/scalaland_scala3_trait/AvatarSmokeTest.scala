package com.programmera.scalaland_scala3_trait

import org.scalatest.funsuite.AnyFunSuite

class AvatarSmokeTest extends AnyFunSuite:

  test("Composition pulls trait parameters into one Avatar"):
    val a = Avatar("Bilbo", Race.Elf, Profession.Warrior)
    assert(a.name == "Bilbo")
    assert(a.race.raceName == "Elf")
    assert(a.profession.professionName == "Warrior")
    assert(a.stats == Stats.forElf)
    assert(a.profession.damageMultiplier == 2)

  test("Different race/profession combos yield independent Avatars"):
    val a = Avatar("Gimli", Race.Dwarf, Profession.Wizard)
    assert(a.race.raceName == "Dwarf")
    assert(a.stats.strength == 4)
    assert(a.profession.damageMultiplier == 1)

  test("attackDamage uses Profession.damageMultiplier"):
    val attacker = Avatar("A", Race.Dwarf, Profession.Warrior)
    val target = Avatar("T", Race.Elf, Profession.Wizard)
    assert(attacker.attackDamage(target) == (4 - 2) * 2)

  test("Sealed Race + Profession enable exhaustive analysis"):
    // Scala 3 will warn (under -Wunused / -Werror) if a match on
    // a sealed type misses cases. Here we exercise both Race
    // instances to make the contract explicit.
    val races = List(Race.Elf, Race.Dwarf)
    assert(races.map(_.raceName).toSet == Set("Elf", "Dwarf"))
