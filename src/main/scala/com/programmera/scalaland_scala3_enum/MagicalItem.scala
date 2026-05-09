package com.programmera.scalaland_scala3_enum

case class MagicalItem(
    description: String,
    modifiers: Map[CreatureFeature, Int],
):
  def getModifier(feature: CreatureFeature): Int = modifiers.getOrElse(feature, 0)

  override def toString: String =
    val mods = modifiers.map((k, v) => s"$k $v").mkString(", ")
    s"$description ($mods)"

case class MagicalItemList(items: List[MagicalItem] = Nil):
  def add(item: MagicalItem): MagicalItemList = copy(items = item :: items)

  def calculateModifier(feature: CreatureFeature): Int =
    items.foldLeft(0)((sum, item) => sum + item.getModifier(feature))

  override def toString: String =
    val body = if items.nonEmpty then items.mkString("\n") else ""
    s"---- Magical Items ----\n$body\n-----------------------"
