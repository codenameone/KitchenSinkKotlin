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
import com.codename1.charts.models.CategorySeries
import com.codename1.charts.models.XYMultipleSeriesDataset
import com.codename1.charts.models.XYSeries
import com.codename1.charts.renderers.DefaultRenderer
import com.codename1.charts.renderers.SimpleSeriesRenderer
import com.codename1.charts.renderers.XYMultipleSeriesRenderer
import com.codename1.charts.renderers.XYSeriesRenderer
import com.codename1.charts.views.LineChart
import com.codename1.charts.views.PieChart
import com.codename1.charts.views.PointStyle
import com.codename1.l10n.L10NManager
import com.codename1.ui.*
import com.codename1.ui.events.ActionEvent
import com.codename1.ui.geom.Dimension
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.table.DefaultTableModel
import com.codename1.ui.table.Table
import com.codename1.ui.table.TableLayout
import com.codename1.ui.table.TableModel
import java.lang.Double.MAX_VALUE
import java.lang.Double.MIN_VALUE
import kotlin.math.max
import kotlin.math.min

object DemoCharts {
    fun createCategoriesContainer(): Container {
        val dataCells = createDefaultCategoryCells()
        val model: TableModel = object : DefaultTableModel(arrayOf("Category", "Sales"), dataCells, true) {
            override fun setValueAt(row: Int, column: Int, o: Any) {
                if (column == 1 && 0 <= row) {
                    if (o is String) {
                        super.setValueAt(row, column, L10NManager.getInstance().parseCurrency(o))
                    }
                } else {
                    super.setValueAt(row, column, o)
                }
            }
        }
        val dataTable = createTable(model)
        val series = CategorySeries("Sales")
        val chart = PieChart(series, createPieChartRenderer())
        val chartComponent: ChartComponent = object : ChartComponent(chart) {
            // Make an anonymous claas that override calcPreferredSize to fit exactly a half of the screen.
            override fun calcPreferredSize(): Dimension {
                val width = Display.getInstance().displayWidth
                val height = Display.getInstance().displayHeight
                return Dimension(width, height / 2)
            }
        }
        updatePieSeries(series, model)
        model.addDataChangeListener { rows: Int, columns: Int -> updatePieSeries(series, model) }
        return BorderLayout.north(chartComponent).add(BorderLayout.CENTER, dataTable)
    }

    fun createAnnualContainer(): Container {
        val dataCells = createDefaultAnnualCells()
        val model: TableModel = object : DefaultTableModel(arrayOf("Year", "Sales"), dataCells, true) {
            override fun setValueAt(row: Int, column: Int, o: Any) {
                if (column == 1 && 0 <= row) {
                    if (o is String) {
                        super.setValueAt(row, column, L10NManager.getInstance().parseCurrency(o))
                    }
                } else {
                    super.setValueAt(row, column, o)
                }
            }
        }
        val dataTable = createTable(model)
        val annualSeries = XYSeries("Sales")
        updateAnnualSeries(annualSeries, model)
        val series = XYMultipleSeriesDataset()
        series.addSeries(annualSeries)
        val renderer = createChartMultiRenderer(model)
        val chart = LineChart(series, renderer)
        val chartComponent: ChartComponent = object : ChartComponent(chart) {
            // Make an anonymous claas that override calcPreferredSize to fit exactly a half of the screen.
            override fun calcPreferredSize(): Dimension {
                val width = Display.getInstance().displayWidth
                val height = Display.getInstance().displayHeight
                return Dimension(width, height / 2)
            }
        }
        model.addDataChangeListener { row: Int, column: Int ->
            updateAnnualSeries(annualSeries, model)
            updateRendererMinMax(model, chart.renderer)
        }
        dataTable.isScrollableY = true
        return BorderLayout.north(chartComponent).add(BorderLayout.CENTER, dataTable)
    }

    private fun createPieChartRenderer(): DefaultRenderer {
        val renderer = DefaultRenderer()
        val colors = intArrayOf(0xff6096, 0xff6096, 0xff6096)
        for (color in colors) {
            val r = SimpleSeriesRenderer()
            r.color = color
            r.isHighlighted = true
            renderer.addSeriesRenderer(r)
        }
        renderer.labelsColor = 0x000000
        renderer.backgroundColor = 0xe91e63
        renderer.isApplyBackgroundColor = true
        renderer.isShowLegend = false
        renderer.labelsTextSize = CN.convertToPixels(3f).toFloat()
        return renderer
    }

