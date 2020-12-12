package models
import scala.collection.mutable.ArrayBuffer


case class Record(id: String, recordData: List[String],  rf: String,
                 lr: String, decision:String,  time: String)

case class SimRecord(id: String, rf: String, lr: String, decision:String,  time: String)


object RecordDao {

  private val recordDB = ArrayBuffer[Record]()

  def addRecord(r : Record): Unit = {

    recordDB += r
  }

  def getRecord() : List[Record] = {

    recordDB.toList
  }

  def getOneRecord(id: String): Record = {
    recordDB.filter(r => r.id == id).head
  }
}
