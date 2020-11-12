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

import com.codename1.components.ScaleImageButton
import com.codename1.components.SpanButton
import com.codename1.ui.*
import com.codename1.ui.events.ActionEvent
import com.codename1.ui.geom.Dimension
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.plaf.Style
import com.codename1.ui.plaf.UIManager

/**
 * This is the base class for all the demos.
 */
abstract class Demo {
    var demoId: String? = null
        private set
    var demoImage: Image? = null
        private set
    var parentForm: Form? = null
        private set
    var sourceCode: String? = null
        private set

    protected fun init(id: String, demoImage: Image, parentForm: Form, sourceCode: String) {
        demoId = id
        this.demoImage = demoImage
        this.parentForm = parentForm
        this.sourceCode = sourceCode
    }

    /**
     * Build the content of the demo that derives this class.
     *
     * @return container that contain the demo content.
     */
    abstract fun createContentPane(): Container?

    protected fun showDemo(title: String?, content: Component) {
        val demoForm = Form(title, BorderLayout())
        content.uiid = "ComponentDemoContainer"
        val toolbar = demoForm.toolbar
        toolbar.uiid = "DemoToolbar"
        toolbar.titleComponent.uiid = "ComponentDemoTitle"

        val lastForm = CN.getCurrentForm()
        val backCommand = Command.create("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, UIManager.getInstance().getComponentStyle("DemoTitleCommand"))
        ) { lastForm.showBack() }

        toolbar.setBackCommand(backCommand)
        demoForm.add(BorderLayout.CENTER, content)
        demoForm.isFormBottomPaddingEditingMode = true
        demoForm.show()
    }

    fun createComponent(image: Image, header: String, firstLine: String, body: String, listener: (ActionEvent?)-> Unit): Component {
        return AccordionComponent(image, header, firstLine, body, listener)
    }

    fun createComponent(image: Image?, header: String?, firstLine: String?, listener: (ActionEvent?)-> Unit): Component {
        val contentImage: ScaleImageButton = object : ScaleImageButton(image) {
            override fun calcPreferredSize(): Dimension {
                val preferredSize = super.calcPreferredSize()
                preferredSize.height = Display.getInstance().displayHeight / 10
                return preferredSize
            }
        }
        contentImage.backgroundType = Style.BACKGROUND_IMAGE_SCALED
        contentImage.addActionListener(listener)
        contentImage.uiid = "DemoContentImage"

        val contentHeader: Label = Button(header, "DemoContentHeader")
        val contentFirstLine: Label = Button(firstLine, "DemoContentBody")
        val demoContent = BoxLayout.encloseY(contentImage, contentHeader, contentFirstLine)
        contentImage.width = demoContent.width - demoContent.allStyles.getPadding(Component.LEFT) - demoContent.allStyles.getPadding(Component.RIGHT) - contentImage.allStyles.getPadding(Component.LEFT) - contentImage.allStyles.getPadding(Component.RIGHT)
        demoContent.leadComponent = contentImage
        demoContent.uiid = "DemoContentRegular"
        return demoContent
    }

    /**
     * Demo component that have more then one line of description.
     *
     * @param image the image of the component.
     * @param header the header of the component.
     * @param firstLine first line of description.
     * @param body the rest of the description.
     * @param listener add ActionListener to the image of the component.
     */
    private inner class AccordionComponent(image: Image, header: String, firstLine: String, body: String, listener: (ActionEvent?)-> Unit) : Container(BorderLayout()) {
        private var isOpen = false
        private val firstLine: Button = Button("$firstLine $body", "DemoContentBody")
        private val body: SpanButton = SpanButton("$firstLine $body", "DemoContentBody")
        private val openedIcon: Image
        private val closedIcon: Image
        private val openClose: Button
        private val contentContainer: Container

        fun open() {
            // Select all AccordionComponent objects to close them when we open another one.
            val accordionList = ComponentSelector.select("DemoContentAccordion").asList()
            for (currComponent in accordionList) {
                (currComponent as AccordionComponent).close(false)
            }
            if (!isOpen) {
                isOpen = true
                openClose.icon = openedIcon
                body.isHidden = false
                firstLine.isHidden = true
                parent.animateLayout(500)
            }
        }

        fun close(shouldAnimate: Boolean) {
            if (isOpen) {
                isOpen = false
                openClose.icon = closedIcon
                body.isHidden = true
                firstLine.isHidden = false
                if (shouldAnimate) {
                    parent.animateLayout(500)
                }
            }
        }

        init {
            this.body.uiid = "DemoContentBodyButton"
            this.firstLine.addActionListener(listener)

            this.body.addActionListener(listener)
            contentContainer = Container(BoxLayout(BoxLayout.Y_AXIS))
            uiid = "DemoContentAccordion"

            val contentImage: ScaleImageButton = object : ScaleImageButton(image) {
                override fun calcPreferredSize(): Dimension {
                    val preferredSize = super.calcPreferredSize()
                    preferredSize.height = Display.getInstance().displayHeight / 10
                    return preferredSize
                }
            }

            contentImage.backgroundType = Style.BACKGROUND_IMAGE_SCALED
            contentImage.addActionListener(listener)
            contentImage.uiid = "DemoContentImage"

            val contentHeader = Button(header, "DemoContentHeader")
            contentHeader.addActionListener(listener)

            val buttonStyle = UIManager.getInstance().getComponentStyle("AccordionButton")
            openedIcon = FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_UP, buttonStyle)
            closedIcon = FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, buttonStyle)
            openClose = Button("", closedIcon, "AccordionButton")
            openClose.addActionListener {
                if (isOpen) {
                    close(true)
                } else {
                    open()
                }
            }

            contentContainer.addAll(contentHeader, this.firstLine, this.body)
            this.body.isHidden = true

            add(BorderLayout.NORTH, contentImage)
            add(BorderLayout.CENTER, contentContainer)
            add(BorderLayout.EAST, openClose)
        }
    }

    companion object{
        fun adjustToTablet(cnt: Container) {
            // Create anonymous class and override the calcPreferredSize() function to fit exactly half of the scree.
            val leftSide: Container = object : Container(BoxLayout(BoxLayout.Y_AXIS)) {
                override fun calcPreferredSize(): Dimension {
                    val dim = super.calcPreferredSize()
                    dim.width = Display.getInstance().displayWidth / 2
                    return dim
                }
            }

            val rightSide: Container = object : Container(BoxLayout(BoxLayout.Y_AXIS)) {
                override fun calcPreferredSize(): Dimension {
                    val dim = super.calcPreferredSize()
                    dim.width = Display.getInstance().displayWidth / 2
                    return dim
                }
            }

            for ((i, currComponent) in cnt.getChildrenAsList(true).withIndex()) {
                cnt.removeComponent(currComponent)
                if (i % 2 == 0) {
                    leftSide.add(currComponent)
                } else {
                    rightSide.add(currComponent)
                }
            }

            cnt.layout = BoxLayout(BoxLayout.X_AXIS)
            cnt.addAll(leftSide, rightSide)
        }
    }
}