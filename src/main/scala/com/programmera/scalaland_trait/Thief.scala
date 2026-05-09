package com.programmera.scalaland_trait

trait Thief extends Professional {

  override def toString: String = super.toString + "\n is a thief."

  // Good climber
  override def climb: Int = {
    strength + DieRoll.roll(1)
  }
}
  


