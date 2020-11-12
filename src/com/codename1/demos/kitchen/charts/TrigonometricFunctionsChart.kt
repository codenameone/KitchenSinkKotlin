/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.codename1.demos.kitchen.charts

import com.codename1.charts.ChartComponent
import com.codename1.charts.models.XYMultipleSeriesDataset
import com.codename1.charts.util.ColorUtil
import com.codename1.charts.views.LineChart
import com.codename1.charts.views.PointStyle
import com.codename1.demos.kitchen.charts.models.XYMultipleSeriesEditor
import com.codename1.ui.Component
import com.codename1.ui.Display
import com.codename1.ui.animations.Motion
import java.util.*

/**
 * Trigonometric functions demo chart.
 */
class TrigonometricFunctionsChart : AbstractDemoChart() {
    private var dataSet: XYMultipleSeriesDataset? = null
        private get() {
            if (field == null) {
                val titles = arrayOf("sin", "cos")
                val x: MutableList<DoubleArray> = ArrayList()
                val values: MutableList<DoubleArray> = ArrayList()
                val step = 4
                val count = 360 / step + 1
                x.add(DoubleArray(count))
                x.add(DoubleArray(count))
                val sinValues = DoubleArray(count)
                val cosValues = DoubleArray(count)
                values.add(sinValues)
                values.add(cosValues)
                for (i in 0 until count) {
                    val angle = i * step
                    x[0][i] = angle.toDouble()
                    x[1][i] = angle.toDouble()
                    val rAngle = Math.toRadians(angle.toDouble())
                    sinValues[i] = Math.sin(rAngle)
                    cosValues[i] = Math.cos(rAngle)
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
        get() = "Trigonometric functions"

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    override val desc: String
        get() = "The graphical representations of the sin and cos functions (line chart)"
    override val chartModelEditor: Component?
        get() {
            val x = XYMultipleSeriesEditor()
            x.init(dataSet)
            return x
        }
    override val chartTitle: String
        get() = "Trigonometric function"

    override fun execute(): Component {
        val colors = intArrayOf(ColorUtil.BLUE, ColorUtil.CYAN)
        val styles = arrayOf<PointStyle?>(PointStyle.POINT, PointStyle.POINT)
        val renderer = buildRenderer(colors, styles)
        setChartSettings(renderer, "Trigonometric functions", "X (in degrees)", "Y", 0.0, 360.0, -1.0, 1.0,
                ColorUtil.GRAY, ColorUtil.LTGRAY)
        val strWidth = Display.getInstance().convertToPixels(10f)
        val numXLabels = Display.getInstance().displayWidth / (strWidth + 20)
        renderer.xLabels = numXLabels
        renderer.yLabels = 10
        renderer.xAxisMin = 0.0
        renderer.xAxisMax = 50.0
        initRenderer(renderer)
        val m = Motion.createLinearMotion(0, 310, 10000)
        val chart = LineChart(dataSet, renderer)
        val cmp: ChartComponent = object : ChartComponent(chart) {
            override fun initComponent() {
                super.initComponent()
                componentForm.registerAnimated(this)
            }

            override fun animate(): Boolean {
                val b = super.animate()
                return if (m.isFinished) {
                    b
                } else {
                    renderer.xAxisMin = m.value.toDouble()
                    renderer.xAxisMax = m.value + 50.toDouble()
                    true
                }
            }
        }
        m.start()
        return cmp
    }
}