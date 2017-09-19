/**
 * SbrDao.scala
 * --------------
 * Author: websc
 * Date: 19/09/17 14:01
 * Copyright (c) 2017  Office for National Statistics
 */

package uk.gov.ons.sbr.data.db

trait SbrDao[T]{

  def insert(data: T): T

  def update(data: T): T

  def delete(data: T): Unit

  def deleteAll: Unit

  def count(): Long

}