    private fun createDefaultCategoryCells(): Array<Array<Any>> {
        return arrayOf(arrayOf("Products", 10000.0), arrayOf("Virtual", 20000.0), arrayOf("Services", 22000.0))
    }

    private fun createDefaultAnnualCells(): Array<Array<Any>> {
        return arrayOf(arrayOf(2015, 154143.0), arrayOf(2016, 148591.0), arrayOf(2018, 125123.0), arrayOf(2017, 179525.0), arrayOf(2019, 234130.0), arrayOf(2020, 68123.0))
    }

    private fun updatePieSeries(series: CategorySeries, dataModel: TableModel) {
        series.clear()
        for (i in 0 until dataModel.rowCount) {
            val category = dataModel.getValueAt(i, 0) as String
            val value = dataModel.getValueAt(i, 1) as Double
            series.add(category, value)
        }
    }

    private fun updateAnnualSeries(annualSeries: XYSeries, dataTable: TableModel) {
        annualSeries.clear()
        for (i in 0 until dataTable.rowCount) {
            annualSeries.add((dataTable.getValueAt(i, 0) as Int).toDouble(), (dataTable.getValueAt(i, 1) as Double))
        }
    }

    private fun createChartMultiRenderer(tm: TableModel): XYMultipleSeriesRenderer {
        val renderer = XYMultipleSeriesRenderer()
        val r = XYSeriesRenderer()
        r.color = 0x2ebbae
        r.lineWidth = 7f
        r.isFillPoints = true
        r.pointStyle = PointStyle.CIRCLE
        renderer.addSeriesRenderer(r)
        renderer.pointSize = 15f
        renderer.backgroundColor = 0x009688
        renderer.isApplyBackgroundColor = true
        renderer.setLabelsTextFont(Font.createTrueTypeFont("native:MainRegular", "native:MainRegular"))
        renderer.labelsTextSize = CN.convertToPixels(3f).toFloat()
        renderer.labelsColor = 0x000000
        renderer.setZoomEnabled(false, false)
        renderer.setShowGrid(true)
        renderer.xLabels = tm.rowCount
        renderer.yLabels = tm.rowCount
        renderer.marginsColor = 0x009688
        updateRendererMinMax(tm, renderer)
        return renderer
    }

    private fun updateRendererMinMax(tm: TableModel, renderer: XYMultipleSeriesRenderer) {
        var xMin: Double = MAX_VALUE
        var xMax: Double = MIN_VALUE
        var yMin: Double = MAX_VALUE
        var yMax: Double = MIN_VALUE
        for (i in 0 until tm.rowCount) {
            val currentX: Double = (tm.getValueAt(i, 0) as Int).toDouble()
            val currentY = tm.getValueAt(i, 1) as Double
            xMin = min(xMin, currentX)
            xMax = max(xMax, currentX)
            yMin = min(yMin, currentY)
            yMax = max(yMax, currentY)
        }
        renderer.xAxisMin = xMin
        renderer.xAxisMax = xMax
        renderer.yAxisMin = yMin
        renderer.yAxisMax = yMax
    }

    private fun createTable(tm: TableModel): Table {
        return object : Table(tm) {
            override fun createCell(value: Any, row: Int, column: Int, editable: Boolean): Component {
                val cell = super.createCell(value, row, column, editable)
                when {
                    row == -1 -> {
                        cell.uiid = "SalesTableHeader"
                    }
                    row % 2 != 0 -> {
                        cell.uiid = "SalesTableOddRow"
                    }
                    else -> {
                        cell.uiid = "SalesTableEvenRow"
                    }
                }
                if (column == 1 && 0 <= row) {
                    val tx = cell as TextField
                    tx.constraint = TextArea.DECIMAL
                    tx.text = L10NManager.getInstance().formatCurrency((value as Double))
                    tx.addActionListener { e: ActionEvent? ->
                        tm.setValueAt(row, column, tx.text)
                        tx.text = L10NManager.getInstance().formatCurrency((tm.getValueAt(row, column) as Double))
                    }
                } else if (tm.getValueAt(0, 0) is Int && column == 0 && 0 <= row) {
                    val tx = cell as TextField
                    tx.constraint = TextArea.DECIMAL
                }
                return cell
            }

            override fun createCellConstraint(value: Any, row: Int, column: Int): TableLayout.Constraint {
                val constraint = super.createCellConstraint(value, row, column)
                constraint.widthPercentage = 50
                return constraint
            }
        }
    }
}