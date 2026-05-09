package com.programmera.scalaland_scala3_enum

enum CreatureFeature:
  case Strength, Wisdom, Charisma

case class CreatureFeatureSet(
    strength: Int,
    wisdom: Int,
    charisma: Int,
    hitpoints: Int,
):
  override def toString: String =
    s"(strength: $strength, wisdom: $wisdom, charisma: $charisma) hitpoints $hitpoints"

class DeathException(msg: String) extends Exception(msg)
