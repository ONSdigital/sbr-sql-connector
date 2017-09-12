package uk.gov.ons.sbr.data.db

trait SbrDao [T] {

  def insert(data: T): T

  def update(data: T): T

  def delete(data: T): Unit

  def deleteAll: Unit

  def count(): Long

}