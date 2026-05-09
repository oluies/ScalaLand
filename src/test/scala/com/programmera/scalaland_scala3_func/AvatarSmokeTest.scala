package com.programmera.scalaland_scala3_func

import org.scalatest.funsuite.AnyFunSuite

class AvatarSmokeTest extends AnyFunSuite:

  test("Opaque types prevent mixing strength with wisdom"):
    val s = Strength(10)
    val w = Wisdom(10)
    // The line below is a compile error - that's the lesson.
    // val confused: Strength = w
    assert(s.value == 10)
    assert(w.value == 10)

  test("Hitpoints requires a positive value"):
    intercept[IllegalArgumentException](Hitpoints(0))
    intercept[IllegalArgumentException](Hitpoints(-1))

  test("Hitpoints subtraction floors at 1, never throws"):
    val hp = Hitpoints(5)
    val low = hp - 100
    assert(low.value == 1)

  test("Default given BattleSystem applies subtraction"):
    val a = Avatar.fresh("Attacker", str = 10, wis = 5, cha = 5)
    val b = Avatar.fresh("Target", str = 4, wis = 5, cha = 5)
    val bAfter = a.attack(b)
    // Default: damage = max(10 - 4, 0) = 6, target hp = max(8 - 6, 1) = 2
    assert(bAfter.hitpoints.value == 2)

  test("Explicit `using` selects the brutal ruleset"):
    val a = Avatar.fresh("Attacker", str = 10, wis = 5, cha = 5)
    val b = Avatar.fresh("Target", str = 4, wis = 5, cha = 5)
    val bAfter = a.attack(b)(using BattleSystem.brutal)
    // Brutal: damage = max(10 - 8, 0) = 2, target hp = max(8 - 2, 1) = 6
    assert(bAfter.hitpoints.value == 6)
