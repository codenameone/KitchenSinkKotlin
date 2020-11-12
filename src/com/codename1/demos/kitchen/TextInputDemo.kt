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

import com.codename1.components.ClearableTextField
import com.codename1.components.SpanLabel
import com.codename1.components.ToastBar
import com.codename1.io.CSVParser
import com.codename1.io.Log
import com.codename1.ui.*
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.layouts.GridLayout
import com.codename1.ui.list.DefaultListCellRenderer
import com.codename1.ui.list.DefaultListModel
import com.codename1.ui.table.TableLayout
import com.codename1.ui.util.Resources
import com.codename1.ui.validation.RegexConstraint
import com.codename1.ui.validation.Validator
import java.io.IOException
import java.util.*
import java.io.InputStream


/**
 * Class that demonstrate the usage of the TextField, TextArea, ClearableTextField, AutoCompleteTextField,
 * and TextComponent components.
 * The TextInput Components are basic components that allow to view the text on the form and get text input from the user.
 *
 * @author Sergey Gerashenko.
 */
class TextInputDemo(parentForm: Form) : Demo() {
    override fun createContentPane(): Container? {
        val demoContainer = Container(BoxLayout(BoxLayout.Y_AXIS), "DemoContainer")
        demoContainer.isScrollableY = true

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("text-field.png"),
                "Text Field",
                "A specialized version of TextArea with",
                "some minor deviations from the original specifically: Blinking cursor is rendered on TextField only. com.codename1.ui.events.DataChangeList is only available in TextField." +
                        "This is crucial for character by character input event tracking setDoneListener(com. codename1.ui. events.ActionLister) is only available in Text Field Different UIID's (TextField vs. TextArea)."
        ) { showDemo("Text Field", createTextFieldDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("text-area.png"),
                "Text Area",
                "An optionally multi-line editable region that",
                "can display text and allow a user to edit it. By default the text area will grow based on its content. TextArea is useful both for text input and for displaying multi-line data, it is used internally by components such as SpanLabel & SpanButton." +
                        "TextArea & TextField are very similar, we discuss the main differences between the two here. In fact they are so similar that our sample code below was written for TextField but should be interchangeable with TextArea."
        ) { showDemo("Text Area", createTextAreaDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("clearable-text-field.png"),
                "Clearable Text Field",
                "Wraps a text field so it will have an X to",
                "clear its content on the right hand side."
        ) { showDemo("Clearable Text Field", createClearableTextFieldDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("auto-complete-text-field.png"),
                "Auto Complete Text Field",
                "An editable TextField with completion",
                "suggestions that show up in a drop down menu while the user types in text. This class uses the \"TextField\" " +
                        "UIID by default as well as \"AutoCompletePopup\" & \"AutoCompleteList\" for the popup list details. The " +
                        "sample below shows the more trivial use case for this widget."
        ) { showDemo("Browser", createAutoCompleteDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("floating-hint.png"),
                "Text Component",
                "Text Component Encapsulates a text field",
                "and label into a single component. This allows the UI to adapt for IOS/Android behavior differences and support features like floating hint when necessary." +
                        "It also includes platform specific error handling logic. It is highly recommended to use text component in the context of a TextModeLayout this allows " +
                        "the layout to implicitly adapt to the on-top mode and use a box layout Y mode for iOS and other platforms. This class supports several theme constants."
        ) { showDemo("Text Component", createTextComponentContainer()) })

        return demoContainer
    }

    private fun createTextFieldDemo(): Container {
        val textFields = Container()
        val tl: TableLayout = if (Display.getInstance().isTablet) {
            TableLayout(7, 2)
        } else {
            TableLayout(14, 1)
        }

        val firstName = TextField("", "First Name", 20, TextArea.ANY)
        firstName.uiid = "DemoTextArea"
        val surname = TextField("", "Surname", 20, TextArea.ANY)
        surname.uiid = "DemoTextArea"
        val email = TextField("", "E-Mail", 20, TextArea.EMAILADDR)
        email.uiid = "DemoTextArea"
        val url = TextField("", "URL", 20, TextArea.URL)
        url.uiid = "DemoTextArea"
        val phone = TextField("", "Phone", 20, TextArea.PHONENUMBER)
        phone.uiid = "DemoTextArea"
        val num1 = TextField("", "1234", 4, TextArea.NUMERIC)
        num1.uiid = "DemoTextArea"
        val num2 = TextField("", "1234", 4, TextArea.NUMERIC)
        num2.uiid = "DemoTextArea"
        val num3 = TextField("", "1234", 4, TextArea.NUMERIC)
        num3.uiid = "DemoTextArea"
        val num4 = TextField("", "1234", 4, TextArea.NUMERIC)
        num4.uiid = "DemoTextArea"

        num1.addDataChangedListener { _: Int, _: Int ->
            if (num1.text.length == 4) {
                num1.stopEditing { num2.startEditing() }
            }
        }
        num2.addDataChangedListener { _: Int, _: Int ->
            if (num2.text.length == 4) {
                num2.stopEditing { num3.startEditing() }
            }
        }
        num3.addDataChangedListener { _: Int, _: Int ->
            if (num3.text.length == 4) {
                num3.stopEditing { num4.startEditing() }
            }
        }
        num4.addDataChangedListener { _: Int, _: Int ->
            if (num4.text.length == 4) {
                num4.stopEditing()
            }
        }

        val submit = Button("Submit", "TextFieldsDemoButton")
        submit.addActionListener { ToastBar.showInfoMessage("Your personal data was saved successfully") }
        tl.isGrowHorizontally = true
        textFields.layout = tl
        textFields.add(Label("First Name", "DemoLabel")).add(firstName).add(Label("Surname", "DemoLabel")).add(surname).add(Label("E-Mail", "DemoLabel")).add(email).add(Label("URL", "DemoLabel")).add(url).add(Label("Phone", "DemoLabel")).add(phone).add(Label("Credit Card", "DemoLabel")).add(GridLayout.encloseIn(4, num1, num2, num3, num4))

        val demoContainer = BorderLayout.center(textFields)
        demoContainer.add(BorderLayout.SOUTH, submit)
        demoContainer.uiid = "Wrapper"
        val cnt = BoxLayout.encloseY(demoContainer)
        cnt.isScrollableY = true
        return cnt
    }

    private fun createTextAreaDemo(): Container {
        val textFields = Container()
        val tl: TableLayout = if (Display.getInstance().isTablet) {
            TableLayout(7, 2)
        } else {
            TableLayout(14, 1)
        }

        val name = TextArea(1, 20, TextArea.ANY)
        name.uiid = "DemoTextArea"
        val email = TextArea(1, 20, TextArea.EMAILADDR)
        email.uiid = "DemoTextArea"
        val message = TextArea(7, 20, TextArea.ANY)
        message.uiid = "DemoTextArea"

        val contactUsButton = Button("Contact Us", "TextFieldsDemoButton")
        contactUsButton.addActionListener {
            name.text = ""
            email.text = ""
            message.text = ""
            ToastBar.showInfoMessage("Your Message has sent")
        }

        tl.isGrowHorizontally = true
        textFields.layout = tl
        textFields.add(Label("Your Name*", "DemoLabel")).add(name).add(Label("Your Email*", "DemoLabel")).add(email).add(SpanLabel("Your Message", "DemoLabel")).add(message)

        val demoContainer = BorderLayout.center(textFields)
        demoContainer.add(BorderLayout.SOUTH, contactUsButton)
        demoContainer.uiid = "Wrapper"
        return BoxLayout.encloseY(demoContainer)
    }

    private fun createClearableTextFieldDemo(): Container {
        val userName = TextField("", "User Name", 20, TextArea.ANY)
        userName.uiid = "DemoTextArea"

        val clearableUserName = ClearableTextField.wrap(userName)
        val password = TextField("", "Password", 20, TextArea.PASSWORD)
        password.uiid = "DemoTextArea"

        val clearablePassword = ClearableTextField.wrap(password)
        val textFieldsContainer = BoxLayout.encloseY(Label("Username:", "DemoLabel"),
                clearableUserName,
                Label("Password:", "DemoLabel"),
                clearablePassword)

        val loginButton = Button("Login", "TextFieldsDemoButton")
        loginButton.addActionListener { ToastBar.showInfoMessage("Username or Password are incorrect") }

        val demoContainer = BorderLayout.center(textFieldsContainer)
        demoContainer.add(BorderLayout.SOUTH, loginButton)
        demoContainer.uiid = "Wrapper"
        return BoxLayout.encloseY(demoContainer)
    }

    private fun createAutoCompleteDemo(): Container {
        val parser = CSVParser()
        val commonWords: MutableList<String> = ArrayList()

        try{
            val reader: InputStream = Display.getInstance().getResourceAsStream(javaClass, "/common-words.csv")
            val data = parser.parse(reader)
            for (s in data) {
                commonWords.add(s[0])
            }
        }catch(err: IOException) {
            Log.e(err)
        }

        val options = DefaultListModel<String?>()
        val ac: AutoCompleteTextField = object : AutoCompleteTextField(options) {
            override fun filter(text: String): Boolean {
                if (text.isEmpty()) {
                    options.removeAll()
                    return false
                }
                val matchedWords = searchWords(text, commonWords)
                options.removeAll()
                if (matchedWords.isEmpty()) {
                    return true
                }
                for (s in matchedWords) {
                    options.addItem(s)
                }
                return true
            }
        }

        ac.uiid = "DemoTextArea"
        val renderer = DefaultListCellRenderer<Any>()
        renderer.uiid = "DemoLabel"
        renderer.isShowNumbers = false
        ac.setCompletionRenderer(renderer)
        val demoContainer = BoxLayout.encloseY(Label("Search:", "DemoLabel"), ac)
        demoContainer.uiid = "Wrapper"
        return BoxLayout.encloseY(demoContainer)
    }

    fun searchWords(text: String?, wordsList: List<String>): MutableList<String> {
        val matchedWords: MutableList<String> = ArrayList()
        var count = 0
        for (word in wordsList) {
            if (word.contains(text!!)) {
                matchedWords.add(word)
                if (++count == 5) {
                    return matchedWords
                }
            }
        }
        return matchedWords
    }

    private fun createTextComponentContainer(): Container {
        // Add some text fields to the page
        val name = TextComponent().labelAndHint("Name")
        FontImage.setMaterialIcon(name.field.hintLabel, FontImage.MATERIAL_PERSON)
        val email = TextComponent().labelAndHint("E-mail").constraint(TextArea.EMAILADDR)
        FontImage.setMaterialIcon(email.field.hintLabel, FontImage.MATERIAL_EMAIL)
        val password = TextComponent().labelAndHint("Password").constraint(TextArea.PASSWORD)
        FontImage.setMaterialIcon(password.field.hintLabel, FontImage.MATERIAL_LOCK)
        val bio = TextComponent().labelAndHint("Bio").multiline(true)
        FontImage.setMaterialIcon(bio.field.hintLabel, FontImage.MATERIAL_LIBRARY_BOOKS)
        val saveButton = Button("Save", "InputSaveButton")

        // Add validation to the save Button
        val saveValidation = Validator()
        saveValidation.addConstraint(email, RegexConstraint.validEmail())
        saveValidation.addSubmitButtons(saveButton)
        saveButton.addActionListener {
            // Show saving status
            val savingStatus = ToastBar.getInstance().createStatus()
            savingStatus.message = "Saving"
            savingStatus.setExpires(3000)
            savingStatus.isShowProgressIndicator = true
            savingStatus.show()

            // Show saved
            val saved = ToastBar.getInstance().createStatus()
            saved.message = "Input was successfully saved"
            saved.showDelayed(4000)
            saved.setExpires(2000)
            saved.show()
            name.field.clear()
            email.field.clear()
            bio.field.clear()
            password.field.clear()
        }

        val textFields = BoxLayout.encloseY(name, email, password, bio)
        textFields.isScrollableY = true
        val textFieldsAndSaveButton = BorderLayout.south(saveButton).add(BorderLayout.CENTER, textFields)
        textFieldsAndSaveButton.uiid = "Wrapper"
        return BorderLayout.center(textFieldsAndSaveButton)
    }

    init {
        init("Text Input", Resources.getGlobalResources().getImage("text-field-demo.png"), parentForm, "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/TextInputDemo.java")
    }
}