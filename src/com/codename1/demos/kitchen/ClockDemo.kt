/*
 * Copyright (c) 2012, Codename One and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Codename One designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Codename One through http://www.codenameone.com/ if you
 * need additional information or have any questions.
 */
package com.codename1.demos.kitchen

import com.codename1.ui.*
import com.codename1.ui.geom.GeneralPath
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.plaf.UIManager
import java.util.*
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * This demo shows off low level graphics in Codename One and drawing of shapes, it also demonstrates the
 * flexibility of the image class
 *
 * @author Sergey Gerashenko.
 */
class ClockDemo(parentForm: Form) : Demo() {
    // at 1-minute intervals
    private var shortTickLen = 0

    // at 5-minute intervals
    private var medTickLen = 0

    // at 15-minute intervals
    private var longTickLen = 0

    private var minuteHandWidth = 0f
    private var hourHandWidth = 0f

    override fun createContentPane(): Container? {
        val demoForm = Form(demoId, BorderLayout())
        demoForm.contentPane.uiid = "ComponentDemoContainer"
        val toolbar = demoForm.toolbar
        toolbar.uiid = "DemoToolbar"
        toolbar.titleComponent.uiid = "DemoTitle"

        // Toolbar add source and back buttons.
        val commandStyle = UIManager.getInstance().getComponentStyle("DemoTitleCommand")
        val backCommand = Command.create("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, commandStyle)
        ) { parentForm!!.showBack() }

        val sourceCommand = Command.create("", FontImage.create("{ }", commandStyle)
        ) { CN.execute(sourceCode) }

        toolbar.addCommandToRightBar(sourceCommand)
        toolbar.setBackCommand(backCommand)
        val clock = AnalogClock()

        if (CN.isDesktop()) {
            minuteHandWidth = 3f
            hourHandWidth = 1f
        } else {
            minuteHandWidth = 6f
            hourHandWidth = 3f
        }

        refreshClockColor()
        demoForm.add(BorderLayout.CENTER, clock)
        demoForm.show()
        return null
    }

    private inner class AnalogClock : Component() {
        private var lastRenderedTime: Long = 0
        private val padding = CN.convertToPixels(2f)

        fun start() {
            componentForm.registerAnimated(this)
        }

        fun stop() {
            componentForm.deregisterAnimated(this)
        }

        override fun animate(): Boolean {
            if (System.currentTimeMillis() / 1000 != lastRenderedTime / 1000) {
                lastRenderedTime = System.currentTimeMillis()
                return true
            }
            return false
        }

        public override fun paintBackground(g: Graphics) {
            super.paintBackground(g)
            // Center point.
            val centerX = x + width / 2
            val centerY = y + height / 2
            val radius = min(width, height) / 2 - padding
            drawClock(g, centerX, centerY, radius, 50, 30, 10, false)
            start()
        }
    }

    private fun drawTicks(g: Graphics, centerX: Int, centerY: Int, radius: Int) {
        val tickStroke = Stroke(2f, Stroke.CAP_BUTT, Stroke.JOIN_ROUND, 1f)
        val ticksPath = GeneralPath()

        // Draw a tick for each "second" (1 through 60)
        for (i in 1..60) {

            // default tick length is short
            val len: Int = when {
                i % 15 == 0 -> {
                    // Longest tick at 15-minute intervals.
                    longTickLen
                }
                i % 5 == 0 -> {
                    // Medium ticks at 5-minute intervals.
                    medTickLen
                }
                else -> {
                    // Short ticks every minute.
                    shortTickLen
                }
            }
            val di = i.toDouble() // tick num as double for easier math.

            // Get the angle from 12 O'Clock to this tick (radians)
            val angleFrom12 = di / 60.0 * 2.0 * Math.PI

            // Get the angle from 3 O'Clock to this tick
            // Note: 3 O'Clock corresponds with zero angle in unit circle
            // Makes it easier to do the math.
            val angleFrom3 = Math.PI / 2.0 - angleFrom12

            // Move to the outer edge of the circle at correct position
            // for this tick.
            ticksPath.moveTo(
                    (centerX + cos(angleFrom3) * radius).toFloat(),
                    (centerY - sin(angleFrom3) * radius).toFloat()
            )

            // Draw line inward along radius for length of tick mark.
            ticksPath.lineTo(
                    (centerX + cos(angleFrom3) * (radius - len)).toFloat(),
                    (centerY - sin(angleFrom3) * (radius - len)).toFloat()
            )
        }

        // Draw the ticks.
        g.color = clockColor
        g.drawShape(ticksPath, tickStroke)
    }

