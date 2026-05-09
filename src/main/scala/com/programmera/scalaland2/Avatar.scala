package com.programmera.scalaland2

abstract class Avatar(
    val name: String, 
    var strength: Int, 
    var wisdom: Int, 
    var charisma: Int) {
  var hitpoints: Int = strength * 2
  override def toString: String = "Avatar: " + name +
    "\n (strength: "+ strength + ", wisdom: "+ wisdom +
    ", charisma: "+ charisma + ")"
}

