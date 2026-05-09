package com.programmera.scalaland_func_final

object RaceType extends Enumeration {
  val Elf, Dwarf = Value
}

object CharacterType extends Enumeration {
  val Thief, Warrior, Wizard = Value
}

object Avatar {
  // Avatar factory method
  def apply(name: String, race: RaceType.Value, character: CharacterType.Value): Avatar = {
    race match {
      case RaceType.Dwarf => {
        character match {
          case CharacterType.Thief => new Avatar(name) with Dwarf with Thief
          case CharacterType.Warrior => new Avatar(name) with Dwarf with Warrior
          case CharacterType.Wizard => new Avatar(name) with Dwarf with Wizard
        }
      }
      case RaceType.Elf => {
        character match {
          case CharacterType.Thief => new Avatar(name) with Elf with Thief
          case CharacterType.Warrior => new Avatar(name) with Elf with Warrior
          case CharacterType.Wizard => new Avatar(name) with Elf with Wizard
        }
      }
    }
  }
}

class Avatar(val name: String) extends Character {
  protected var features = generateFeatures
  val maxHitpoints: Int = features.strength * 3
  protected var currentHitpoints = maxHitpoints

  protected var items: MagicalItemList = new MagicalItemList()

  override def strength: Int = features.strength +
    items.calculateModifier(CharacterFeature.Strength)
  override def wisdom: Int = features.wisdom +
    items.calculateModifier(CharacterFeature.Wisdom)
  override def charisma: Int = features.charisma +
    items.calculateModifier(CharacterFeature.Charisma)

  def addItem(item: MagicalItem): Unit = {
    items = items.add(item)
  }

  override def toString: String = super.toString + "\n" + items
}
