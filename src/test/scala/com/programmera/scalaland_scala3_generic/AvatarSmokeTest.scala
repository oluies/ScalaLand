package com.programmera.scalaland_scala3_generic

import org.scalatest.funsuite.AnyFunSuite

class AvatarSmokeTest extends AnyFunSuite:

  test("Avatar attacks Avatar via typeclass"):
    val attacker = Avatar("Bilbo", strength = 5, hitpoints = 10)
    val target = Avatar("Goblin", strength = 1, hitpoints = 8)
    val damaged = attacker.attack(target)
    assert(damaged.hitpoints == 3) // 8 - 5
    assert(damaged.name == "Goblin")

  test("Avatar attacks bare Int via the Int Attackable instance"):
    val attacker = Avatar("Bilbo", strength = 7, hitpoints = 10)
    val rock: Int = 100
    val cracked = attacker.attack(rock) // resolves Attackable[Int]
    assert(cracked == 93)

  test("summon retrieves the typeclass instance directly"):
    val attackableInt = summon[Attackable[Int]]
    assert(attackableInt.damaged(10, 3) == 7)

  test("Ability.perform returns Int for Climb, Boolean for Fly"):
    // The match type + `inline match` make these returns
    // differently-typed at compile time. The compiler reduces
    // AbilityResult[Ability.Climb.type] to Int and
    // AbilityResult[Ability.Fly.type] to Boolean.
    // Explicit type argument prevents widening of Ability.Climb
    // (which would default to its parent type `Ability` and
    // block match-type reduction).
    val climbResult: Int = Ability.perform[Ability.Climb.type](5)(Ability.Climb)
    val flyResult: Boolean = Ability.perform[Ability.Fly.type](5)(Ability.Fly)
    assert(climbResult == 10)
    assert(flyResult == false)
