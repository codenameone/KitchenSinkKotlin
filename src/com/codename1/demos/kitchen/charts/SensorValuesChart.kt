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
package com.codename1.demos.kitchen.charts

import com.codename1.charts.models.XYMultipleSeriesDataset
import com.codename1.charts.renderers.XYSeriesRenderer
import com.codename1.charts.util.ColorUtil
import com.codename1.charts.util.MathHelper
import com.codename1.charts.views.PointStyle
import com.codename1.charts.views.TimeChart
import com.codename1.demos.kitchen.charts.models.XYMultipleSeriesEditor
import com.codename1.ui.Component
import com.codename1.ui.Display
import java.util.*

/**
 * Temperature sensor demo chart.
 */
class SensorValuesChart : AbstractDemoChart() {
    private var dataSet: XYMultipleSeriesDataset? = null
        private get() {
            if (field == null) {
                val titles = arrayOf<String?>("Inside", "Outside")
                val now = Math.round(Date().time / DAY.toFloat()) * DAY
                x = ArrayList()
                for (i in titles.indices) {
                    val dates = arrayOfNulls<Date>(HOURS)
                    for (j in 0 until HOURS) {
                        dates[j] = Date(now - (HOURS - j) * HOUR)
                    }
                    (x as ArrayList<Array<Date?>>).add(dates)
                }
                val values: MutableList<DoubleArray> = ArrayList()
                values.add(doubleArrayOf(21.2, 21.5, 21.7, 21.5, 21.4, 21.4, 21.3, 21.1, 20.6, 20.3, 20.2,
                        19.9, 19.7, 19.6, 19.9, 20.3, 20.6, 20.9, 21.2, 21.6, 21.9, 22.1, 21.7, 21.5))
                values.add(doubleArrayOf(1.9, 1.2, 0.9, 0.5, 0.1, -0.5, -0.6, MathHelper.NULL_VALUE,
                        MathHelper.NULL_VALUE, -1.8, -0.3, 1.4, 3.4, 4.9, 7.0, 6.4, 3.4, 2.0, 1.5, 0.9, -0.5,
                        MathHelper.NULL_VALUE, -1.9, -2.5, -4.3))
                field = buildDateDataset(titles, x as ArrayList<Array<Date?>>, values)
            }
            return field
        }
    private var x: MutableList<Array<Date?>>? = null

    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    override val name: String
        get() = "Sensor data"

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    override val desc: String
        get() = "The temperature, as read from an outside and an inside sensors"
    override val chartModelEditor: Component?
        get() {
            val x = XYMultipleSeriesEditor()
            x.init(dataSet)
            return x
        }
    override val chartTitle: String
        get() = "Temperature"

    override fun execute(): Component {
        val colors = intArrayOf(ColorUtil.GREEN, ColorUtil.BLUE)
        val styles = arrayOf<PointStyle?>(PointStyle.CIRCLE, PointStyle.DIAMOND)
        val renderer = buildRenderer(colors, styles)
        val length = renderer.seriesRendererCount
        for (i in 0 until length) {
            (renderer.getSeriesRendererAt(i) as XYSeriesRenderer).isFillPoints = true
        }

        // lazy initialization of x...
        dataSet
        setChartSettings(renderer, "Sensor temperature", "Hour", "Celsius degrees",
                x!![0][0]!!.time.toDouble(), x!![0][HOURS - 1]!!.time.toDouble(), -5.0, 30.0, ColorUtil.LTGRAY, ColorUtil.LTGRAY)
        val strWidth = Display.getInstance().convertToPixels(25f)
        val numXLabels = Display.getInstance().displayWidth / (strWidth + 20)
        renderer.xLabels = numXLabels
        renderer.yLabels = 10
        renderer.setShowGrid(true)
        renderer.xLabelsAlign = Component.CENTER
        renderer.setYLabelsAlign(Component.RIGHT)
        renderer.margins = intArrayOf(20, 30, 80, 0)
        initRenderer(renderer)
        val chart = TimeChart(dataSet,
                renderer)
        return newChart(chart)
    }

    companion object {
        private const val HOUR = 3600 * 1000.toLong()
        private const val DAY = HOUR * 24
        private const val HOURS = 24
    }
}