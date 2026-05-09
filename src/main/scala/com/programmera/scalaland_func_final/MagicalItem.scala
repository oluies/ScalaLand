package com.programmera.scalaland_func_final

case class MagicalItemList(items: List[MagicalItem] = Nil) {

  def add(item: MagicalItem): MagicalItemList = copy(items = item :: this.items)

  def calculateModifier(feature: CharacterFeature.Value): Int = {
    items.foldLeft(0) { (sum, item) =>
      sum + item.getModifier(feature)
    }
  }

  override def toString: String = {
    val itemsStr =
      if (items.length > 0) items.mkString("", "\n", "\n")
      else ""
    "---- Magical Items ----\n%s-----------------------".format(itemsStr)
  }
}

class MagicalItem(val description: String, val modifiers: Map[CharacterFeature.Value, Int]) {

  def getModifier(feature: CharacterFeature.Value): Int =
    modifiers.get(feature).getOrElse(0)

  override def toString: String = {
    val modStr = modifiers.map(x => s"${x._1} ${x._2}").mkString(", ")
    "%s (%s)".format(description, modStr)
  }
}
