package com.programmera.scalaland2

class Elf(
    override val name: String,
    startingStrength: Int,
    startingWisdom: Int,
    startingCharisma: Int
) extends Avatar(name, startingStrength, startingWisdom, startingCharisma) {
  override def toString: String = super.toString + "\n is an elf."
}
