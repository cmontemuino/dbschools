package com.dbschools.mgb
package snippet

import xml.{NodeSeq, Text}
import scalaz._
import Scalaz._
import org.scala_tools.time.Imports._
import org.joda.time.{Seconds, Days}
import org.squeryl.PrimitiveTypeMode._
import net.liftweb._
import common.{Full, Loggable}
import util._
import Helpers._
import net.liftweb.http.{SessionVar, SHtml}
import SHtml.{ElemAttr, text, number, onSubmitUnit, ajaxRadio, ajaxButton, ajaxCheckbox, link}
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JsCmds.Replace
import LiftExtensions._
import bootstrap.liftweb.ApplicationPaths
import schema.{Musician, AppSchema}
import model._
import model.TestingManagerMessages._
import Cache.lastAssTimeByMusician

class Students extends SelectedMusician with Loggable {
  private val selectors = svSelectors.is

  private def replaceContents = {
    val elemId = "dynamicSection"
    Replace(elemId, elemFromTemplate("students", s"#$elemId"))
  }

  selectors.opCallback = Some(() => replaceContents)
  def yearSelector = selectors.yearSelector
  def groupSelector = selectors.groupSelector
  def instrumentSelector = selectors.instrumentSelector

  private val lastPassFinder = new LastPassFinder()
  private val lastPassesByMusician = lastPassFinder.lastPassed().groupBy(_.musicianId)

  def createNew = "#create [href]" #> ApplicationPaths.newStudent.href

  def sortBy = {
    val orders = Seq(SortStudentsBy.Name, SortStudentsBy.LastAssessment, SortStudentsBy.LastPiece)
    ajaxRadio[SortStudentsBy.Value](orders, Full(svSortingStudentsBy.is), (s) => {
      svSortingStudentsBy(s)
      replaceContents
    }).flatMap(item => <label style="margin-right: .5em;">{item.xhtml} {item.key.toString} </label>)
  }

  var newId = 0
  var grade = 6
  var name = ""
  var selectedMusicians = Set[Musician]()

