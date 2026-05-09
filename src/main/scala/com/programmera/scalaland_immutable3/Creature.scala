package com.programmera.scalaland_immutable3

trait Creature {

  // Abstract fields
  val name: String
  protected val _features: CreatureFeatureSet

  // Setters and Getters
  def strength: Int = _features.strength
  def withStrength(s: Int): Creature

  def wisdom: Int = _features.wisdom
  def withWisdom(w: Int): Creature

  def charisma: Int = _features.charisma
  def withCharisma(c: Int): Creature

  def hitpoints: Int = _features.hitpoints
  def withHitpoints(h: Int): Creature

  // Called during initialization of the instance
  protected def generateCreatureFeatures(): CreatureFeatureSet = {
    throw new IllegalArgumentException(
      "Avatar has no race! " +
        "You must mix in a race during instanciation."
    )
  }

  // Top level implemenentation, no need to call super
  override def toString: String = """Creature: %s 
     | %s """.stripMargin.format(name, _features)

}
