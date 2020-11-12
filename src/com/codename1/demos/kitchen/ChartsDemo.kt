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

import com.codename1.demos.kitchen.charts.*
import com.codename1.ui.*
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.util.Resources

/**
 * Class that demonstrate the usage of the Chart components.
 * The charts package enables Codename One developers to add charts and visualizations to their
 * apps without having to include external libraries or embedding web views.
 *
 * @author Sergey Gerashenko.
 */
class ChartsDemo(parentForm: Form) : Demo() {

    override fun createContentPane(): Container? {
        val demoContainer = Container(BoxLayout(BoxLayout.Y_AXIS), "DemoContainer")
        demoContainer.isScrollableY = true

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("chart-bar.png"),
                "Bar Chart",
                "The bar chart rendering class") {
            val chart: AbstractDemoChart = SalesBarChart()
            showChart(chart)
        })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("chart-bubble.png"),
                "Bubble Chart",
                "The bubble chart rendering class") {
            val chart: AbstractDemoChart = ProjectStatusBubbleChart()
            showChart(chart)
        })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("chart-combined-xy.png"),
                "CombinedXY Chart",
                "The combinedXY chart rendering class") {
            val chart: AbstractDemoChart = CombinedTemperatureChart()
            showChart(chart)
        })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("chart-cubic-line.png"),
                "CubicLine Chart",
                "The interpolated (cubic) line chart rendering",
                " class") {
            val chart: AbstractDemoChart = AverageCubicTemperatureChart()
            showChart(chart)
        })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("chart-dial.png"),
                "Dial Chart",
                "The dial chart rendering class") {
            val chart = WeightDialChart()
            showChart(chart)
        })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("chart-donut.png"),
                "Donut Chart",
                "The donut chart rendering class") {
            val chart = BudgetDoughnutChart()
            showChart(chart)
        })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("chart-line.png"),
                "Line Chart",
                "The lineChart rendering class") {
            val chart = TrigonometricFunctionsChart()
            showChart(chart)
        })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("chart-pie.png"),
                "Pie Chart",
                "The pie chart rendering class") {
            val chart = BudgetPieChart()
            showChart(chart)
        })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("chart-radar.png"),
                "Radar Chart",
                "The radar chart rendering class") {
            val chart = EmployeeChart()
            showChart(chart)
        })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("chart-scatter.png"),
                "Scatter Chart",
                "The scatter chart rendering class") {
            val chart = ScatterChart()
            showChart(chart)
        })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("chart-time.png"),
                "Time Chart",
                "The Time chart rendering class") {
            val chart = SensorValuesChart()
            showChart(chart)
        })

        return demoContainer
    }

    private fun showChart(demo: AbstractDemoChart) {
        val chartForm = Form(demo.chartTitle, BorderLayout())
        val toolbar = chartForm.toolbar
        toolbar.uiid = "DemoToolbar"
        toolbar.titleComponent.uiid = "ComponentDemoTitle"
        val lastForm = CN.getCurrentForm()
        val backCommand = Command.create("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, lastForm.uiManager.getComponentStyle("DemoTitleCommand"))
        ) { lastForm.showBack() }
        toolbar.setBackCommand(backCommand)
        chartForm.add(BorderLayout.CENTER, demo.execute())
        chartForm.contentPane.uiid = "ComponentDemoContainer"
        chartForm.show()
    }

    init {
        init("Charts", Resources.getGlobalResources().getImage("charts-demo-icon.png"), parentForm,
                "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/ChartsDemo.java")
    }
}