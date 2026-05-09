package com.programmera.scalaland_scala3_func

/** **Opaque types** are the Scala 3 idiom that gives you a *new
  * type* with zero runtime overhead. At the JVM level a `Strength`
  * value is just an Int, but the type checker treats it as a
  * distinct type from Hitpoints, Wisdom, etc.
  *
  * Compare against `scalaland_func_final` where strength, wisdom,
  * charisma are all `Int` and you can accidentally pass a Wisdom
  * value where Strength was expected. Here that's a compile error.
  */
opaque type Strength  = Int
opaque type Wisdom    = Int
opaque type Charisma  = Int
opaque type Hitpoints = Int

object Strength:
  def apply(n: Int): Strength =
    require(n >= 0, "strength must be non-negative")
    n
  extension (s: Strength)
    def value: Int             = s
    def +(other: Int): Strength = s + other

object Wisdom:
  def apply(n: Int): Wisdom =
    require(n >= 0, "wisdom must be non-negative")
    n
  extension (w: Wisdom) def value: Int = w

object Charisma:
  def apply(n: Int): Charisma =
    require(n >= 0, "charisma must be non-negative")
    n
  extension (c: Charisma) def value: Int = c

object Hitpoints:
  def apply(n: Int): Hitpoints =
    require(n > 0, "a living character has at least 1 hitpoint")
    n
  extension (h: Hitpoints)
    def value: Int                = h
    def -(damage: Int): Hitpoints = Hitpoints((h - damage).max(1))
