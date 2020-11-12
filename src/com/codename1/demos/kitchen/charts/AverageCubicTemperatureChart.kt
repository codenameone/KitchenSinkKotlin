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
import com.codename1.charts.views.CubicLineChart
import com.codename1.charts.views.PointStyle
import com.codename1.demos.kitchen.charts.models.XYMultipleSeriesEditor
import com.codename1.ui.Component

/**
 * Average temperature demo chart.
 */
class AverageCubicTemperatureChart : AbstractDemoChart() {
    private var dataSet: XYMultipleSeriesDataset? = null
        private get() {
            if (field == null) {
                field = createTemperatureDataset()
            }
            return field
        }

    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    override val name: String
        get() = "Average temperature"

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    override val desc: String
        get() =  "The average temperature in 4 Greek islands (cubic line chart)"

    override val chartTitle: String
        get() = "Avg. Cubic Temperature"


    override val chartModelEditor: Component?
        get() {
            val x = XYMultipleSeriesEditor()
            x.init(dataSet)
            return x
        }

    override fun execute(): Component {
        val colors = intArrayOf(ColorUtil.BLUE, ColorUtil.GREEN, ColorUtil.CYAN, ColorUtil.MAGENTA)
        val styles = arrayOf<PointStyle?>(PointStyle.CIRCLE, PointStyle.DIAMOND,
                PointStyle.TRIANGLE, PointStyle.SQUARE)
        val renderer = buildRenderer(colors, styles)
        val length = renderer.seriesRendererCount
        for (i in 0 until length) {
            (renderer.getSeriesRendererAt(i) as XYSeriesRenderer).isFillPoints = true
        }
        setChartSettings(renderer, "Average temperature", "Month", "Temperature", 0.5, 12.5, 0.0, 32.0,
                ColorUtil.LTGRAY, ColorUtil.LTGRAY)
        renderer.xLabels = 12
        renderer.yLabels = 10
        renderer.setShowGrid(true)
        renderer.xLabelsAlign = Component.RIGHT
        renderer.setYLabelsAlign(Component.RIGHT)
        renderer.isZoomButtonsVisible = true
        renderer.panLimits = doubleArrayOf(-10.0, 20.0, -10.0, 40.0)
        renderer.isPanEnabled = true
        renderer.isZoomEnabled = true
        renderer.zoomLimits = doubleArrayOf(-10.0, 20.0, -10.0, 40.0)
        renderer.margins = intArrayOf(20, 30, 80, 0)
        initRenderer(renderer)
        val chart = CubicLineChart(
                dataSet,
                renderer,
                0.33f
        )
        return newChart(chart)
    }

}