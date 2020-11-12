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
import com.codename1.charts.models.*
import com.codename1.charts.renderers.DefaultRenderer
import com.codename1.charts.renderers.SimpleSeriesRenderer
import com.codename1.charts.renderers.XYMultipleSeriesRenderer
import com.codename1.charts.renderers.XYSeriesRenderer
import com.codename1.charts.views.AbstractChart
import com.codename1.charts.views.PointStyle
import com.codename1.ui.CN
import com.codename1.ui.Display
import com.codename1.ui.Font
import com.codename1.ui.plaf.UIManager
import java.util.*

/**
 * An abstract class for the demo charts to extend. It contains some methods for
 * building datasets and renderers.
 */
abstract class AbstractDemoChart : IDemoChart {
    var isDrawOnMutableImage = false
    var smallFont = Font.createTrueTypeFont("native:MainRegular", "native:MainRegular").derive(CN.convertToPixels(0.5f).toFloat(), Font.STYLE_PLAIN)
    var medFont = Font.createTrueTypeFont("native:MainRegular", "native:MainRegular").derive(CN.convertToPixels(1f).toFloat(), Font.STYLE_PLAIN)
    var largeFont = Font.createTrueTypeFont("native:MainRegular", "native:MainRegular").derive(CN.convertToPixels(1.5f).toFloat(), Font.STYLE_PLAIN)

