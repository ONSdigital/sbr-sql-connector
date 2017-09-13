package uk.gov.ons.sbr.data.model

import play.api.libs.json.Json
import uk.gov.ons.sbr.data.model.UnitType.UnitType

case class StatUnit(refPeriod: Long,
                    key: String,
                    unitType: String,
                    variables: Map[String, String] = Map.empty[String, String],
                    var children: Seq[StatUnit] = Nil)

object StatUnit {
  // This bit will allow us to convert to/from JSON
  implicit val suWrites = Json.writes[StatUnit]

  def apply(obj: Enterprise): StatUnit = {
    // convert an Enterprise to a StatUnit of type ENT
    StatUnit(refPeriod = obj.ref_period, key = obj.entref.toString,unitType = UnitType.ENT.toString,
      variables = Enterprise.variablesToMap(obj)
    )
  }

  def apply(obj: LegalUnit): StatUnit = {
    // convert to a StatUnit of type LEU
    StatUnit(refPeriod = obj.ref_period, key = obj.ubrn.toString,unitType = UnitType.LEU.toString,
      variables = LegalUnit.variablesToMap(obj)
    )
  }

 def apply(obj: Company): StatUnit = {
    // convert to a StatUnit of type CH
    StatUnit(refPeriod = obj.ref_period, key = obj.companynumber,unitType = UnitType.CH.toString,
      variables = Company.variablesToMap(obj)
    )
  }

  def apply(obj: Paye): StatUnit = {
    // convert to a StatUnit of type PAYE
    StatUnit(refPeriod = obj.ref_period, key = obj.payeref,unitType = UnitType.PAYE.toString,
      variables = Paye.variablesToMap(obj)
    )
  }

  def apply(obj: Vat): StatUnit = {
    // convert to a StatUnit of type VAT
    StatUnit(refPeriod = obj.ref_period, key = obj.vatref,unitType = UnitType.VAT.toString,
      variables = Vat.variablesToMap(obj)
    )
  }
}
