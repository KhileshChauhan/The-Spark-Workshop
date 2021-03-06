package Exercise3_05

import java.time.LocalDateTime
import org.apache.hadoop.io.Text
import org.apache.spark.rdd.RDD
import Utilities01.HelperScala.createSession
import Utilities02.HelperScala.{extractRawRecords, parseRawWarc, sampleWarcLoc}
import Utilities02.WarcRecord

object Exercise3_05 {

  def fallAsleep(record: WarcRecord): (Long, String) = {
    val startTime = LocalDateTime.now()
    val currentUri = record.targetURI
    val threadId: Long = Thread.currentThread().getId
    println(s"1@ falling asleep in thread $threadId at $startTime processing $currentUri")
    Thread.sleep(3000)
    val endTime = LocalDateTime.now()
    println(s"2@ awakening in thread $threadId at $endTime processing $currentUri")
    (Thread.currentThread().getId, currentUri)
  }

  def trivialFilter(threadIdUri: (Long, String)): Boolean = {
    val newThreadId: Long = Thread.currentThread().getId
    val timePoint = LocalDateTime.now()
    println(s"3@ filter in thread $newThreadId at $timePoint processing ${threadIdUri._2}")
    true
  }

  def quickPrint(threadIdUri: (Long, String)): (Long, Long) = {
    val newThreadId: Long = Thread.currentThread().getId
    val timePoint = LocalDateTime.now()
    println(s"4@ map2 in thread $newThreadId at $timePoint processing ${threadIdUri._2}")
    (threadIdUri._1, newThreadId)
  }

  def main(args: Array[String]): Unit = {
    implicit val session = createSession(3, "Wave exploration")
    val rawRecords: RDD[Text] = extractRawRecords(sampleWarcLoc)
    val warcRecords: RDD[WarcRecord] = rawRecords
      .flatMap(parseRawWarc)

    val threadIdsRDD: RDD[(Long, Long)] = warcRecords
      .map(fallAsleep)
      .filter(trivialFilter)
      .map(quickPrint)

    val distinctThreads: List[(Long, Long)] = threadIdsRDD.distinct().collect().toList
    println(distinctThreads)
  }
}
