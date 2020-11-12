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

import com.codename1.components.MultiButton
import com.codename1.ui.*
import com.codename1.ui.events.ActionEvent
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.GridLayout
import com.codename1.ui.plaf.UIManager

open class MainWindow {
    private var demoImageWidth = CN.convertToPixels(15f)
    private var demoImageHeight = CN.convertToPixels(12f)

    fun buildForm(): Form {
        val mainWindow = Form("Components", GridLayout(7, 2, 7, 3))
        val contentPane = mainWindow.contentPane
        contentPane.uiid = "MainWindowContainer"
        contentPane.isScrollableY = true

        val tb = mainWindow.toolbar
        tb.uiid = "MainWindowToolbar"
        tb.titleComponent.uiid = "MainWindowTitle"

        val demos = arrayOf(
                LabelsDemo(mainWindow),
                ButtonsDemo(mainWindow),
                TogglesDemo(mainWindow),
                TextInputDemo(mainWindow),
                SelectionDemo(mainWindow),
                ContainersDemo(mainWindow),
                DialogDemo(mainWindow),
                ProgressDemo(mainWindow),
                ToolbarDemo(mainWindow),
                ChartsDemo(mainWindow),
                AdvancedDemo(mainWindow),
                MediaDemo(mainWindow),
                MapsDemo(mainWindow),
                ClockDemo(mainWindow)
        )

        if (CN.isTablet()) {
            mainWindow.layout = GridLayout(5, 3)
        }

        for (demo in demos) {
            val demoComponent = createDemoComponent(demo)
            mainWindow.add(demoComponent)
        }

        return mainWindow
    }

    private fun createDemoComponent(demo: Demo): Component {
        val demoComponent = MultiButton(demo.demoId)
        demoComponent.uiid = "MainWindowDemoComponent"
        demoComponent.icon = demo.demoImage!!.fill(demoImageWidth, demoImageHeight)
        demoComponent.iconPosition = BorderLayout.NORTH
        demoComponent.addActionListener { e: ActionEvent? -> createAndShowForm(demo) }
        demoComponent.iconUIID = "DemoComponentIcon"
        demoComponent.uiidLine1 = "MainWindowDemoName"
        return demoComponent
    }

    private fun createAndShowForm(demo: Demo) {
        val demoContent = demo.createContentPane() ?: return
        val demoForm = Form(demo.demoId, BorderLayout())
        val toolbar = demoForm.toolbar
        toolbar.uiid = "DemoToolbar"
        toolbar.titleComponent.uiid = "DemoTitle"

        // Toolbar add source and back buttons.
        val commandStyle = UIManager.getInstance().getComponentStyle("TitleCommand")
        val backCommand = Command.create("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, commandStyle)
        ) { e: ActionEvent? -> demo.parentForm?.showBack() }

        val sourceCommand = Command.create("", FontImage.create("{ }", UIManager.getInstance().getComponentStyle("TitleCommand"))
        ) { e: ActionEvent? -> CN.execute(demo.sourceCode) }

        toolbar.addCommandToRightBar(sourceCommand)
        toolbar.setBackCommand(backCommand)
        if (CN.isTablet()) {
            Demo.adjustToTablet(demoContent)
        }

        // Change the UIID of the source Button.
        toolbar.getComponentAt(1).uiid = "SourceCommand"
        demoForm.add(BorderLayout.CENTER, demoContent)
        demoForm.show()
    }
}