package com.programmera.scalaland_scala3_enum

/** A single immutable case class - no `with Elf with Thief` stacking,
  * no abstract `Creature` trait, no Avatar.apply factory cascade.
  *
  * Compare against `scalaland_immutable3.Avatar.apply` which has 25+
  * lines of nested `creature match { case ... profession match { ... } }`
  * to dispatch to one of six concrete mixin combinations. Here the
  * `case class` constructor IS the factory.
  */
case class Avatar(
    name: String,
    race: Race,
    profession: Profession,
    features: CreatureFeatureSet,
    items: MagicalItemList = MagicalItemList(),
):
  def strength: Int  = features.strength  + items.calculateModifier(CreatureFeature.Strength)
  def wisdom: Int    = features.wisdom    + items.calculateModifier(CreatureFeature.Wisdom)
  def charisma: Int  = features.charisma  + items.calculateModifier(CreatureFeature.Charisma)
  def hitpoints: Int = features.hitpoints

  def withStrength(s: Int): Avatar  = copy(features = features.copy(strength = s))
  def withWisdom(w: Int): Avatar    = copy(features = features.copy(wisdom = w))
  def withCharisma(c: Int): Avatar  = copy(features = features.copy(charisma = c))

  def withHitpoints(h: Int): Avatar =
    if h > 0 then copy(features = features.copy(hitpoints = h))
    else throw new DeathException(s"$name died!")

  def addItem(item: MagicalItem): Avatar = copy(items = items.add(item))

  def climb: Int = profession.climb(strength)

  override def toString: String =
    s"Avatar: $name\n  $race $profession\n  $features\n$items"

object Avatar:
  /** Roll fresh stats for a new character. */
  def apply(name: String, race: Race, profession: Profession): Avatar =
    Avatar(name, race, profession, race.generateFeatures(), MagicalItemList())
