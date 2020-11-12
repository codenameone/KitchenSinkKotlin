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

import com.codename1.ui.Button
import com.codename1.ui.Component
import com.codename1.ui.FontImage
import com.codename1.ui.events.ActionEvent
import com.codename1.ui.plaf.UIManager
import com.codename1.ui.table.Table
import com.codename1.ui.table.TableModel

/**
 * Special table whose last column is a "remove" button
 *
 * @author Shai Almog
 */
class RemoveTable(m: TableModel?, val onRemove: (Int, Int) -> Unit) : Table(m) {
    private var removeImage: FontImage? = null
        private get() {
            if (field == null) {
                val s = UIManager.getInstance().getComponentStyle("TableCell")
                s.fgColor = 0xff0000
                field = FontImage.createMaterial(FontImage.MATERIAL_DELETE, s, 3.5f)
            }
            return field
        }

    override fun createCell(value: Any, row: Int, column: Int, editable: Boolean): Component {
        if (column == model.columnCount - 1) {
            val removeButton = Button(removeImage)
            removeButton.addActionListener { e: ActionEvent? ->
                onRemove(row, column)
                model = model
            }
            return removeButton
        }
        return super.createCell(value, row, column, editable)
    }
}