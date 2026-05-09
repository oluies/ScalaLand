package com.programmera.scalaland3

import org.scalatest.funsuite.AnyFunSuite

class AvatarSmokeTest extends AnyFunSuite {
  test("Elf factory produces a named, alive avatar") {
    val e = Elf("Legolas")
    assert(e.name == "Legolas")
    assert(e.hitpoints > 0)
  }

  test("MagicalItemList starts empty and accepts items") {
    val list = new MagicalItemList()
    assert(list.strengthModifier == 0)
    list.add(new MagicalItem("Sword", 2, 0, 0))
    assert(list.strengthModifier == 2)
  }
}