    private fun drawNumbers(g: Graphics, centerX: Int, centerY: Int, radius: Int) {
        val coordinates = arrayOfNulls<Coordinate>(12)

        // Calculate all the numbers coordinates.
        for (i in 1..12) {
            // Calculate the string width and height so we can center it properly
            val hourString = i.toString()
            val charWidth = g.font.stringWidth(hourString)
            val charHeight = g.font.height
            val di = i.toDouble() // number as double for easier math

            // Calculate the position along the edge of the clock where the number should
            // be drawn.
            // Get the angle from 12 O'Clock to this tick (radians).
            val angleFrom12 = di / 12.0 * 2.0 * Math.PI

            // Get the angle from 3 O'Clock to this tick
            // Note: 3 O'Clock corresponds with zero angle in unit circle
            // Makes it easier to do the math.
            val angleFrom3 = Math.PI / 2.0 - angleFrom12
            val extraRange = CN.convertToPixels(2f)

            // Get diff between number position and clock center
            val tx = (cos(angleFrom3) * (radius - longTickLen - extraRange)).toInt()
            val ty = (-sin(angleFrom3) * (radius - longTickLen - extraRange)).toInt()
            coordinates[i - 1] = Coordinate(tx + centerX - charWidth / 2, ty + centerY - charHeight / 2)
        }

        // Draw all the numbers.
        for (i in 1..12) {
            val hourString = i.toString()
            g.drawString(hourString, coordinates[i - 1]!!.x, coordinates[i - 1]!!.y)
        }
    }

    private fun drawSecondHand(g: Graphics, centerX: Int, centerY: Int, radius: Int) {
        val secondHand = GeneralPath()
        secondHand.moveTo(centerX.toFloat(), centerY.toFloat())
        secondHand.lineTo(centerX.toFloat(), (centerY - (radius - medTickLen)).toFloat())
        val translatedSecondHand = secondHand.createTransformedShape(
                Transform.makeTranslation(0f, 5f)
        )
        val calendar = Calendar.getInstance(TimeZone.getDefault())

        // Calculate the angle of the second hand
        val second = calendar[Calendar.SECOND].toDouble()
        val secondAngle = second / 60.0 * 2.0 * Math.PI
        g.rotateRadians(secondAngle.toFloat(), centerX, centerY)
        g.color = 0xff0000
        g.drawShape(
                translatedSecondHand,
                Stroke(2f, Stroke.CAP_BUTT, Stroke.JOIN_BEVEL, 1f)
        )
        g.resetAffine()
    }

    private fun drawMinuteHand(g: Graphics, centerX: Int, centerY: Int, radius: Int) {
        // Draw the minute hand
        val minuteHand = GeneralPath()
        minuteHand.moveTo(centerX.toFloat(), centerY.toFloat())
        minuteHand.lineTo(centerX.toFloat() + minuteHandWidth, centerY.toFloat())
        minuteHand.lineTo(centerX.toFloat() + 1, (centerY - (radius - shortTickLen)).toFloat())
        minuteHand.lineTo(centerX.toFloat() - 1, (centerY - (radius - shortTickLen)).toFloat())
        minuteHand.lineTo(centerX.toFloat() - minuteHandWidth, centerY.toFloat())
        minuteHand.closePath()

        // Translate the minute hand slightly down so it overlaps the center
        val translatedMinuteHand = minuteHand.createTransformedShape(
                Transform.makeTranslation(0f, 5f)
        )
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val minute = calendar[Calendar.MINUTE].toDouble() +
                calendar[Calendar.SECOND].toDouble() / 60.0
        val minuteAngle = minute / 60.0 * 2.0 * Math.PI

        // Rotate and draw the minute hand
        g.rotateRadians(minuteAngle.toFloat(), centerX, centerY)
        g.color = clockColor
        g.fillShape(translatedMinuteHand)
        g.resetAffine()
    }

