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
package com.codename1.demos.kitchen.charts.models

import com.codename1.charts.models.XYSeries
import com.codename1.ui.Button
import com.codename1.ui.Container
import com.codename1.ui.Label
import com.codename1.ui.TextField
import com.codename1.ui.events.ActionEvent
import com.codename1.ui.events.DataChangedListener
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.table.Table
import com.codename1.ui.table.TableModel

/**
 * A tool to edit an XYSeries
 *
 * @author Shai Almog
 */
class XYSeriesEditor : Container(BoxLayout.y()) {
    private var xy: XYSeries? = null
    fun init(xy: XYSeries, titleChanged: (String?)-> Unit) {
        isScrollableY = true
        this.xy = xy
        val title = TextField(xy.title, "Series Title", 20, TextField.ANY)
        title.addActionListener { e: ActionEvent? ->
            xy.title = title.text
            titleChanged(title.text)
        }
        val series: Table = RemoveTable(object : TableModel {
            override fun getRowCount(): Int {
                return xy.itemCount
            }

            override fun getColumnCount(): Int {
                return 3
            }

            override fun getColumnName(i: Int): String {
                return SERIES_COLUMN_NAMES[i]
            }

            override fun isCellEditable(row: Int, column: Int): Boolean {
                return true
            }

            override fun getValueAt(row: Int, column: Int): Any? {
                return when (column) {
                    0 -> xy.getX(row)
                    1 -> xy.getY(row)
                    else -> null
                }
            }

            override fun setValueAt(row: Int, column: Int, o: Any) {
                when (column) {
                    0 -> {
                        val y = xy.getY(row)
                        xy.remove(row)
                        xy.add(row, o.toString().toDouble(), y)
                    }
                    1 -> {
                        val x = xy.getX(row)
                        xy.remove(row)
                        xy.add(row, x, o.toString().toDouble())
                    }
                }
            }

            override fun addDataChangeListener(d: DataChangedListener) {}
            override fun removeDataChangeListener(d: DataChangedListener) {}
        }) { row: Int, column: Int -> xy.remove(row) }
        series.isScrollableY = false
        series.isScrollableX = false
        val annotations: Table = RemoveTable(object : TableModel {
            override fun getRowCount(): Int {
                return xy.annotationCount
            }

            override fun getColumnCount(): Int {
                return ANNOTATION_COLUMN_NAMES.size
            }

            override fun getColumnName(i: Int): String {
                return ANNOTATION_COLUMN_NAMES[i]
            }

            override fun isCellEditable(row: Int, column: Int): Boolean {
                return true
            }

            override fun getValueAt(row: Int, column: Int): Any? {
                return when (column) {
                    0 -> xy.getAnnotationAt(row)
                    1 -> xy.getAnnotationX(row)
                    2 -> xy.getAnnotationY(row)
                    else -> null
                }
            }

            override fun setValueAt(row: Int, column: Int, o: Any) {
                val a = xy.getAnnotationAt(row)
                val x = xy.getAnnotationX(row)
                val y = xy.getAnnotationY(row)
                when (column) {
                    0 -> {
                        xy.removeAnnotation(row)
                        xy.addAnnotation(o.toString(), row, x, y)
                    }
                    1 -> {
                        xy.removeAnnotation(row)
                        xy.addAnnotation(a, row, o.toString().toDouble(), y)
                    }
                    2 -> {
                        xy.removeAnnotation(row)
                        xy.addAnnotation(a, row, x, o.toString().toDouble())
                    }
                }
            }

            override fun addDataChangeListener(d: DataChangedListener) {}
            override fun removeDataChangeListener(d: DataChangedListener) {}
        }) { row: Int, column: Int -> xy.removeAnnotation(row) }
        annotations.isScrollableY = false
        annotations.isScrollableX = false
        val addSeries = Button("+")
        val addAnnotation = Button("+")
        addSeries.addActionListener { e: ActionEvent? ->
            xy.add(0.0, 0.0)
            series.model = series.model
        }
        addAnnotation.addActionListener { e: ActionEvent? ->
            xy.add(0.0, 0.0)
            series.model = series.model
        }
        add("Series Title").add(title).add(BorderLayout.center(Label("Series")).add(BorderLayout.EAST, addSeries)).add(series).add(BorderLayout.center(Label("Annotations")).add(BorderLayout.EAST, addAnnotation)).add(annotations)
    }

    companion object {
        private val SERIES_COLUMN_NAMES = arrayOf("X", "Y", "Remove")
        private val ANNOTATION_COLUMN_NAMES = arrayOf("Annotation", "X", "Y", "Remove")
    }
}