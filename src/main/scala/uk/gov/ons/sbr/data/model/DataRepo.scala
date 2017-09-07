package uk.gov.ons.sbr.data.model

trait DataRepo [T] {

  def insert(data: T): T

  def update(data: T): T

  def delete(data: T): Unit

  def deleteAll: Unit

  def count(): Long

}