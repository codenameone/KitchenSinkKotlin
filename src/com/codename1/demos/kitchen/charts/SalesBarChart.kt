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

import com.codename1.charts.renderers.XYMultipleSeriesRenderer
import com.codename1.charts.renderers.XYSeriesRenderer
import com.codename1.charts.util.ColorUtil
import com.codename1.charts.views.BarChart
import com.codename1.ui.Component
import java.util.*

/**
 * Sales demo bar chart.
 */
class SalesBarChart : AbstractDemoChart() {
    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    override val name: String
        get() = "Sales horizontal bar chart"

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    override val desc: String
        get() = "The monthly sales for the last 2 years (horizontal bar chart)"
    override val chartModelEditor: Component?
        get() = null
    override val chartTitle: String
        get() = "Sales"

    override fun execute(): Component {
        val titles = arrayOf<String?>("2007", "2008")
        val values: MutableList<DoubleArray> = ArrayList()
        values.add(doubleArrayOf(5230.0, 7300.0, 9240.0, 10540.0, 7900.0, 9200.0, 12030.0, 11200.0, 9500.0, 10500.0, 11600.0, 13500.0))
        values.add(doubleArrayOf(14230.0, 12300.0, 14240.0, 15244.0, 15900.0, 19200.0, 22030.0, 21200.0, 19500.0, 15500.0, 12600.0, 14000.0))
        val colors = intArrayOf(ColorUtil.CYAN, ColorUtil.BLUE)
        val renderer = buildBarRenderer(colors)
        renderer.orientation = XYMultipleSeriesRenderer.Orientation.HORIZONTAL
        setChartSettings(renderer, "Monthly sales in the last 2 years", "Month", "Units sold", 0.5,
                12.5, 0.0, 24000.0, ColorUtil.GRAY, ColorUtil.LTGRAY)
        renderer.xLabels = 1
        renderer.yLabels = 10
        renderer.addXTextLabel(1.0, "Jan")
        renderer.addXTextLabel(3.0, "Mar")
        renderer.addXTextLabel(5.0, "May")
        renderer.addXTextLabel(7.0, "Jul")
        renderer.addXTextLabel(10.0, "Oct")
        renderer.addXTextLabel(12.0, "Dec")
        initRenderer(renderer)
        val length = renderer.seriesRendererCount
        for (i in 0 until length) {
            val seriesRenderer = renderer.getSeriesRendererAt(i) as XYSeriesRenderer
            seriesRenderer.isDisplayChartValues = true
        }
        val chart = BarChart(buildBarDataset(titles, values), renderer,
                BarChart.Type.DEFAULT)
        return newChart(chart)
    }
}