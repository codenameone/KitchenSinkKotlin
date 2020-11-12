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

import com.codename1.charts.util.ColorUtil
import com.codename1.charts.views.DoughnutChart
import com.codename1.ui.Component
import java.util.*

/**
 * Budget demo pie chart.
 */
class BudgetDoughnutChart : AbstractDemoChart() {

    override val chartTitle: String
        get() = "Doughnut Chart Demo"

    override val chartModelEditor: Component?
        get() = null

    override val name: String
        get() = "Budget chart for several years"

    override val desc: String
        get() = "The budget per project for several years (doughnut chart)"

    override fun execute(): Component {
        val values: MutableList<DoubleArray?> = ArrayList()
        values.add(doubleArrayOf(12.0, 14.0, 11.0, 10.0, 19.0))
        values.add(doubleArrayOf(10.0, 9.0, 14.0, 20.0, 11.0))
        val titles: MutableList<Array<String?>?> = ArrayList()
        titles.add(arrayOf("P1", "P2", "P3", "P4", "P5"))
        titles.add(arrayOf("Project1", "Project2", "Project3", "Project4", "Project5"))
        val colors = intArrayOf(ColorUtil.BLUE, ColorUtil.GREEN, ColorUtil.MAGENTA, ColorUtil.YELLOW, ColorUtil.CYAN)
        val renderer = buildCategoryRenderer(colors)
        renderer.isApplyBackgroundColor = true
        renderer.labelsColor = ColorUtil.GRAY
        initRenderer(renderer)
        val chart = DoughnutChart(buildMultipleCategoryDataset("Project budget", titles, values), renderer)
        return newChart(chart)
    }
}