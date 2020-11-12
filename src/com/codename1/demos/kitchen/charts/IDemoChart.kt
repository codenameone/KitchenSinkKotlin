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

import com.codename1.ui.Component

/**
 * Defines the demo charts.
 */
interface IDemoChart {
    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    val name: String

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    val desc: String

    /**
     * Executes the chart demo.
     *
     * @return the built UI
     */
    fun execute(): Component
    val chartTitle: String
    val chartModelEditor: Component?

    companion object {
        /** A constant for the name field in a list activity.  */
        const val NAME = "name"

        /** A constant for the description field in a list activity.  */
        const val DESC = "desc"
    }
}