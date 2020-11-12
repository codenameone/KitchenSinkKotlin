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

import com.codename1.components.ScaleImageLabel
import com.codename1.components.Switch
import com.codename1.components.ToastBar
import com.codename1.ui.*
import com.codename1.ui.events.ActionEvent
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.layouts.FlowLayout
import com.codename1.ui.util.Resources

/**
 * Class that demonstrate the usage of the Toolbar component.
 * Toolbar replaces the default title area with a powerful abstraction that allows functionality
 * ranging from side menus (hamburger) to title animations and any arbitrary component type.
 * Toolbar allows customizing the Form title with different commands on the title area, within the side menu or the overflow menu.
 *
 * @author Sergey Gerashenko.
 */
class ToolbarDemo(parentForm: Form) : Demo() {

    override fun createContentPane(): Container? {
        val toolBarForm = Form("Toolbar", BorderLayout())
        toolBarForm.contentPane.uiid = "ComponentDemoContainer"
        val tb = toolBarForm.toolbar
        tb.uiid = "DemoToolbar"
        tb.titleComponent.uiid = "DemoTitle"
        tb.addSearchCommand { callBack: ActionEvent ->
            val text = callBack.source as String
            // Update the UI depending on the text.
        }
        if (CN.isTablet()) {
            Toolbar.setPermanentSideMenu(true)
        }
        val lastForm = Display.getInstance().current
        val backButton = Button("Back", "DemoButton")
        backButton.addActionListener { e: ActionEvent? -> lastForm.showBack() }

        val homeButton = Button("Home", FontImage.MATERIAL_HOME, "ToolbarDemoButton")
        homeButton.addActionListener { e: ActionEvent? -> lastForm.showBack() }

        val settings = Button("Settings", FontImage.MATERIAL_SETTINGS, "ToolbarDemoButton")
        settings.addActionListener { e: ActionEvent? ->
            val contentPane = toolBarForm.contentPane
            val settingsLabel = FlowLayout.encloseCenter(Label("Settings", "DemoHeader"))
            val wifi = BorderLayout.west(Label("Wi-Fi", "DemoToolbarLabel")).add(BorderLayout.EAST, Switch())
            val mobileData = BorderLayout.west(Label("Mobile data", "DemoToolbarLabel")).add(BorderLayout.EAST, Switch())
            val airplaneMode = BorderLayout.west(Label("Airplane mode", "DemoToolbarLabel")).add(BorderLayout.EAST, Switch())

            contentPane.add(BorderLayout.NORTH, BoxLayout.encloseY(settingsLabel, wifi, mobileData, airplaneMode))

            if (!CN.isTablet()) {
                tb.closeSideMenu()
            }
            contentPane.revalidate()
        }

        val sourceCode = Button("Source Code", FontImage.MATERIAL_CODE, "ToolbarDemoButton")
        sourceCode.addActionListener { e: ActionEvent? -> CN.execute(this.sourceCode) }

        val logoutButton = Button("Logout", FontImage.MATERIAL_EXIT_TO_APP, "ToolbarDemoButton")
        logoutButton.addActionListener { e: ActionEvent? ->
            if (!CN.isTablet()) {
                tb.closeSideMenu()
            }
            ToastBar.showInfoMessage("You have successfully logged out")
        }

        if (!CN.isTablet()) {
            val cn1Icon = ScaleImageLabel(Resources.getGlobalResources().getImage("code-name-one-icon.png"))
            cn1Icon.uiid = "SideMenuIconDemo"
            val size = CN.convertToPixels(20f)
            cn1Icon.preferredH = size
            cn1Icon.preferredW = size
            val sideMenuHeader = BoxLayout.encloseY(cn1Icon, Label("Codename One", "SideMenuHeader"))
            tb.addComponentToSideMenu(sideMenuHeader)
        }

        tb.addComponentToSideMenu(homeButton)
        tb.addComponentToSideMenu(settings)
        tb.addComponentToSideMenu(sourceCode)
        tb.addComponentToSideMenu(logoutButton)
        toolBarForm.backCommand = object : Command("") {
            override fun actionPerformed(ev: ActionEvent) {
                parentForm!!.showBack()
            }
        }

        toolBarForm.add(BorderLayout.SOUTH, backButton)
        toolBarForm.show()
        return null
    }

    init {
        init("Toolbar", Resources.getGlobalResources().getImage("toolbar-demo.png"), parentForm,
                "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/ToolbarDemo.java")
    }
}