package com.programmera.scalaland_scala3_trait

/** Same trait-parameter pattern: damage multiplier is data on the trait, not an abstract member
  * that subclasses might forget to override.
  */
sealed trait Profession(
    val professionName: String,
    val damageMultiplier: Int
)

class Warrior extends Profession("Warrior", damageMultiplier = 2)
class Wizard extends Profession("Wizard", damageMultiplier = 1)
class Thief extends Profession("Thief", damageMultiplier = 1)

object Profession:
  val Warrior: Profession = new Warrior
  val Wizard: Profession = new Wizard
  val Thief: Profession = new Thief
