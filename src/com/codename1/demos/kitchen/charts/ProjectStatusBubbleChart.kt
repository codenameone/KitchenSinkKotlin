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
import com.codename1.charts.models.XYValueSeries
import com.codename1.charts.renderers.XYMultipleSeriesRenderer
import com.codename1.charts.renderers.XYSeriesRenderer
import com.codename1.charts.util.ColorUtil
import com.codename1.charts.views.BubbleChart
import com.codename1.ui.Component

/**
 * Project status demo bubble chart.
 */
class ProjectStatusBubbleChart : AbstractDemoChart() {
    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    override val name: String
        get() = "Project tickets status"

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    override val desc: String
        get() = "The opened tickets and the fixed tickets (bubble chart)"
    override val chartModelEditor: Component?
        get() = null
    override val chartTitle: String
        get() = "Project tickets"

    override fun execute(): Component {
        val series = XYMultipleSeriesDataset()
        val newTicketSeries = XYValueSeries("New Tickets")
        newTicketSeries.add(1.0, 2.0, 14.0)
        newTicketSeries.add(2.0, 2.0, 12.0)
        newTicketSeries.add(3.0, 2.0, 18.0)
        newTicketSeries.add(4.0, 2.0, 5.0)
        newTicketSeries.add(5.0, 2.0, 1.0)
        series.addSeries(newTicketSeries)
        val fixedTicketSeries = XYValueSeries("Fixed Tickets")
        fixedTicketSeries.add(1.0, 1.0, 7.0)
        fixedTicketSeries.add(2.0, 1.0, 4.0)
        fixedTicketSeries.add(3.0, 1.0, 18.0)
        fixedTicketSeries.add(4.0, 1.0, 3.0)
        fixedTicketSeries.add(5.0, 1.0, 1.0)
        series.addSeries(fixedTicketSeries)
        val renderer = XYMultipleSeriesRenderer()
        renderer.setAxisTitleTextFont(medFont)
        renderer.setChartTitleTextFont(largeFont)
        renderer.setLabelsTextFont(medFont)
        renderer.setLegendTextFont(medFont)
        renderer.margins = intArrayOf(20, 30, 80, 0)
        val newTicketRenderer = XYSeriesRenderer()
        newTicketRenderer.color = ColorUtil.BLUE
        renderer.addSeriesRenderer(newTicketRenderer)
        val fixedTicketRenderer = XYSeriesRenderer()
        fixedTicketRenderer.color = ColorUtil.GREEN
        renderer.addSeriesRenderer(fixedTicketRenderer)
        initRenderer(renderer)
        setChartSettings(renderer, "Project work status", "Priority", "", 0.5, 5.5, 0.0, 5.0, ColorUtil.GRAY,
                ColorUtil.LTGRAY)
        renderer.xLabels = 7
        renderer.yLabels = 0
        renderer.setShowGrid(false)
        val chart = BubbleChart(series, renderer)
        return newChart(chart)
    }
}