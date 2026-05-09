package com.programmera.scalaland_trait

trait Wizard extends Professional {

  override def toString: String = super.toString + "\n is a wizard."

  // Good with spells
  override def magicAttack(foe: Creature): Unit = {
    println("Wizard using magicAttack.")
    val damage = (this.wisdom - foe.wisdom)/2 + DieRoll.roll(2)
    sufferDamage(foe, damage)
  }
}  


