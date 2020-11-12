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

import com.codename1.charts.models.AreaSeries
import com.codename1.charts.models.CategorySeries
import com.codename1.charts.util.ColorUtil
import com.codename1.charts.views.RadarChart
import com.codename1.ui.Component

class EmployeeChart : AbstractDemoChart() {
    override val chartTitle: String
        get() = "Employee"

    override val chartModelEditor: Component?
        get() = null

    override val name: String
        get() = "Radar chart"

    override val desc: String
        get() = "Generate Radar chart"

    override fun execute(): Component {
        val colors = intArrayOf(ColorUtil.BLUE)
        val renderer = buildCategoryRenderer(colors)
        initRenderer(renderer)
        val chart = RadarChart(dataSet, renderer)
        return newChart(chart)
    }

    private val dataSet: AreaSeries
        get() {
            val series = CategorySeries("Employee")
            series.add("technical skills", 0.5)
            series.add("sense of humor", 0.3)
            series.add("personality", 0.8)
            series.add("accomplishment", 0.4)
            series.add("experience", 0.9)
            val dataSet = AreaSeries()
            dataSet.addSeries(series)
            return dataSet
        }
}