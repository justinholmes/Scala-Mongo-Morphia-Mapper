/*
  A mapper class for Scala to work with Morphia
  
  example
  @Entity(noClassnameStored = true)
  class User {
  @Id
  var id: ObjectId = _
  var name: String = _

  }
  object User extends MongoDB[User] {

  }

 * 
 */
package models

import com.google.code.morphia.Datastore
import org.bson.types.ObjectId
import scalaj.collection.Imports._


abstract class MongoDB[T](implicit m: Manifest[T]) {

  import com.mongodb.Mongo;
  import com.google.code.morphia.Morphia;
  val mongo = new Mongo()
  val morphia = new Morphia()
  var mapped = false;
  def connect(): Datastore = {

    morphia.mapPackage("models")
    morphia.createDatastore(mongo, "test")

  }

  val clz: Class[T] = m.erasure.asInstanceOf[Class[T]]

  val connection = {
    connect
  }

  def getById(id: String):Option[T] ={
    if (id != null)
     Option(connection.get(clz,new ObjectId(id))) else None
  }

  def find(field: String, equal: String) = {
   Option(connection.find(clz).field(field).equal(equal).get)
  }

  def findAll() = {
    connection.createQuery(clz).asList.asScala.toList
  }

  def save(x: T) = {
    connection.save(List(x).asJava)
  }
  def findByGeoLocation(field:String,long:Double,lat:Double) ={
    connection.find(clz).field(field).near(long,lat).get();
  }
  def delete(x:T) = {
    connection.delete(x)
  }
}