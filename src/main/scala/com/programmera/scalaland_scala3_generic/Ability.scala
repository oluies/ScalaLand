package com.programmera.scalaland_scala3_generic

/** **Match types** - type-level computation. The result type of an
  * Ability depends on which case it is.
  *
  * In `scalaland_generic` everything was either Int or T (the
  * F-bound). With match types we can declare that climbing yields
  * meters (Int), swimming yields meters (Int), but flying is just
  * a yes/no (Boolean). The compiler propagates these distinct types
  * through generic code without unsafe casts.
  */
enum Ability:
  case Climb, Swim, Fly

/** AbilityResult maps each Ability case to its result type at the
  * type level. The compiler reduces `AbilityResult[Ability.Climb.type]`
  * to `Int`, `AbilityResult[Ability.Fly.type]` to `Boolean`. */
type AbilityResult[A <: Ability] = A match
  case Ability.Climb.type => Int
  case Ability.Swim.type  => Int
  case Ability.Fly.type   => Boolean

object Ability:
  /** `inline` + `inline match` is what unlocks match-type reduction
    * at use sites: the call-site must know the singleton case
    * type, and the compiler inlines the body and reduces the type.
    *
    * Compare against scalaland_generic which would need three
    * separately-named methods or runtime casts to achieve the
    * same per-case typing. */
  inline def perform[A <: Ability](strength: Int)(inline a: A): AbilityResult[A] =
    inline a match
      case _: Ability.Climb.type => strength * 2
      case _: Ability.Swim.type  => strength
      case _: Ability.Fly.type   => false
