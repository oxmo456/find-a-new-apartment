package eu.seria.fana

import scala.util.{Failure, Try}


object SortingOption extends Enumeration {

  type SortingOption = Value
  val NewestFirst = Value("newest-first")
  val OldestFirst = Value("oldest-first")

  def apply(values: Option[Seq[String]]): SortingOption = {
    values.flatMap(_.headOption).flatMap(value => {
      Try {
        SortingOption.withName(value)
      }.toOption
    }).getOrElse(OldestFirst)


  }


}
