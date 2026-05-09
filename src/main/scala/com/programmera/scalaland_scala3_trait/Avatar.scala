package com.programmera.scalaland_scala3_trait

/** Composition over mixin. Compare against `scalaland_trait.Avatar`, which extended `Professional`
  * (which extended `Creature`) and was instantiated as `new Avatar(name) with Elf with Thief` - a
  * 6-trait linearization that initialized via a stateful `generateCreatureFeatures()` call.
  *
  * Here Avatar holds a `Race` and a `Profession` as plain values. No mixin, no init-time side
  * effect, no abstract members, no `var`. The Scala 3 type system already enforces what
  * `scalaland_trait`'s init-throw was approximating.
  */
case class Avatar(name: String, race: Race, profession: Profession):
  val stats: Stats = race.baseStats

  def attackDamage(target: Avatar): Int =
    ((stats.strength - target.stats.strength) * profession.damageMultiplier).max(0)

  override def toString: String =
    s"Avatar: $name the ${race.raceName} ${profession.professionName} ($stats, hp ${stats.hitpoints})"
