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
import com.google.code.morphia.logging._

abstract class MongoDB[T](implicit m: Manifest[T]) {

  import com.mongodb.Mongo;
  import com.google.code.morphia.Morphia;
  
  
  val mongo = new Mongo()
  val morphia = new Morphia()

  def connect(): Datastore = {
    MorphiaLoggerFactory.reset()
    MorphiaLoggerFactory.registerLogger(classOf[com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory])
    morphia.mapPackage("models")   
    morphia.createDatastore(mongo, "nameOfDatabase")
  }

  val clz: Class[T] = m.erasure.asInstanceOf[Class[T]]
  

  val connection = connect

  def getById(id: String):Option[T] ={
      try{
            if (id != null)
     Option(connection.get(clz,new ObjectId(id))) else None
      }  catch {
     case e: Exception => None
   }
  }
  def getCount() = Option(connection.getCount())
  def find(field: String, equal: Any) = Option(connection.find(clz).field(field).equal(equal).get)
  
  def findList(field: String, equal: Any) = Option(connection.find(clz).field(field).equal(equal).asList())
  
    def findList(field: String, equal: Any,sort:String) = Option(connection.find(clz).field(field).equal(equal).order(sort).asList())

  def search(field:String, q:String, field2:String,q2:String) = {
   val results = connection.createQuery(clz);
   results.or(
        results.criteria(field).containsIgnoreCase(q),
        results.criteria(field2).containsIgnoreCase(q2)
		   )
    results.asList()
    
  }
  
  def find(field:String, q:Any, field2:String,q2:Any) = {
   val results = connection.createQuery(clz);
   results.and(
        results.criteria(field).equal(q),
        results.criteria(field2).equal(q2)
		   )
    Option(results.get)
  }
  def findAll() =  connection.createQuery(clz).asList.asScala.toList
  def findAll(sort:String) =  Option(connection.createQuery(clz).order(sort).asList.asScala.toList)

  def save(x: T) =  connection.save(List(x).asJava)
  

  def delete(x:T) = connection.delete(x)
   
}

