package uk.gov.ons.sbr.data.model

import uk.gov.ons.sbr.data.model.UnitType.UnitType

case class StatUnit(refPeriod: Long,
                    key: String,
                    unitType: UnitType,
                    variables: Map[String, String] = Map.empty[String, String],
                    children: Seq[StatUnit] = Nil,
                    links: Option[StatUnitLinks] = None)

object StatUnit {

  def apply(obj: Enterprise): StatUnit = {
    // convert an Enterprise to a StatUnit of type ENT
    StatUnit(refPeriod = obj.ref_period, key = obj.entref.toString,unitType = UnitType.ENT,
      variables = Enterprise.variablesToMap(obj)
    )
  }

  def apply(obj: LegalUnit): StatUnit = {
    // convert an Enterprise to a StatUnit of type CH
    StatUnit(refPeriod = obj.ref_period, key = obj.ubrn.toString,unitType = UnitType.LEU,
      variables = LegalUnit.variablesToMap(obj)
    )
  }

 /* def apply(obj: Company): StatUnit = {
    // convert an Enterprise to a StatUnit of type CH
    StatUnit(refPeriod = obj.ref_period, key = obj.companynumber,unitType = UnitType.CH,
      variables = Company.variablesToMap(obj)
    )
  }*/
}
