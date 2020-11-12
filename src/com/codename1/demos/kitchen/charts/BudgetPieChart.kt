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

import com.codename1.charts.ChartComponent
import com.codename1.charts.models.SeriesSelection
import com.codename1.charts.util.ColorUtil
import com.codename1.charts.views.PieChart
import com.codename1.ui.Component
import com.codename1.ui.geom.Rectangle

/**
 * Budget demo pie chart.
 */
class BudgetPieChart : AbstractDemoChart() {
    override val name: String
        get() = "Budget chart"

    override val desc: String
        get() = "The budget per project for this year (pie chart)"

    override val chartTitle: String
        get() = "Budget"

    override val chartModelEditor: Component?
        get() = null

    private fun shiftColor(color: Int, factor: Double): Int {
        return ColorUtil.rgb(
                Math.min(ColorUtil.red(color) * factor, 255.0).toInt(),
                Math.min(ColorUtil.green(color) * factor, 255.0).toInt(),
                Math.min(ColorUtil.blue(color) * factor, 255.0).toInt()
        )
    }

    override fun execute(): Component {
        val values = doubleArrayOf(12.0, 14.0, 11.0, 10.0, 19.0)
        val colors = intArrayOf(ColorUtil.BLUE, ColorUtil.GREEN, ColorUtil.MAGENTA, ColorUtil.YELLOW, ColorUtil.CYAN)
        val renderer = buildCategoryRenderer(colors)
        for (r in renderer.seriesRenderers) {
            r.isGradientEnabled = true
            r.setGradientStart(0.0, shiftColor(r.color, 0.8))
            r.setGradientStop(0.0, shiftColor(r.color, 1.5))
        }
        renderer.isZoomButtonsVisible = true
        renderer.isZoomEnabled = true
        renderer.setChartTitleTextFont(largeFont)
        renderer.isDisplayValues = true
        renderer.isShowLabels = true
        initRenderer(renderer)
        val seriesSet = buildCategoryDataset("Project budget", values)
        val chart = PieChart(seriesSet, renderer)
        val comp: ChartComponent = object : ChartComponent(chart) {
            private var inDrag = false
            override fun pointerPressed(x: Int, y: Int) {
                inDrag = false
                super.pointerPressed(x, y) //To change body of generated methods, choose Tools | Templates.
            }

            override fun pointerDragged(x: Int, y: Int) {
                inDrag = true
                super.pointerDragged(x, y) //To change body of generated methods, choose Tools | Templates.
            }

            override fun seriesReleased(sel: SeriesSelection) {
                if (inDrag) {
                    // Don't do this if it was a drag operation
                    return
                }
                for (r in renderer.seriesRenderers) {
                    r.isHighlighted = false
                }
                val r = renderer.getSeriesRendererAt(sel.pointIndex)
                r.isHighlighted = true
                val seg = chart.getSegmentShape(sel.pointIndex)
                var bounds = seg.bounds
                bounds = Rectangle(
                        bounds.x - 40,
                        bounds.y - 40,
                        bounds.width + 80,
                        bounds.height + 80
                )
                this.zoomToShapeInChartCoords(bounds, 500)
            }
        }
        comp.isZoomEnabled = true
        comp.isPanEnabled = true
        comp.style.bgColor = 0xff0000
        comp.style.setBgTransparency(255)
        return comp
    }
}