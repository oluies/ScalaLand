package com.programmera.scalaland_trait

case class Avatar(name: String) extends Professional {

  // Fields
  val items: MagicalItemList = new MagicalItemList()

  // Initialize class
  generateCreatureFeatures()

  // Override getters
  override def strength: Int = super.strength +
    items.calculateModifier(CreatureFeature.Strength)
  override def wisdom: Int = super.wisdom +
    items.calculateModifier(CreatureFeature.Wisdom)
  override def charisma: Int = super.charisma +
    items.calculateModifier(CreatureFeature.Charisma)

  // super will call toString in Professional
  override def toString: String = super.toString + "\n" + items.toString

}
