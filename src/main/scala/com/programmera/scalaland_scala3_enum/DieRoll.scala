package com.programmera.scalaland_scala3_enum

object DieRoll:
  def roll(noDice: Int): Int =
    require(noDice > 0, "noDice must be larger than 0")
    (1 to noDice).map(_ => (6 * math.random() + 1).toInt).sum
