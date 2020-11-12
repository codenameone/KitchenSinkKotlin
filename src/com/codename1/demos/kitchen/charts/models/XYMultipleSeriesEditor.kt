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

import com.codename1.charts.models.XYMultipleSeriesDataset
import com.codename1.charts.models.XYSeries
import com.codename1.components.Accordion
import com.codename1.ui.Button
import com.codename1.ui.Container
import com.codename1.ui.FontImage
import com.codename1.ui.events.ActionEvent
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.plaf.UIManager

/**
 * Allows editing a multiple series set
 *
 * @author Shai Almog
 */
class XYMultipleSeriesEditor : Container(BorderLayout()) {
    private var xy: XYMultipleSeriesDataset? = null
    fun init(xy: XYMultipleSeriesDataset?) {
        val acc = Accordion()
        add(BorderLayout.CENTER, acc)
        this.xy = xy
        val s = UIManager.getInstance().getComponentStyle("Button")
        s.fgColor = 0xff0000
        val removeImage = FontImage.createMaterial(FontImage.MATERIAL_DELETE, s, 3.5f)
        val addImage = FontImage.createMaterial(FontImage.MATERIAL_ADD, s, 3.5f)
        for (xx in xy!!.series) {
            addSeries(xx, acc, removeImage)
        }
        val add = Button(addImage)
        add(BorderLayout.SOUTH, add)
        add.addActionListener { e: ActionEvent? ->
            val x = XYSeries("New Series")
            addSeries(x, acc, removeImage)
            acc.animateLayout(200)
        }
    }

    private fun addSeries(xx: XYSeries, acc: Accordion, removeImage: FontImage) {
        val edit = XYSeriesEditor()
        edit.init(xx) { title: String? -> acc.setHeader(title, edit) }
        edit.isScrollableY = false;
        edit.isScrollableX = false;
        val remove = Button(removeImage)
        remove.addActionListener { e: ActionEvent? ->
            xy!!.removeSeries(xx)
            acc.removeContent(edit)
            acc.animateLayout(200)
        }
        acc.addContent(xx.title, edit)
    }
}