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

import com.codename1.charts.models.XYSeries
import com.codename1.charts.models.XYValueSeries
import com.codename1.charts.renderers.XYSeriesRenderer
import com.codename1.charts.util.ColorUtil
import com.codename1.charts.views.*
import com.codename1.charts.views.CombinedXYChart.XYCombinedChartDef
import com.codename1.ui.Component
import java.util.*

/**
 * Combined temperature demo chart.
 */
class CombinedTemperatureChart : AbstractDemoChart() {

    override val name: String
        get() = "Combined temperature"

    override val desc: String
        get() = "The average temperature in 2 Greek islands, water temperature and sun shine hours (combined chart)"

    override val chartTitle: String
        get() = "Weather parameters"

    override val chartModelEditor: Component?
        get() = null

    override fun execute(): Component {
        val titles = arrayOf("Crete Air Temperature", "Skiathos Air Temperature")
        val x: MutableList<DoubleArray> = ArrayList()
        for (i in titles.indices) {
            x.add(doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0))
        }
        val values: MutableList<DoubleArray> = ArrayList()
        values.add(doubleArrayOf(12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2,
                13.9))
        values.add(doubleArrayOf(9.0, 10.0, 11.0, 15.0, 19.0, 23.0, 26.0, 25.0, 22.0, 18.0, 13.0, 10.0))
        val colors = intArrayOf(ColorUtil.GREEN, ColorUtil.rgb(200, 150, 0))
        val styles = arrayOf<PointStyle?>(PointStyle.CIRCLE, PointStyle.DIAMOND)
        val renderer = buildRenderer(colors, styles)
        renderer.pointSize = 5.5f
        val length = renderer.seriesRendererCount
        for (i in 0 until length) {
            val r = renderer.getSeriesRendererAt(i) as XYSeriesRenderer
            r.lineWidth = 2f
            r.isFillPoints = true
        }
        setChartSettings(renderer, "Weather data", "Month", "Temperature", 0.5, 12.5, 0.0, 40.0,
                ColorUtil.LTGRAY, ColorUtil.LTGRAY)
        renderer.xLabels = 12
        renderer.yLabels = 10
        renderer.setShowGrid(true)
        renderer.margins = intArrayOf(20, 30, 80, 0)
        renderer.xLabelsAlign = Component.RIGHT
        renderer.setYLabelsAlign(Component.RIGHT)
        renderer.isZoomButtonsVisible = true
        renderer.panLimits = doubleArrayOf(-10.0, 20.0, -10.0, 40.0)
        renderer.zoomLimits = doubleArrayOf(-10.0, 20.0, -10.0, 40.0)
        val sunSeries = XYValueSeries("Sunshine hours")
        sunSeries.add(1.0, 35.0, 4.3)
        sunSeries.add(2.0, 35.0, 4.9)
        sunSeries.add(3.0, 35.0, 5.9)
        sunSeries.add(4.0, 35.0, 8.8)
        sunSeries.add(5.0, 35.0, 10.8)
        sunSeries.add(6.0, 35.0, 11.9)
        sunSeries.add(7.0, 35.0, 13.6)
        sunSeries.add(8.0, 35.0, 12.8)
        sunSeries.add(9.0, 35.0, 11.4)
        sunSeries.add(10.0, 35.0, 9.5)
        sunSeries.add(11.0, 35.0, 7.5)
        sunSeries.add(12.0, 35.0, 5.5)
        val lightRenderer = XYSeriesRenderer()
        lightRenderer.color = ColorUtil.MAGENTA
        val waterSeries = XYSeries("Crete Water Temperature")
        waterSeries.add(1.0, 16.0)
        waterSeries.add(2.0, 15.0)
        waterSeries.add(3.0, 16.0)
        waterSeries.add(4.0, 17.0)
        waterSeries.add(5.0, 20.0)
        waterSeries.add(6.0, 23.0)
        waterSeries.add(7.0, 25.0)
        waterSeries.add(8.0, 25.5)
        waterSeries.add(9.0, 26.5)
        waterSeries.add(10.0, 24.0)
        waterSeries.add(11.0, 22.0)
        waterSeries.add(12.0, 18.0)
        val waterSeries2 = XYSeries("Skiathos Water Temperature")
        waterSeries2.add(1.0, 15.0)
        waterSeries2.add(2.0, 14.0)
        waterSeries2.add(3.0, 14.0)
        waterSeries2.add(4.0, 15.0)
        waterSeries2.add(5.0, 18.0)
        waterSeries2.add(6.0, 22.0)
        waterSeries2.add(7.0, 24.0)
        waterSeries2.add(8.0, 25.0)
        waterSeries2.add(9.0, 24.0)
        waterSeries2.add(10.0, 21.0)
        waterSeries2.add(11.0, 18.0)
        waterSeries2.add(12.0, 16.0)
        renderer.barSpacing = 0.3
        val waterRenderer1 = XYSeriesRenderer()
        waterRenderer1.color = -0xff6634
        waterRenderer1.chartValuesTextAlign = Component.CENTER
        val waterRenderer2 = XYSeriesRenderer()
        waterRenderer2.color = -0x66cc34
        waterRenderer2.chartValuesTextAlign = Component.RIGHT
        val dataset = buildDataset(titles, x, values)
        dataset.addSeries(0, sunSeries)
        dataset.addSeries(0, waterSeries)
        dataset.addSeries(0, waterSeries2)
        renderer.addSeriesRenderer(0, lightRenderer)
        renderer.addSeriesRenderer(0, waterRenderer1)
        renderer.addSeriesRenderer(0, waterRenderer2)
        waterRenderer1.isDisplayChartValues = true
        waterRenderer1.chartValuesTextSize = smallFont.height / 2.toFloat()
        waterRenderer2.isDisplayChartValues = true
        waterRenderer2.chartValuesTextSize = smallFont.height / 2.toFloat()
        val types = arrayOf(
                XYCombinedChartDef(BarChart.TYPE, 0, 1), XYCombinedChartDef(BubbleChart.TYPE, 2),
                XYCombinedChartDef(LineChart.TYPE, 3), XYCombinedChartDef(CubicLineChart.TYPE, 4))
        initRenderer(renderer)
        val chart = CombinedXYChart(dataset, renderer, types)
        return newChart(chart)
    }
}