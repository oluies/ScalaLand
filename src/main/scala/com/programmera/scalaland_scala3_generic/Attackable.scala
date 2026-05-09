package com.programmera.scalaland_scala3_generic

/** **Typeclass** - a pattern Scala 3 makes first-class via `given`.
  *
  * `scalaland_generic` solves "any creature can be attacked" with F-bounded inheritance: `trait
  * Professional extends Creature` with a `magicAttack[T <: Creature](foe: T): T` and an abstract
  * `type SubCreature <: Creature`. The result is rigid: to make a NEW kind of attackable thing you
  * must extend Creature, taking on every Creature member.
  *
  * Here `Attackable[A]` is a typeclass: any type `A` can be made attackable by providing a `given
  * Attackable[A]` instance, with no inheritance. Add new attackable types without touching Avatar /
  * Creature / etc.
  */
trait Attackable[A]:
  def hp(a: A): Int
  def damaged(a: A, damage: Int): A

object Attackable:
  /** Bare Int is a valid "attackable thing" - useful for smoke tests and quick scripts.
    */
  given Attackable[Int] with
    def hp(n: Int): Int = n
    def damaged(n: Int, damage: Int): Int = (n - damage).max(0)
