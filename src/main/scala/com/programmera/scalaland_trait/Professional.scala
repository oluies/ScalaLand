package com.programmera.scalaland_trait

trait Professional extends Creature {

  // Returns climbed meters
  def climb: Int = {
    println("This character can not climb.")
    0
  }

  // Will if successful reduce hitpoints on foe
  def magicAttack(foe: Creature): Unit = {
    println("This character can not use magic to attack.")
  }

  // Will if successful reduce hitpoints on foe
  def weaponAttack(foe: Creature): Unit = {
    println("Default weaponAttack.")
    val damage = (this.strength - foe.strength) / 3 + DieRoll.roll(1)
    sufferDamage(foe, damage)
  }

  protected def sufferDamage(foe: Creature, damage: Int): Unit = {
    println("Damage: " + damage)
    if (damage > 0) foe.hitpoints -= damage
  }
}
