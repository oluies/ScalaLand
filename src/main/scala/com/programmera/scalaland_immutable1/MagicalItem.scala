package com.programmera.scalaland_immutable1

case class MagicalItem(
    description: String,
    modifiers: Map[CreatureFeature.Value,Int]) {

  def getModifier(feature : CreatureFeature.Value): Int =
    modifiers.get(feature).getOrElse(0)

  override def toString: String = {
    val modStr = modifiers.map { x => s"${x._1} ${x._2}" }.mkString(", ")
    "%s (%s)".format(description, modStr)
  }
}






