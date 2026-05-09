package com.programmera.scalaland_scala3_generic

case class Avatar(name: String, strength: Int, hitpoints: Int)

object Avatar:
  /** Per-type Attackable instance. Avatar isn't a Creature, doesn't
    * extend any Attackable interface - the typeclass instance is
    * a separate value defined alongside the type. */
  given Attackable[Avatar] with
    def hp(a: Avatar): Int = a.hitpoints
    def damaged(a: Avatar, damage: Int): Avatar =
      a.copy(hitpoints = (a.hitpoints - damage).max(0))

/** Top-level extension method, using `summon` to pick up the
  * Attackable typeclass instance for `Target` at the call site.
  *
  * Compare against `scalaland_generic.Professional.weaponAttack[T <: Creature]`
  * which has a 12-line method body that includes
  * `case f: T @unchecked => f` to work around type erasure.
  * The typeclass approach has zero erasure pain because dispatch
  * happens via the typeclass instance, not type parameters.
  */
extension (attacker: Avatar)
  def attack[Target](target: Target)(using A: Attackable[Target]): Target =
    A.damaged(target, attacker.strength)
