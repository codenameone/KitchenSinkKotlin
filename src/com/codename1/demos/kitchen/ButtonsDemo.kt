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

import com.codename1.components.*
import com.codename1.io.FileSystemStorage
import com.codename1.io.Log
import com.codename1.ui.*
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.plaf.Style
import com.codename1.ui.plaf.UIManager
import com.codename1.ui.table.TableLayout
import com.codename1.ui.util.ImageIO
import com.codename1.ui.util.Resources

/**
 * Class that demonstrate a simple usage of the Button, SpanButton, MultiButton, ScaleImageButton
 * FloatingActionButton, and ShareButton components.
 * The buttons are components that allowing clickability.
 *
 * @author Sergey Gerashenko.
 */
class ButtonsDemo(parentForm: Form) : Demo() {
    private val badge: FloatingActionButton = FloatingActionButton.createBadge("0")

    override fun createContentPane(): Container? {
        val demoContainer = Container(BoxLayout(BoxLayout.Y_AXIS), "DemoContainer")
        demoContainer.isScrollableY = true

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("buttons.png"),
                "Buttons",
                "Button is the base class for several UI",
                "widgets allowing clickability. It has 3 States: rollover, pressed and the default state. Button can also " +
                        "have an Action Listener that react when the button is clicked or handle actions via a Command.Button UIID by " +
                        "default.") { showDemo("Buttons", createButtonsDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("span-button.png"),
                "Span Buttons",
                "A complex button similar to MultiButton",
                "that breaks lines automatically and looks like a regular button(more or less). Unlike the multi button the " +
                        "span buttons has the UIID style of a button.") { showDemo("Span Buttons", createSpanButtonsDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("multi-buttons.png"),
                "Multi Buttons",
                "A powerful button like component that",
                """
                    allows multiple rows/and an icon to be added every row/icon can have its own UIID.
                    
                    Internally the multi-button is a container with a lead component. Up to 4 rows are supported.
                    """.trimIndent()) { showDemo("Multi Buttons", createMultiButtonsDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("scale-image-label.png"),
                "Scale Image Button",
                "Button that simplifies the usage of scale to",
                """
                    fill/fit. This is effectively equivalent to just setting the style image on a button but more convenient for some special circumstances.
                    
                    One major difference is that preferred size equals the image in this case.
                    """.trimIndent()) { showDemo("Scale Image Button", createScaleImageButton()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("floating-action-button.png"),
                "Floating Action Button",
                "Floating action buttons are a material design",
                "element used to promote a special action in a form. They are represented as a floating circle with a " +
                        "flat icon floating above the UI typically in the bottom right area.") { showDemo("Floating Action Button", createFloatingActionButtonDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("share-button.png"),
                "Share Button",
                "The share button allows sharing a String",
                """
                    or an image either thru the defined sharing services or thru native OS sharing support. On Android & iOS the native sharing API is invoked for this class.
                    
                    The code below demonstrates image sharing, notice that an image must be stored using the FileSystemStorage API and shouldn't use a different API like Storage.
                    """.trimIndent()) { showDemo("Share Button", createShareButtonDemo()) })

        return demoContainer
    }

    private fun createButtonsDemo(): Container {
        val firstButton = Button("Button", "DemoButton")
        firstButton.addActionListener { ToastBar.showInfoMessage("First Button was pressed") }

        val secondButton = Button("", FontImage.MATERIAL_INFO, "DemoButton")
        secondButton.addActionListener { ToastBar.showInfoMessage("Second Button was pressed") }

        val thirdButton = Button("Button", FontImage.MATERIAL_INFO, "DemoButton")
        thirdButton.addActionListener { ToastBar.showInfoMessage("Third Button was pressed") }

        val demoContainer = BoxLayout.encloseY(Label("Button with text:", "DemoLabel"),
                firstButton,
                Label("Button with icon:", "DemoLabel"),
                secondButton,
                Label("Button with text and icon:", "DemoLabel"),
                thirdButton)
        demoContainer.uiid = "ButtonContainer"

        return BoxLayout.encloseY(demoContainer)
    }

    private fun createSpanButtonsDemo(): Container {
        val button = SpanButton("A complex button similar to MultiButton that breaks lines automatically and looks like " +
                "a regular button(more or less). Unlike the multi button the " +
                "span buttons has the UIID style of a button.", "WhiteText")
        button.uiid = "DemoButton"
        button.addActionListener { ToastBar.showInfoMessage("Button was pressed") }

        val spanLabelContainer = BoxLayout.encloseY(Label("SpanButton:", "DemoLabel"), button)
        spanLabelContainer.uiid = "ButtonContainer"

        return BoxLayout.encloseY(spanLabelContainer)
    }

    private fun createMultiButtonsDemo(): Container {
        val twoLinesNoIcon = MultiButton("MultiButton")
        twoLinesNoIcon.uiid = "DemoButton"
        twoLinesNoIcon.uiidLine1 = "DemoMultiButtonHeader"
        twoLinesNoIcon.textLine2 = "Line 2"
        twoLinesNoIcon.uiidLine2 = "DemoMultiButtonText"

        val emblem: Image = FontImage.createMaterial(FontImage.MATERIAL_ARROW_RIGHT, UIManager.getInstance().getComponentStyle("DemoMultiIcon"))
        val icon: Image = FontImage.createMaterial(FontImage.MATERIAL_INFO, UIManager.getInstance().getComponentStyle("DemoMultiIcon"))

        val oneLineIconEmblem = MultiButton("Icon + Emblem")
        oneLineIconEmblem.uiidLine1 = "DemoMultiButtonHeader"
        oneLineIconEmblem.icon = icon
        oneLineIconEmblem.emblem = emblem
        oneLineIconEmblem.iconUIID = "DemoMultiButtonIcon"
        oneLineIconEmblem.uiid = "DemoButton"

        val twoLinesIconEmblem = MultiButton("Icon + Emblem")
        twoLinesIconEmblem.uiidLine1 = "DemoMultiButtonHeader"
        twoLinesIconEmblem.uiid = "DemoButton"
        twoLinesIconEmblem.icon = icon
        twoLinesIconEmblem.iconUIID = "DemoMultiButtonIcon"
        twoLinesIconEmblem.emblem = emblem
        twoLinesIconEmblem.textLine2 = "Line 2"
        twoLinesIconEmblem.uiidLine2 = "DemoMultiButtonText"

        val twoLinesIconEmblemHorizontal = MultiButton("Icon + Emblem")
        twoLinesIconEmblemHorizontal.uiidLine1 = "DemoMultiButtonHeader"
        twoLinesIconEmblemHorizontal.uiid = "DemoButton"
        twoLinesIconEmblemHorizontal.icon = icon
        twoLinesIconEmblemHorizontal.iconUIID = "DemoMultiButtonIcon"
        twoLinesIconEmblemHorizontal.emblem = emblem
        twoLinesIconEmblemHorizontal.textLine2 = "Line 2"
        twoLinesIconEmblemHorizontal.uiidLine2 = "DemoMultiButtonText"
        twoLinesIconEmblemHorizontal.isHorizontalLayout = true

        val fourLinesIcon = MultiButton("With Icon")
        fourLinesIcon.uiid = "DemoButton"
        fourLinesIcon.icon = icon
        fourLinesIcon.iconUIID = "DemoMultiButtonIcon"
        fourLinesIcon.uiidLine1 = "DemoMultiButtonHeader"
        fourLinesIcon.textLine2 = "Line 2"
        fourLinesIcon.uiidLine2 = "DemoMultiButtonText"
        fourLinesIcon.textLine3 = "Line 3"
        fourLinesIcon.uiidLine3 = "DemoMultiButtonText"
        fourLinesIcon.textLine4 = "Line 4"
        fourLinesIcon.uiidLine4 = "DemoMultiButtonText"

        val demoContainer = BoxLayout.encloseY(oneLineIconEmblem,
                twoLinesNoIcon,
                twoLinesIconEmblem,
                twoLinesIconEmblemHorizontal,
                fourLinesIcon)
        demoContainer.uiid = "ButtonContainer"

        return BoxLayout.encloseY(demoContainer)
    }

    private fun createShareButtonDemo(): Container {
        val textShare = ShareButton()
        val icon: Image = FontImage.createMaterial(FontImage.MATERIAL_SHARE, UIManager.getInstance().getComponentStyle("DemoButtonIcon"))
        textShare.icon = icon
        textShare.uiid = "DemoButton"
        textShare.textToShare = "Hello there"
        textShare.text = "share text"

        val imagePath = FileSystemStorage.getInstance().appHomePath + "icon.png"
        val imageToShare = Resources.getGlobalResources().getImage("code-name-one-icon.png")

        try {
            val os = FileSystemStorage.getInstance().openOutputStream(imagePath)
            ImageIO.getImageIO().save(imageToShare, os, ImageIO.FORMAT_PNG, 1f)
            os.close()
        } catch (error: Exception) {
            Log.e(error)
        }

        val imageShare = ShareButton()
        imageShare.uiid = "DemoButton"
        imageShare.icon = icon
        imageShare.text = "share image"
        imageShare.setImageToShare(imagePath, "image/png")

        val shareButtonContainer = BoxLayout.encloseY(textShare, imageShare)
        shareButtonContainer.uiid = "ButtonContainer"

        return BoxLayout.encloseY(shareButtonContainer)
    }

    private fun createScaleImageButton(): Container {
        val tableLayout = TableLayout(2, 2)
        val buttonsContainer = Container(tableLayout)
        val icon: Image = FontImage.createMaterial(FontImage.MATERIAL_IMAGE, UIManager.getInstance().getComponentStyle("DemoScaleImageButton"))
        val fillLabel = ScaleImageLabel(icon)
        fillLabel.backgroundType = Style.BACKGROUND_IMAGE_SCALED_FILL
        fillLabel.uiid = "DemoScaleImageButton"

        val fillButton = ScaleImageButton(icon)
        fillButton.backgroundType = Style.BACKGROUND_IMAGE_SCALED_FILL
        fillButton.uiid = "DemoScaleImageButton"

        val button1 = ScaleImageButton(icon)
        button1.uiid = "DemoScaleImageButton"

        val button2 = ScaleImageButton(icon)
        button2.uiid = "DemoScaleImageButton"
        buttonsContainer.add(tableLayout.createConstraint().widthPercentage(20), button1).add(tableLayout.createConstraint().widthPercentage(80), Label("<- 20% of the screen", "ScaleComponentDemoLabel")).add(SpanLabel("80% of the screen->", "ScaleComponentDemoLabel")).add(button2).add(fillLabel).add(Label("<-image fill", "ScaleComponentDemoLabel")).add(SpanLabel("image fill->", "ScaleComponentDemoLabel")).add(fillButton)

        val demoContainer = BoxLayout.encloseY(buttonsContainer)
        demoContainer.uiid = "ButtonContainer"
        return BoxLayout.encloseY(demoContainer)
    }

    private fun createFloatingActionButtonDemo(): Container {
        val tasksContainer = Container(BoxLayout(BoxLayout.Y_AXIS))
        tasksContainer.isScrollableY = true

        val addNew = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD, "RedFabDemo")
        val greenButton = addNew.createSubFAB(FontImage.MATERIAL_ADD_TASK, "")
        greenButton.uiid = "GreenFabDemo"
        greenButton.addActionListener {
            val header = TextComponent().labelAndHint("note header: ")
            val body = TextComponent().labelAndHint("note body: ")
            val ok = Command("Ok")
            val cancel = Command("Cancel")
            if (Dialog.show("Enter Note", BoxLayout.encloseY(header, body), ok, cancel) === ok && header.text.isNotEmpty()) {
                tasksContainer.add(createNote(header.text, body.text, tasksContainer, true))
                val taskCount = Integer.valueOf(badge.text) + 1
                badge.text = taskCount.toString()
                tasksContainer.revalidate()
            }
        }

        val purpleButton = addNew.createSubFAB(FontImage.MATERIAL_ADD_TASK, "")
        purpleButton.uiid = "PurpleFabDemo"
        purpleButton.addActionListener {
            val header = TextComponent().labelAndHint("note header: ")
            val body = TextComponent().labelAndHint("note body: ")
            val ok = Command("Ok")
            val cancel = Command("Cancel")
            if (Dialog.show("Enter Note", BoxLayout.encloseY(header, body), ok, cancel) === ok && header.text.isNotEmpty()) {
                tasksContainer.add(createNote(header.text, body.text, tasksContainer, false))
                val taskCount = Integer.valueOf(badge.text) + 1
                badge.text = taskCount.toString()
                tasksContainer.revalidate()
            }
        }

        val header = Label("My Tasks", "DemoHeaderLabel")
        tasksContainer.add(header)
        tasksContainer.uiid = "Wrapper"

        val demoContainer = badge.bindFabToContainer(tasksContainer, Component.RIGHT, Component.TOP)
        return addNew.bindFabToContainer(BorderLayout.center(demoContainer))
    }

    private fun createNote(header: String, body: String, notes: Container, isGreen: Boolean): Component {
        val deleteButton: Button
        val emptyLabel = Label(" ", "EmptyGreenLabel")
        val noteHeaderLabel = SpanLabel(header, "NoteHeaderLabel")
        val noteBodyLabel = SpanLabel(body, "NoteBodyLabel")
        val noteContainer = BoxLayout.encloseY(emptyLabel, noteHeaderLabel, noteBodyLabel)

        if (isGreen) {
            deleteButton = Button("", FontImage.createMaterial(FontImage.MATERIAL_DELETE, UIManager.getInstance().getComponentStyle("DeleteButton")), "DeleteButton")
            noteContainer.uiid = "NoteGreenContainer"
        } else {
            deleteButton = Button("", FontImage.createMaterial(FontImage.MATERIAL_DELETE, UIManager.getInstance().getComponentStyle("PurpleDeleteButton")), "DeleteButton")
            noteContainer.uiid = "NotePurpleContainer"
            emptyLabel.uiid = "EmptyPurpleLabel"
        }

        val note = SwipeableContainer(deleteButton, noteContainer)
        deleteButton.addActionListener {
            notes.removeComponent(note)
            val taskCount = Integer.valueOf(badge.text) - 1
            badge.text = taskCount.toString()
            notes.revalidate()
        }
        return note
    }

    init {
        init("Buttons", Resources.getGlobalResources().getImage("demo-buttons.png"), parentForm,
                "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/ButtonsDemo.java")
    }
}