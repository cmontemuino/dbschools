package com.dbschools.mgb
package model

import scalaz._
import Scalaz._
import org.scala_tools.time.Imports._
import snippet.Authenticator
import snippet.Selectors.Selection

object testingState {
  val enqueuedMusicians = new MusicianQueue()
  var testingMusicians = Set[TestingMusician]()
  var chatMessages = List[ChatMessage]()
  var callAfterMinsByTesterId = Map[Int, Option[Int]]().withDefaultValue(Some(TestingManager.defaultNextCallMins))
  var servicingQueueTesterIds = Map[Int, Selection]()
  var servicingQueueTesterIdsReset = false
  var callNowTesterIds = Set[Int]()
  var desktopNotifyByTesterId = Map[Int, Boolean]().withDefaultValue(false)
  def desktopNotify = Authenticator.opLoggedInUser.map(user => desktopNotifyByTesterId(user.id)) | false
  var specialSchedule = false

  /** Returns a TestingDuration for each tester servicing the queue. */
  def testingDurations = {
    val now = DateTime.now
    val testingMusiciansFromQueueByTesterId = testingMusicians.filter(_.fromQueue.nonEmpty).groupBy(_.tester.id)
    val durationsFromQueueServicingSessions =
    (for {
      (testerId, testingMusicians)  <- testingMusiciansFromQueueByTesterId
      selection                     <- servicingQueueTesterIds get testerId
      lastStudentStart  = testingMusicians.map(_.startingTime).reduce {(a, b) => if (a > b) a else b}
      sessionAge        = new Interval(lastStudentStart, now).toDuration
      opCallNow         = if (callNowTesterIds.contains(testerId)) Some(0) else None
    } yield {
      opCallNow orElse callAfterMinsByTesterId(testerId) map(mins => {
        val expectedSessionDuration = new Duration(mins * 60000)
        TesterDuration(testerId, selection, expectedSessionDuration - sessionAge)
      })
    }).flatten
    val zeroDurations = (servicingQueueTesterIds -- testingMusiciansFromQueueByTesterId.keySet).
      map {case (testerId, selection) => TesterDuration(testerId, selection, new Duration(0))}
    (durationsFromQueueServicingSessions ++ zeroDurations).toSeq.sortBy(_.duration.millis)
  }
}
