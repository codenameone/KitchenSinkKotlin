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
import com.codename1.charts.views.PointStyle
import com.codename1.charts.views.ScatterChart
import com.codename1.demos.kitchen.charts.models.XYMultipleSeriesEditor
import com.codename1.ui.Component
import java.util.*

/**
 * Scatter demo chart.
 */
class ScatterChart : AbstractDemoChart() {
    private var dataSet: XYMultipleSeriesDataset? = null
        private get() {
            if (field == null) {
                val titles = arrayOf("Series 1", "Series 2", "Series 3", "Series 4", "Series 5")
                val x: MutableList<DoubleArray> = ArrayList()
                val values: MutableList<DoubleArray> = ArrayList()
                val count = 20
                val length = titles.size
                val r = Random()
                for (i in 0 until length) {
                    val xValues = DoubleArray(count)
                    val yValues = DoubleArray(count)
                    for (k in 0 until count) {
                        xValues[k] = (k + r.nextInt() % 10).toDouble()
                        yValues[k] = (k * 2 + r.nextInt() % 10).toDouble()
                    }
                    x.add(xValues)
                    values.add(yValues)
                }
                field = buildDataset(titles, x, values)
            }
            return field
        }

    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    override val name: String
        get() = "Scatter chart"

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    override val desc: String
        get() = "Randomly generated values for the scatter chart"
    override val chartModelEditor: Component?
        get() {
            val x = XYMultipleSeriesEditor()
            x.init(dataSet)
            return x
        }
    override val chartTitle: String
        get() = "Scatter chart"

    override fun execute(): Component {
        val colors = intArrayOf(ColorUtil.BLUE, ColorUtil.CYAN, ColorUtil.MAGENTA, ColorUtil.LTGRAY, ColorUtil.GREEN)
        val styles = arrayOf<PointStyle?>(PointStyle.X, PointStyle.DIAMOND, PointStyle.TRIANGLE,
                PointStyle.SQUARE, PointStyle.CIRCLE)
        val renderer = buildRenderer(colors, styles)
        setChartSettings(renderer, "Scatter chart", "X", "Y", -10.0, 30.0, -10.0, 51.0, ColorUtil.GRAY,
                ColorUtil.LTGRAY)
        renderer.xLabels = 10
        renderer.yLabels = 10
        for (i in 0..4) {
            (renderer.getSeriesRendererAt(i) as XYSeriesRenderer).isFillPoints = true
        }
        initRenderer(renderer)
        val chart = ScatterChart(dataSet, renderer)
        return newChart(chart)
    }
}