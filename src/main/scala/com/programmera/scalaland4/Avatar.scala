package com.programmera.scalaland4

abstract class Avatar(
    val name: String, 
    private var _strength: Int, 
    private var _wisdom: Int, 
    private var _charisma: Int) {

  var hitpoints: Int = _strength * 2
  val items: MagicalItemList = new MagicalItemList()
  
  // Setters and getters 
  def strength: Int = _strength + 
    items.calculateModifier(CreatureFeature.Strength)
  def strength_=(s: Int): Unit = { _strength = s }
  def wisdom: Int = _wisdom +
    items.calculateModifier(CreatureFeature.Wisdom)
  def wisdom_=(w: Int): Unit = { _wisdom = w }
  def charisma: Int = _charisma +
    items.calculateModifier(CreatureFeature.Charisma)
  def charisma_=(c: Int): Unit = { _charisma = c }
  
  override def toString: String = "Avatar: " + name +
    "\n (strength: "+ _strength + ", wisdom: "+ _wisdom +
    ", charisma: "+ _charisma + ")" +
    "\n"+ items
}

