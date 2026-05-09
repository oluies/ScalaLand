package com.programmera.scalaland3

object Elf {
  def apply(name: String): Elf = 
    new Elf(
      name, 
      startingStrength = DieRoll.roll(2), 
      startingWisdom = DieRoll.roll(4),
      startingCharisma = DieRoll.roll(4) ) 
}

class Elf(
    override val name: String, 
    startingStrength: Int, 
    startingWisdom: Int, 
    startingCharisma: Int) 
  extends Avatar(
    name, 
    startingStrength, 
    startingWisdom, 
    startingCharisma) {

  override def toString: String = super.toString + "\n is an elf."
}
  