    private fun drawHourHand(g: Graphics, centerX: Int, centerY: Int, radius: Int) {
        // Draw the hour hand
        val hourHand = GeneralPath()
        hourHand.moveTo(centerX.toFloat(), centerY.toFloat())
        hourHand.lineTo(centerX.toFloat() + hourHandWidth, centerY.toFloat())
        hourHand.lineTo(centerX.toFloat() + 1, (centerY - (radius - longTickLen) * 0.75).toFloat())
        hourHand.lineTo(centerX.toFloat() - 1, (centerY - (radius - longTickLen) * 0.75).toFloat())
        hourHand.lineTo(centerX.toFloat() - hourHandWidth, centerY.toFloat())
        hourHand.closePath()
        val translatedHourHand = hourHand.createTransformedShape( Transform.makeTranslation(0f, 5f) )

        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val hour = (calendar[Calendar.HOUR_OF_DAY] % 12).toDouble() +
                calendar[Calendar.MINUTE].toDouble() / 60.0
        val angle = hour / 12.0 * 2.0 * Math.PI
        g.rotateRadians(angle.toFloat(), centerX, centerY)
        g.color = clockColor
        g.fillShape(translatedHourHand)
        g.resetAffine()
    }

    private fun drawClock(g: Graphics, centerX: Int, centerY: Int, radius: Int,
                          longTickLen: Int, medTickLen: Int, shortTickLen: Int, smallVersion: Boolean) {
        this.longTickLen = longTickLen
        this.medTickLen = medTickLen
        this.shortTickLen = shortTickLen

        drawTicks(g, centerX, centerY, radius)
        if (!smallVersion) {
            drawNumbers(g, centerX, centerY, radius)
        }

        drawSecondHand(g, centerX, centerY, radius)
        drawMinuteHand(g, centerX, centerY, radius)
        drawHourHand(g, centerX, centerY, radius)
    }

    private inner class ClockImage : Image {
        private val defaultWidth = 250
        private val defaultHeight = 250
        private var lastRenderedTime: Long = 0
        private var width: Int
        private var height: Int

        constructor() : super(null) {
            width = defaultWidth
            height = defaultHeight
        }

        constructor(width: Int, height: Int) : super(null) {
            this.width = width
            this.height = height
        }

        override fun isAnimation(): Boolean {
            return true
        }

        override fun scale(width: Int, height: Int) {
            this.width = width
            this.height = height
        }

        override fun requiresDrawImage(): Boolean {
            return true
        }

        override fun getWidth(): Int {
            return width
        }

        override fun getHeight(): Int {
            return height
        }

        override fun fill(width: Int, height: Int): Image {
            return ClockImage(width, height)
        }

        override fun drawImage(g: Graphics, nativeGraphics: Any, x: Int, y: Int) {
            drawImage(g, nativeGraphics, x, y, width, height)
        }

        override fun drawImage(g: Graphics, nativeGraphics: Any, x: Int, y: Int, w: Int, h: Int) {
            val radius = min(getWidth(), getHeight()) / 2
            val centerX = x + w / 2
            val centerY = y + h / 2
            drawClock(g, centerX, centerY, radius, 10, 6, 2, true)
        }

        override fun animate(): Boolean {
            if (System.currentTimeMillis() / 1000 != lastRenderedTime / 1000) {
                lastRenderedTime = System.currentTimeMillis()
                return true
            }
            return false
        }
    }

    private inner class Coordinate(val x: Int, val y: Int)
    companion object {
        private var clockColor = 0
        fun refreshClockColor() {
            clockColor = UIManager.getInstance().getComponentStyle("ClockComponent").fgColor
        }
    }

    init {
        init("Clock", ClockImage(), parentForm,
                "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/ClockDemo.java")
    }
}