    protected fun createTemperatureDataset(): XYMultipleSeriesDataset {
        val titles = arrayOf("Crete", "Corfu", "Thassos", "Skiathos")
        val x: MutableList<DoubleArray> = ArrayList()
        for (i in titles.indices) {
            x.add(doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0))
        }
        val values: MutableList<DoubleArray> = ArrayList()
        values.add(doubleArrayOf(12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2,
                13.9))
        values.add(doubleArrayOf(10.0, 10.0, 12.0, 15.0, 20.0, 24.0, 26.0, 26.0, 23.0, 18.0, 14.0, 11.0))
        values.add(doubleArrayOf(5.0, 5.3, 8.0, 12.0, 17.0, 22.0, 24.2, 24.0, 19.0, 15.0, 9.0, 6.0))
        values.add(doubleArrayOf(9.0, 10.0, 11.0, 15.0, 19.0, 23.0, 26.0, 25.0, 22.0, 18.0, 13.0, 10.0))
        return buildDataset(titles, x, values)
    }

    /**
     * Builds an XY multiple dataset using the provided values.
     *
     * @param titles the series titles
     * @param xValues the values for the X axis
     * @param yValues the values for the Y axis
     * @return the XY multiple dataset
     */
    protected fun buildDataset(titles: Array<String>, xValues: List<DoubleArray>,
                               yValues: List<DoubleArray>): XYMultipleSeriesDataset {
        val dataset = XYMultipleSeriesDataset()
        addXYSeries(dataset, titles, xValues, yValues, 0)
        return dataset
    }

    fun addXYSeries(dataset: XYMultipleSeriesDataset, titles: Array<String>, xValues: List<DoubleArray>,
                    yValues: List<DoubleArray>, scale: Int) {
        val length = titles.size
        for (i in 0 until length) {
            val series = XYSeries(titles[i], scale)
            val xV = xValues[i]
            val yV = yValues[i]
            val seriesLength = xV.size
            for (k in 0 until seriesLength) {
                series.add(xV[k], yV[k])
            }
            dataset.addSeries(series)
        }
    }

    /**
     * Builds an XY multiple series renderer.
     *
     * @param colors the series rendering colors
     * @param styles the series point styles
     * @return the XY multiple series renderers
     */
    protected fun buildRenderer(colors: IntArray, styles: Array<PointStyle?>): XYMultipleSeriesRenderer {
        val renderer = XYMultipleSeriesRenderer()
        setRenderer(renderer, colors, styles)
        return renderer
    }

    protected fun setRenderer(renderer: XYMultipleSeriesRenderer, colors: IntArray, styles: Array<PointStyle?>) {
        renderer.axisTitleTextSize = smallFont.height / 2.toFloat()
        renderer.chartTitleTextSize = smallFont.height.toFloat()
        renderer.labelsTextSize = smallFont.height / 2.toFloat()
        renderer.legendTextSize = smallFont.height / 2.toFloat()
        renderer.pointSize = 5f
        renderer.margins = intArrayOf(medFont.height, medFont.height, 15, medFont.height)
        val length = colors.size
        for (i in 0 until length) {
            val r = XYSeriesRenderer()
            r.color = colors[i]
            r.pointStyle = styles[i]
            renderer.addSeriesRenderer(r)
        }
    }

    /**
     * Sets a few of the series renderer settings.
     *
     * @param renderer the renderer to set the properties to
     * @param title the chart title
     * @param xTitle the title for the X axis
     * @param yTitle the title for the Y axis
     * @param xMin the minimum value on the X axis
     * @param xMax the maximum value on the X axis
     * @param yMin the minimum value on the Y axis
     * @param yMax the maximum value on the Y axis
     * @param axesColor the axes color
     * @param labelsColor the labels color
     */
    protected fun setChartSettings(renderer: XYMultipleSeriesRenderer, title: String?, xTitle: String?,
                                   yTitle: String?, xMin: Double, xMax: Double, yMin: Double, yMax: Double, axesColor: Int,
                                   labelsColor: Int) {
        renderer.chartTitle = title
        renderer.xTitle = xTitle
        renderer.yTitle = yTitle
        renderer.xAxisMin = xMin
        renderer.xAxisMax = xMax
        renderer.yAxisMin = yMin
        renderer.yAxisMax = yMax
        renderer.axesColor = axesColor
        renderer.labelsColor = labelsColor
    }

    /**
     * Builds an XY multiple time dataset using the provided values.
     *
     * @param titles the series titles
     * @param xValues the values for the X axis
     * @param yValues the values for the Y axis
     * @return the XY multiple time dataset
     */
    protected fun buildDateDataset(titles: Array<String?>, xValues: List<Array<Date?>>,
                                   yValues: List<DoubleArray>): XYMultipleSeriesDataset {
        val dataset = XYMultipleSeriesDataset()
        val length = titles.size
        for (i in 0 until length) {
            val series = TimeSeries(titles[i])
            val xV = xValues[i]
            val yV = yValues[i]
            val seriesLength = xV.size
            for (k in 0 until seriesLength) {
                series.add(xV[k], yV[k])
            }
            dataset.addSeries(series)
        }
        return dataset
    }

    /**
     * Builds a category series using the provided values.
     *
     * @param values the values
     * @return the category series
     */
    protected fun buildCategoryDataset(title: String?, values: DoubleArray): CategorySeries {
        val series = CategorySeries(title)
        var k = 0
        for (value in values) {
            series.add("Project " + ++k, value)
        }
        return series
    }

    /**
     * Builds a multiple category series using the provided values.
     *
     * @param titles the series titles
     * @param values the values
     * @return the category series
     */
    protected fun buildMultipleCategoryDataset(title: String?,
                                               titles: List<Array<String?>?>, values: List<DoubleArray?>): MultipleCategorySeries {
        val series = MultipleCategorySeries(title)
        var k = 0
        for (value in values) {
            series.add("2007  $k", titles[k], value)
            k++
        }
        return series
    }

    /**
     * Builds a category renderer to use the provided colors.
     *
     * @param colors the colors
     * @return the category renderer
     */
    protected fun buildCategoryRenderer(colors: IntArray): DefaultRenderer {
        val renderer = DefaultRenderer()
        renderer.labelsTextSize = smallFont.height / 2.toFloat()
        renderer.legendTextSize = smallFont.height / 2.toFloat()
        renderer.margins = intArrayOf(medFont.height, medFont.height, medFont.height, medFont.height)
        for (color in colors) {
            val r = SimpleSeriesRenderer()
            r.color = color
            renderer.addSeriesRenderer(r)
        }
        return renderer
    }

    /**
     * Builds a bar multiple series dataset using the provided values.
     *
     * @param titles the series titles
     * @param values the values
     * @return the XY multiple bar dataset
     */
    protected fun buildBarDataset(titles: Array<String?>, values: List<DoubleArray>): XYMultipleSeriesDataset {
        val dataset = XYMultipleSeriesDataset()
        val length = titles.size
        for (i in 0 until length) {
            val series = CategorySeries(titles[i])
            val v = values[i]
            val seriesLength = v.size
            for (k in 0 until seriesLength) {
                series.add(v[k])
            }
            dataset.addSeries(series.toXYSeries())
        }
        return dataset
    }

    /**
     * Builds a bar multiple series renderer to use the provided colors.
     *
     * @param colors the series renderers colors
     * @return the bar multiple series renderer
     */
    protected fun buildBarRenderer(colors: IntArray): XYMultipleSeriesRenderer {
        val renderer = XYMultipleSeriesRenderer()
        renderer.axisTitleTextSize = smallFont.height / 2.toFloat()
        renderer.setChartTitleTextFont(smallFont)
        renderer.labelsTextSize = smallFont.height / 2.toFloat()
        renderer.legendTextSize = smallFont.height / 2.toFloat()
        val length = colors.size
        for (i in 0 until length) {
            val r = XYSeriesRenderer()
            r.color = colors[i]
            renderer.addSeriesRenderer(r)
        }
        return renderer
    }

    protected fun initRenderer(renderer: DefaultRenderer) {
        val s = UIManager.getInstance().getComponentStyle("DemoChart")
        renderer.backgroundColor = s.bgColor
        renderer.isApplyBackgroundColor = true
        renderer.labelsColor = s.fgColor
        renderer.axesColor = s.fgColor
        if (renderer is XYMultipleSeriesRenderer) {
            renderer.marginsColor = s.bgColor
        }
        if (Font.isNativeFontSchemeSupported()) {
            val fnt = Font.createTrueTypeFont("native:MainLight", "native:MainLight").derive(Display.getInstance().convertToPixels(2.5f).toFloat(), Font.STYLE_PLAIN)
            renderer.textTypeface = fnt
            renderer.setChartTitleTextFont(fnt)
            renderer.setLabelsTextFont(fnt)
            renderer.setLegendTextFont(fnt)
            if (renderer is XYMultipleSeriesRenderer) {
                renderer.setAxisTitleTextFont(fnt)
            }
            if (renderer is XYMultipleSeriesRenderer) {
                val x = renderer
                x.xLabelsColor = s.fgColor
                x.setYLabelsColor(0, s.fgColor)
            }
        }
    }

    protected fun newChart(chart: AbstractChart?): ChartComponent {
        val c = ChartComponent(chart)
        c.isFocusable = true
        c.isZoomEnabled = true
        c.isPanEnabled = true
        return c
    }
}