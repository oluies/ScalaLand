package com.programmera.scalaland_scala3_trait

case class Stats(strength: Int, wisdom: Int, charisma: Int):
  def hitpoints: Int = strength * 2

object Stats:
  /** Deterministic for testability; real game would roll dice. */
  def forElf:   Stats = Stats(strength = 2, wisdom = 4, charisma = 4)
  def forDwarf: Stats = Stats(strength = 4, wisdom = 3, charisma = 2)

/** **Trait parameter** - the Scala 3 idiom that replaces the
  * abstract-member-with-init-throw pattern from `scalaland_trait`.
  *
  * In `scalaland_trait`, Creature.generateCreatureFeatures threw an
  * IllegalArgumentException to force users to mix in a race; the
  * compiler couldn't enforce it. Here the parameter is declared on
  * the trait itself, and only **classes** extending the trait can
  * supply it - the type system enforces what the throw used to.
  *
  * Scala 3 rule we hit: parameterized traits can only have their
  * constructor called by classes, not by other traits. So the
  * `new Avatar(name) with Elf with Thief` mixin pattern from
  * scalaland_trait does not translate; the idiomatic Scala 3
  * version uses composition instead.
  */
sealed trait Race(val raceName: String, val baseStats: Stats)

class Elf   extends Race("Elf",   Stats.forElf)
class Dwarf extends Race("Dwarf", Stats.forDwarf)

object Race:
  val Elf:   Race = new Elf
  val Dwarf: Race = new Dwarf
