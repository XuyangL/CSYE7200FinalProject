package models

import play.api.data.Form
import play.api.data.Forms._

case class BasicForm(CreditUsage: Int, Age: Int, PastDue_30_59: Int, DebtRatio: BigDecimal,
MonthlyIncome: Int, NumberOfOpenCreditLinesAndLoans: Int, PastDue_90: Int,
NumberRealEstateLoansOrLines: Int, PastDue_60_89: Int, Dependents: Int)

// this could be defined somewhere else,
// but I prefer to keep it in the companion object
object BasicForm {
  val form: Form[BasicForm] = Form(
    mapping(
      "CreditUsage" -> number,
      "Age" -> number,
      "PastDue_30_59" -> number,
      "DebtRatio" -> bigDecimal,
      "MonthlyIncome" -> number,
      "NumberOfOpenCreditLinesAndLoans" -> number,
      "PastDue_90" -> number,
      "NumberRealEstateLoansOrLines" -> number,
      "PastDue_60_89" -> number,
      "Dependents" -> number
    )(BasicForm.apply)(BasicForm.unapply)
  )
}
