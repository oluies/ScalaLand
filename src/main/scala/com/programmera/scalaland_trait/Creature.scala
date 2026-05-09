package com.programmera.scalaland_trait

trait Creature {

  // Abstract field
  val name: String

  // Fields
  private var _strength = 0
  private var _wisdom = 0
  private var _charisma = 0
  private var _hitpoints = 0

  // Setters and Getters 
  def strength: Int = _strength 
  def strength_=(s: Int): Unit = { _strength = s }
  def wisdom: Int = _wisdom
  def wisdom_=(w: Int): Unit = { _wisdom = w }
  def charisma: Int = _charisma
  def charisma_=(c: Int): Unit = { _charisma = c }
  def hitpoints: Int= _hitpoints
  def hitpoints_=(h: Int): Unit = {
    println("Hitpoints, old value: " + hitpoints + ", new value: " + h)
    if (h > 0)
      _hitpoints = h
    else
      throw new DeathException(name + " died!")
  }
 
  // Called during initialization of the instance
  protected def generateCreatureFeatures(): Unit = {
    throw new IllegalArgumentException("Creature has no race! " +
      "You must mix in a race during instanciation.")
  }

  // Top level implemenentation, no need to call super
  override def toString: String = """Creature: %s
    |(strength: %d, wisdom: %d, charisma: %d) hitpoints: %d """.
    stripMargin.
    format(name, _strength, _wisdom, _charisma, _hitpoints)
}




