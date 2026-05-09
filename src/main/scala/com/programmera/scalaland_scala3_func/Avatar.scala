package com.programmera.scalaland_scala3_func

/** A combat system parameterised over a `BattleSystem` `given`. Different rule sets (e.g. AD&D vs.
  * World of Darkness) plug in different damage formulas without subclassing Avatar.
  */
trait BattleSystem:
  def damage(attacker: Strength, defender: Strength): Int

object BattleSystem:
  given default: BattleSystem with
    def damage(a: Strength, d: Strength): Int = (a.value - d.value).max(0)

  /** Brutal alternate ruleset: defender's strength counts double. */
  val brutal: BattleSystem = new BattleSystem:
    def damage(a: Strength, d: Strength): Int = (a.value - 2 * d.value).max(0)

case class Avatar(
    name: String,
    strength: Strength,
    wisdom: Wisdom,
    charisma: Charisma,
    hitpoints: Hitpoints
):
  /** `using` makes the BattleSystem an implicit context parameter resolved at the call site.
    * Compare against `scalaland_func_final.Character.physicalAttack` which returned `Option[(Int,
    * Int) => Int]` to defer the formula - here the formula is pluggable but type-checked.
    */
  def attack(target: Avatar)(using bs: BattleSystem): Avatar =
    val dmg = bs.damage(strength, target.strength)
    target.copy(hitpoints = target.hitpoints - dmg)

  override def toString: String =
    s"Avatar: $name (str ${strength.value}, wis ${wisdom.value}, " +
      s"cha ${charisma.value}, hp ${hitpoints.value})"

object Avatar:
  /** Smart constructor with named-argument refactor-safety. */
  def fresh(name: String, str: Int, wis: Int, cha: Int): Avatar =
    Avatar(
      name = name,
      strength = Strength(str),
      wisdom = Wisdom(wis),
      charisma = Charisma(cha),
      hitpoints = Hitpoints(str * 2)
    )
