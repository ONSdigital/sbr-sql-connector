/**
 * UnitType.scala
 * --------------
 * Author: websc
 * Date: 18/09/17 15:21
 * Copyright (c) 2017  Office for National Statistics
 */

package uk.gov.ons.sbr.data.model

object UnitType extends Enumeration  {
  /*
   Scala Enumerations are problematic because they all evaluate to the same type (Enumeration),
   so it can be hard to distinguish between them if you use different Enumerations in the same code.
   In this case, we are not using other Enumerations, so it should be OK.

   Also, Enumeration gives us an easy way to create an instance from a String:

   e.g. val ent: UnitType = UnitType.withName("ENT")

   */
  type UnitType = Value
  val ENT, LEU, CH, PAYE, VAT = Value
}

