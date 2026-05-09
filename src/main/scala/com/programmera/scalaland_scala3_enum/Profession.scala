package com.programmera.scalaland_scala3_enum

/** Profession replaces `object ProfessionalType extends Enumeration` AND the
  * `Thief`/`Warrior`/`Wizard` behaviour traits. Each combat / skill method is a single exhaustive
  * `match` on the enum - the compiler verifies completeness, no `case _` escape hatch.
  */
enum Profession:
  case Thief, Warrior, Wizard

  /** Climbed meters; 0 if the profession can't climb. */
  def climb(strength: Int): Int = this match
    case Thief => strength + DieRoll.roll(1)
    case Warrior => DieRoll.roll(1)
    case Wizard => 0

  /** Damage dealt with a weapon attack; 0 if the profession can't fight. */
  def weaponAttack(myStrength: Int, foeStrength: Int): Int = this match
    case Warrior => ((myStrength - foeStrength) / 2 + DieRoll.roll(2)).max(0)
    case Thief => ((myStrength - foeStrength) / 3 + DieRoll.roll(1)).max(0)
    case Wizard => 0

  /** Damage dealt with a magic attack; 0 if the profession can't cast. */
  def magicAttack(myWisdom: Int, foeWisdom: Int): Int = this match
    case Wizard => ((myWisdom - foeWisdom) / 2 + DieRoll.roll(2)).max(0)
    case Thief | Warrior => 0
