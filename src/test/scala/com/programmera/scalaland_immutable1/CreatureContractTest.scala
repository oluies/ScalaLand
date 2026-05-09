package com.programmera.scalaland_immutable1

import org.scalatest.funsuite.AnyFunSuite

class CreatureContractTest extends AnyFunSuite {
  test("Npc without race fails on init - documented contract") {
    intercept[IllegalArgumentException] {
      Npc("RogueWithoutRace", optionalFeatures = None)
    }
  }

  test("CreatureFeatureSet.copy is structural") {
    val base = CreatureFeatureSet(strength = 1, wisdom = 2, charisma = 3, hitpoints = 4)
    assert(base.copy(strength = 9).strength == 9)
    assert(base.copy(strength = 9).wisdom == 2)
  }
}
