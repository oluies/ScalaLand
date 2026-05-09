package com.programmera.scalaland4

import org.scalatest.funsuite.AnyFunSuite

class AvatarSmokeTest extends AnyFunSuite {
  test("Elf factory produces a named avatar") {
    val e = Elf("Legolas")
    assert(e.name == "Legolas")
  }

  test("MagicalItemList sums modifiers per CreatureFeature") {
    val list = new MagicalItemList()
    list.add(new MagicalItem("Sword", 2, 0, 0))
    list.add(new MagicalItem("Hat",   0, 1, 0))
    assert(list.calculateModifier(CreatureFeature.Strength) == 2)
    assert(list.calculateModifier(CreatureFeature.Wisdom)   == 1)
  }
}