  def newStudent = {

    def saveStudent = {
      logger.warn(s"Creating student $newId $grade $name")
      Noop
    }

    "#studentId" #> text(if (newId == 0) "" else newId.toString,
                      id => Helpers.asInt(id).foreach(intId => newId = intId)) &
    "#grade"     #> number(grade, grade = _, grade, 8) &
    "#name"      #> text(name, name = _) &
    "#save"      #> onSubmitUnit(() => saveStudent)
  }

  def render = {
    val fmt = DateTimeFormat.forStyle("S-")

    val groupAssignments = GroupAssignments.sorted(lastPassesByMusician)
    svGroupAssignments(groupAssignments)

    def cbId(musicianId: Int) = "mcb" + musicianId

    def enableButtons =
      JsEnableIf("#schedule", selectedMusicians.nonEmpty) & Students.adjustButtons

    def autoSelectButton = ajaxButton("Check 5", () => {
      val indexOfLastChecked = LastCheckedIndex.find(
        groupAssignments.map(_.musician), selectedMusicians)
      groupAssignments.drop(indexOfLastChecked + 1).take(5).map(row => {
        selectedMusicians += row.musician
        JsCheckIf("#" + cbId(row.musician.id), true)
      }).reduce(_ & _) & enableButtons
    })

    def flattrs(attrs: Option[TheStrBindParam]*): Seq[ElemAttr] = attrs.flatten

    def scheduleButton =
      ajaxButton("Add Checked", () => {
        scheduleSelectedMusicians()
        Noop
      }, flattrs(disableIf(selectedMusicians.isEmpty)): _*)

    def testButton = {
      val empty = model.testingState.enqueuedMusicians.isEmpty
      ajaxButton("Test", () => {
        RedirectTo(ApplicationPaths.testing.href)
      }, flattrs(disableIf(empty), classIf("btn-primary", ! empty)): _*)
    }

    def clearScheduleButton =
      ajaxButton("Clear", () => {
        Actors.testingManager ! ClearQueue
        Noop
      }, flattrs(disableIf(model.testingState.enqueuedMusicians.isEmpty &&
        model.testingState.testingMusicians.isEmpty)): _*)

    (if (selectors.opSelectedTerm   .isDefined) ".schYear" #> none[String] else PassThru) andThen (
    (if (selectors.opSelectedGroupId.isDefined) ".group"   #> none[String] else PassThru) andThen (
    (if (selectors.opSelectedInstId .isDefined) ".instr"   #> none[String] else PassThru) andThen (

    "#autoSelect"             #> autoSelectButton &
    "#schedule"               #> scheduleButton &
    "#test"                   #> testButton &
    "#clearSchedule"          #> clearScheduleButton &
    ".studentRow"             #> {
      def selectionCheckbox(musician: Musician) =
        ajaxCheckbox(false, checked => {
          if (checked) selectedMusicians += musician
          else selectedMusicians -= musician
          enableButtons
        }, "id" -> cbId(musician.id))

      val now = DateTime.now
      groupAssignments.map(row => {
        val lastAsmtTime = lastAssTimeByMusician.get(row.musician.id)
        ".sel      *" #> selectionCheckbox(row.musician) &
        ".schYear  *" #> Terms.formatted(row.musicianGroup.school_year) &
        ".stuName  *" #> studentLink(row.musician) &
        ".grade    *" #> Terms.graduationYearAsGrade(row.musician.graduation_year.get) &
        ".group    *" #> row.group.name &
        ".instr    *" #> row.instrument.name.get &
        ".lastAss  *" #> ~lastAsmtTime.map(fmt.print) &
        ".daysSince *" #> ~lastAsmtTime.map(la => Days.daysBetween(la, now).getDays.toString) &
        ".lastPass *" #> formatLastPasses(lastPassesByMusician.get(row.musician.id))
      })
    })))
  }

  private def formatLastPasses(opLastPasses: Option[Iterable[LastPass]]): NodeSeq = {
    val lastPasses = opLastPasses.getOrElse(Seq[LastPass]()).map(lp => Text(lp.toString))
    lastPasses.fold(NodeSeq.Empty)(_ ++ <br/> ++ _).drop(1)
  }

  def next = {
    val groupAssignments = svGroupAssignments.is
    val groupNames = groupAssignments.map(_.group.name)
    val instNames  = groupAssignments.map(_.instrument.name.get)

    def uniqueOrEmpty(names: Iterable[String]) = names.toSet.toList match {case one :: Nil => s"$one " case _ => ""}

    case class NextInfo(stuLink: xml.Elem, index: Int, count: Int)
    val opNextInfo = for {
      m <- opMusician
      idxThis = groupAssignments.indexWhere(_.musician == m)
      if idxThis >= 0
      idxNext = idxThis + 1
      if idxNext < groupAssignments.size
    } yield NextInfo(Testing.studentNameLink(groupAssignments(idxNext).musician, test = false), idxNext, groupAssignments.size)

    opNextInfo.map(ni =>
      "#snGroup *"      #> uniqueOrEmpty(groupNames) &
      "#snInstrument *" #> uniqueOrEmpty(instNames) &
      "#snLink *"       #> ni.stuLink
    ) getOrElse PassThru
  }

  def inNoGroups = {
    val musicians = join(AppSchema.musicians, AppSchema.musicianGroups.leftOuter)((m, mg) =>
      where(mg.map(_.id).isNull) select m on (m.musician_id.get === mg.map(_.musician_id)))

    ".studentRow"   #> musicians.map(m =>
      ".stuName  *" #> studentLink(m) &
      ".id       *" #> m.id &
      ".stuId    *" #> m.student_id.get &
      ".grade    *" #> Terms.graduationYearAsGrade(m.graduation_year.get)
    )
  }

  private def scheduleSelectedMusicians() {
    val now = DateTime.now
    val scheduledMusicians = selectedMusicians.map(musician => {
      val lastAsmtTime = lastAssTimeByMusician.get(musician.id)
      val opNextPieceName = for {
        lastPasses  <- lastPassesByMusician.get(musician.id)
        lastPass    <- lastPasses.headOption
        nextPiece   <- Cache.nextPiece(lastPass.piece)
      } yield nextPiece.name.get
      val longAgo = 60L * 60 * 24 * 365 * 100
      val secondsSince = lastAsmtTime.map(la => Seconds.secondsBetween(la, now).getSeconds.toLong) | longAgo
      EnqueuedMusician(musician, -secondsSince, opNextPieceName | Cache.pieces.head.name.get)
    })
    Actors.testingManager ! EnqueueMusicians(scheduledMusicians)
  }

  private def studentLink(m: Musician) = {
    link(ApplicationPaths.studentDetails.href, () => {
      svSelectedMusician(Some(m))
    }, Text(m.name))
  }
}

object Students {
  def adjustButtons = {
    val ne = model.testingState.enqueuedMusicians.nonEmpty
    val testSel = "#test"
    JsEnableIf("#clearSchedule", ne) & JsEnableIf(testSel, ne) & JsClassIf(testSel, "btn-primary", ne)
  }
}

object svSortingStudentsBy extends SessionVar[SortStudentsBy.Value](SortStudentsBy.Name)

object svSelectors extends SessionVar[Selectors](new Selectors())

object svGroupAssignments extends SessionVar[Seq[GroupAssignment]](Nil)
