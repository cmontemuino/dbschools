package com.dbschools.perclock

import scala.scalajs.js
import org.scalajs.dom
import dom.document
import model.{Period, Periods}

object ClassPeriods extends js.JSApp {

  def main(): Unit = {
    def byId(id: String) = document.getElementById(id)
    val timeRemaining = byId("timeRemaining")
    val period        = byId("period")
    val periodNumber  = byId("periodNumber")
    val periodRange   = byId("periodRange")
    val periodLength  = byId("periodLength")
    val progressMins  = byId("progressMins")

    def update(): Unit = {
      Periods.periodWithin match {
        case p: Period =>
          period.setAttribute("class", "")
          val secs = Math.floor((p.endMs - Periods.nowMs) / 1000)
          timeRemaining.setAttribute("max", p.totalSecs.toString)
          timeRemaining.setAttribute("value", secs.toString)
          periodNumber.innerHTML = p.num.toString
          periodRange.innerHTML = p.formattedRange
          periodLength.innerHTML = (p.totalSecs.toInt / 60).toString
          val remaining = p.formattedTimeRemaining
          progressMins.innerHTML = remaining
          dom.document.title = remaining
        case _ =>
          period.setAttribute("class", "hide")
          timeRemaining.setAttribute("value", "0")
          periodNumber.innerHTML = ""
          periodRange.innerHTML = ""
          periodLength.innerHTML = ""
          progressMins.innerHTML = ""
          dom.document.title = "Between Periods"
      }
    }

    update()

    dom.setInterval(update _, 1000)

    val drawing = byId("drawing")
    val XPad = 15 // todo Find how to get main’s padding
    val Height = 350
    val TopMargin = 12
    val ColMargin = 6

    val firstStartMs = Periods.week.map(_.map(_.startMs).min).min
    val lastEndMs    = Periods.week.map(_.map(_.endMs  ).max).max
    val totalMs = lastEndMs - firstStartMs
    def yFromMs(ms: Double) = (TopMargin + ((ms - firstStartMs) / totalMs) * (Height - TopMargin)).toInt
    def mkSeq(line: String) = line.split(" +").toSeq
    val fills = mkSeq("red   orange yellow green blue  indigo violet")
    val texts = mkSeq("white black  black  white white white  black")
    val days  = mkSeq("Mon Tue Wed Thu Fri")
    val colWidth = (drawing.clientWidth - ColMargin * (5 - 1)) / 5
    var labeledStartTimes = Set[Double]()
    var labeledEndTimes   = Set[Double]()

    Periods.week.zipWithIndex.foreach {
      case (periods, i) =>
        val colX = i * (colWidth + ColMargin)

        def createTextDiv(text: String, x: Int, y: Int, color: String = "black") = {
          val td = document.createElement("div")
          td.setAttribute("class", "period")
          td.setAttribute("style", s"top: ${y}px; left: ${x}px; font-size: .5em; border: none; color: $color")
          td.appendChild(document.createTextNode(text))
          td
        }

        drawing.appendChild(createTextDiv(days(i), colX + XPad, TopMargin - 11))

        periods.foreach(p => {
          val yStart = yFromMs(p.startMs)
          val yEnd   = yFromMs(p.endMs)
          val periodDiv = document.createElement("div")
          periodDiv.setAttribute("class", "period")
          periodDiv.setAttribute("style",
            s"top: ${yStart}px; height: ${yEnd - yStart}px; left: ${colX + XPad}px; width: ${colWidth}px; " +
            s"background-color: ${fills(p.num - 1)}")
          drawing.appendChild(periodDiv)

          val labelX = colX + XPad + 2
          val textColor = texts(p.num - 1)
          if (! labeledStartTimes.contains(p.startMs)) {
            drawing.appendChild(createTextDiv(p.formattedStart, labelX, yStart, color = textColor))
            labeledStartTimes += p.startMs
          }
          if (! labeledEndTimes.contains(p.endMs)) {
            drawing.appendChild(createTextDiv(p.formattedEnd, labelX, yEnd - 13, color = textColor))
            labeledEndTimes += p.endMs
          }
        })
    }
  }
}
