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
import com.codename1.ui.*
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.layouts.FlowLayout
import com.codename1.ui.list.DefaultListModel
import com.codename1.ui.plaf.UIManager
import com.codename1.ui.util.Resources

/**
 * Class that demonstrate the usage of the CheckBox, RadioButton, and Switch components.
 * The Toggle buttons are basic components that have 2 states: selected and unselected.
 * They allow the user to see it state and change it.
 *
 * @author Sergey Gerashenko.
 */
class TogglesDemo(parentForm: Form) : Demo() {
    override fun createContentPane(): Container? {
        val demoContainer = Container(BoxLayout(BoxLayout.Y_AXIS), "DemoContainer")
        demoContainer.isScrollableY = true

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("check-box.png"),
                "Checkbox",
                "Checkbox is a button that can be selected",
                "or deselected and display its state to the user. Check out RadioButton for a more exclusive selection " +
                        "approach. Both components support a toggle button mode using the Button.setToggle (Boolean) API.") { showDemo("Checkbox", createCheckboxDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("radio-button.png"),
                "Radio Button",
                "Checkbox is a button that can be selected",
                "or deselected and display its state to the user. Check out RadioButton for a more exclusive selection " +
                        "approach. Both components support a toggle button mode using the Button.setToggle (Boolean) API.") { showDemo("Radio Button", createRadioButtonDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("switch.png"),
                "Switch",
                "Button is the base class for several UI",
                "The on/off switch is a checkbox of sort (although it derives container) that represents its state as a switch " +
                        "when using the android native theme this implementation follows the Material Design Switch " +
                        "guidelines: https://material.io/guidelines/components/ selection-controls.html#selection-controls- radio-button"
                ) { showDemo("Switch", createSwitchDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("check-box-list.png"),
                "Check Box List",
                "A list of Check Boxes") { showDemo("CheckBox List", createCheckBoxListDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("radio-button-list.png"),
                "RadioButton List (BoxLayout Y)",
                "A list of Radio Buttons.") { showDemo("RadioButton List (BoxLayout Y)", createRadioButtonListDemo()) })

        return demoContainer
    }

    private fun createCheckboxDemo(): Container {
        val cb1 = CheckBox.createToggle("Tomato")
        cb1.uiid = "DemoCheckBox"
        val cb2 = CheckBox.createToggle("Salad")
        cb2.uiid = "DemoCheckBox"
        val cb3 = CheckBox.createToggle("Onion")
        cb3.uiid = "DemoCheckBox"
        val cb4 = CheckBox.createToggle("Pickled Cucumber")
        cb4.uiid = "DemoCheckBox"
        val cb5 = CheckBox.createToggle("Mushrooms")
        cb5.uiid = "DemoCheckBox"
        val cb6 = CheckBox.createToggle("Cheese")
        cb6.uiid = "DemoCheckBox"
        val cb7 = CheckBox.createToggle("Egg")
        cb7.uiid = "DemoCheckBox"

        cb1.isSelected = true
        cb2.isSelected = true
        cb3.isSelected = true
        cb4.isSelected = true

        val checkBoxContainer = BoxLayout.encloseY(cb1, cb2, cb3, cb4, cb5, cb6, cb7)
        val completeOrder = Button("Complete Order", "DemoButton")
        completeOrder.addActionListener { ToastBar.showInfoMessage("Your order is on the way") }
        val completeOrderContainer = FlowLayout.encloseCenter(completeOrder)
        completeOrderContainer.uiid = "CompleteOrderContainer"
        val demoContainer = BorderLayout.center(checkBoxContainer).add(BorderLayout.SOUTH, completeOrderContainer).add(BorderLayout.NORTH, Label("Burger Ingredients", "BurgerIngredients"))
        demoContainer.uiid = "Wrapper"

        return BoxLayout.encloseY(demoContainer)
    }

    private fun createRadioButtonDemo(): Container {
        val bg = ButtonGroup()
        val rb1 = RadioButton.createToggle("Android", bg)
        val rb2 = RadioButton.createToggle("IOS", bg)
        val rb3 = RadioButton.createToggle("UWP", bg)
        val rb4 = RadioButton.createToggle("Mac Os Desktop", bg)
        val rb5 = RadioButton.createToggle("Windows Desktop", bg)
        val rb6 = RadioButton.createToggle("Javascript", bg)

        rb1.uiid = "DemoRadioButton"
        rb2.uiid = "DemoRadioButton"
        rb3.uiid = "DemoRadioButton"
        rb4.uiid = "DemoRadioButton"
        rb5.uiid = "DemoRadioButton"
        rb6.uiid = "DemoRadioButton"

        rb1.isSelected = true
        val radioButtonsContainer = BoxLayout.encloseY(Label("select build", "SelectBuild"), rb1, rb2, rb3, rb4, rb5, rb6)
        val demoContainer = BorderLayout.center(radioButtonsContainer)
        val applyButton = Button("Send Build", "DemoButton")
        applyButton.addActionListener {
            val selectedButton = bg.selected
            ToastBar.showInfoMessage(selectedButton.text + " build was sent")
        }

        bg.addActionListener {
            applyButton.text = "Send " + bg.selected.text + " Build"
            demoContainer.revalidate()
        }

        val applyContainer = FlowLayout.encloseCenter(applyButton)
        applyContainer.uiid = "CompleteOrderContainer"
        demoContainer.add(BorderLayout.SOUTH, applyContainer)
        demoContainer.uiid = "Wrapper"
        return BoxLayout.encloseY(demoContainer)
    }

    private fun createSwitchDemo(): Container {
        val s = Switch()
        s.setOn()
        if (CN.isDarkMode() != null && !CN.isDarkMode()) {
            s.setOff()
        }
        val switchContainer = BorderLayout.centerAbsolute(s)

        s.addChangeListener {
            if (s.isOn) {
                switchContainer.uiid = "BrightContainer"
            } else {
                switchContainer.uiid = "DarkContainer"
            }
            switchContainer.revalidate()
        }

        return switchContainer
    }

    private fun createCheckBoxListDemo(): Container {
        val model: DefaultListModel<String> = DefaultListModel("Pasta", "Rice", "Bread", "Butter", "Milk", "Eggs", "Cheese", "Salt", "Pepper", "Honey")
        val list = CheckBoxList(model)
        list.isScrollableY = true
        list.layout = BoxLayout(BoxLayout.Y_AXIS)
        list.setShouldCalcPreferredSize(true)

        val add = Button("Add New", "AddNewButton")
        add.addActionListener {
            val newItem = TextComponent().label("New Item: ")
            val ok = Command("Ok")
            val cancel = Command("Cancel")
            if (Dialog.show("Enter Note", newItem, ok, cancel) === ok && newItem.text.isNotEmpty()) {
                model.addItem(newItem.text)
                list.revalidate()
            }
        }

        val icon: Image = FontImage.createMaterial(FontImage.MATERIAL_SHARE, UIManager.getInstance().getComponentStyle("DemoButtonIcon"))
        val share = ShareButton()
        share.icon = icon
        share.text = "Share Groceries"
        share.uiid = "DemoButton"
        share.addActionListener {
            val sb = StringBuilder()
            val selected = model.selectedIndices
            for (i in selected) {
                sb.append(model.getItemAt(i))
                sb.append(", ")
            }
            if (selected.isNotEmpty()) {
                sb.delete(sb.length - 2, sb.length - 1)
            }
            share.textToShare = sb.toString()
            val groceriesSize = model.size
            for (i in 0 until groceriesSize) {
                val currItem = list.getComponentAt(i) as CheckBox
                if (currItem.isSelected) {
                    currItem.isSelected = false
                }
            }
        }

        val buttonsContainer = FlowLayout.encloseCenter(share, add)
        buttonsContainer.uiid = "CompleteOrderContainer"
        val checkBoxContainer = BorderLayout.center(list).add(BorderLayout.NORTH, Label("Select groceries to share", "SelectGroceriesLabel")).add(BorderLayout.SOUTH, buttonsContainer)
        checkBoxContainer.uiid = "Wrapper"
        return BoxLayout.encloseY(checkBoxContainer)
    }

    private fun createRadioButtonListDemo(): Container {
        val question = SpanLabel("Who is the first character in the series to be called \"King in the North\"?", "DemoLabel")
        val answer = Button("Answer", "DemoAnswerButton")
        val model: DefaultListModel<*> = DefaultListModel<Any?>("Jon Snow", "Robb Stark", "Ned Stark", "Edmure Tully")
        val list = RadioButtonList(model)
        list.layout = BoxLayout.y()
        answer.addActionListener {
            if (model.selectedIndex == 1) {
                ToastBar.showInfoMessage("Correct!")
            } else {
                ToastBar.showInfoMessage("Incorrect!!")
            }
        }

        val demoContainer = BorderLayout.center(list).add(BorderLayout.NORTH, question).add(BorderLayout.SOUTH, answer)
        demoContainer.uiid = "Wrapper"

        return BoxLayout.encloseY(demoContainer)
    }

    init {
        init("Toggles", Resources.getGlobalResources().getImage("toggles-demo.png"), parentForm,
                "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/TogglesDemo.java")
    }
}