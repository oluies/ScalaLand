package com.programmera.scalaland_scala3_enum

/** Race replaces both `object CreatureType extends Enumeration` AND the
  * separate `Elf`/`Dwarf` traits from `scalaland_immutable3`. The
  * per-race feature distribution is a method on the enum case rather
  * than a trait override - no mixin needed.
  */
enum Race:
  case Elf, Dwarf

  def generateFeatures(): CreatureFeatureSet =
    val (s, w, c) = this match
      case Elf   => (DieRoll.roll(2), DieRoll.roll(4), DieRoll.roll(4))
      case Dwarf => (DieRoll.roll(4), DieRoll.roll(3), DieRoll.roll(2))
    CreatureFeatureSet(strength = s, wisdom = w, charisma = c, hitpoints = s * 2)
