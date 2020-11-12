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
import com.codename1.components.ToastBar
import com.codename1.ui.*
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.list.GenericListCellRenderer
import com.codename1.ui.util.Resources
import java.util.*
import java.util.Calendar

/**
 * Class that demonstrate the usage of the ComboBox and PickerComponent components.
 * The Selection components are lists of items that allow to select one or more items at a time.
 *
 * @author Sergey Gerashenko.
 */
class SelectionDemo(parentForm: Form) : Demo() {
    override fun createContentPane(): Container? {
        val demoContainer = Container(BoxLayout(BoxLayout.Y_AXIS), "DemoContainer")
        demoContainer.isScrollableY = true

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("combo-box.png"),
                "Combo Box",
                "ComboBox is a list that allows only one",
                "selection at a time, when a user clicks * the code ComboBox a popup button with the full list of elements allows the " +
                        "selection of * a single element. The ComboBox is a driven by the list model and allows all the renderer * features of the " +
                        "list as well.") { showDemo("Combo Box", createComboBoxDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("date-picker.png"),
                "Date Picker",
                "Date Picker is a PickerComponent use",
                "PickerComponent.createDate(null).label(\"Select Birthday\")") { showDemo("Date Picker", createDatePickerDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("time-picker.png"),
                "Time Picker",
                "Time Picker is a PickerComponent use",
                "PickerComponent.createTime(null).label(\"Select Alarm time\")") { showDemo("Time Picker", createTimePickerDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("date-time-picker.png"),
                "Date Time Picker",
                "DateTime Picker is a PickerComponent use ",
                "PickerComponent.createDateTime(null).label (\"Select Meeting schedule\")") { showDemo("Date Time Picker", createDateTimePickerDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("minute-picker.png"),
                "Minute Duration Picker",
                "Minute Picker is a PickerComponent use",
                "PickerComponent.createDurationMinutes (0).label(\"Select Duration\")") { showDemo("Minute Duration Picker", createMinuteDurationPickerDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("hour-picker.png"),
                "Minute, Hour, Duration Picker",
                "Hour Minute Picker is a PickerComponent",
                "use PickerComponent.createDurationHoursMinutes(0,0).label(\"Select Duration\")") { showDemo("Minute, Hour, Duration Picker", createMinuteHourPickerDemo()) })

        return demoContainer
    }

    private fun createComboBoxDemo(): Container {
        val demoContainer = Container(BorderLayout())
        val combo = ComboBox<Map<String, Any>>(
                createListEntry("A Game of Thrones", "1996", "4.45"),
                createListEntry("A Clash Of Kings", "1998", "4.41"),
                createListEntry("A Storm Of Swords", "2000", "4.54"),
                createListEntry("A Feast For Crows", "2005", "4.14"),
                createListEntry("A Dance With Dragons", "2011", "4.33"),
                createListEntry("The Winds of Winter", "2016", "4.40"),
                createListEntry("A Dream of Spring", "Unpublished", "unknown")
        )

        val mb1 = MultiButton()
        mb1.uiid = "DemoMultiButton"
        mb1.uiidLine1 = "DemoMultiLine1"
        mb1.uiidLine2 = "DemoMultiLine2"

        val mb2 = MultiButton()
        mb2.uiid = "DemoMultiButton"
        mb2.uiidLine1 = "DemoMultiLine1"
        mb2.uiidLine2 = "DemoMultiLine2"
        combo.renderer = GenericListCellRenderer<Any>(mb1, mb2)
        combo.selectedIndex = 1
        demoContainer.add(BorderLayout.CENTER, BoxLayout.encloseY(combo))

        val showRating = Button("Show Rating", "DemoButton")
        showRating.addActionListener {
            val selectedItem: Map<*, *> = combo.selectedItem
            ToastBar.showInfoMessage(selectedItem["Line1"] as String? + " rating is: " + selectedItem["rating"] as String?)
        }

        demoContainer.add(BorderLayout.SOUTH, showRating)
        demoContainer.uiid = "Wrapper"
        return BoxLayout.encloseY(demoContainer)
    }

    private fun createDatePickerDemo(): Container {
        val datePicker = PickerComponent.createDate(null).label("Select Birthday: ")
        datePicker.uiid = "DemoPicker"
        val save = Button("Save Birthday", "DemoButton")
        save.addActionListener { ToastBar.showInfoMessage("Birthday was saved: " + datePicker.picker.text) }
        val demoContainer = BorderLayout.center(BoxLayout.encloseY(datePicker))
        demoContainer.add(BorderLayout.SOUTH, save)
        demoContainer.uiid = "Wrapper"
        return BoxLayout.encloseY(demoContainer)
    }

    private fun createTimePickerDemo(): Container {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val minutes = calendar[Calendar.MINUTE]
        val hours = calendar[Calendar.HOUR]
        val timePicker = PickerComponent.createTime(hours * 60 + minutes).label("Select Alarm Time ")
        timePicker.uiid = "DemoPicker"

        val setAlarm = Button("Set Alarm", "DemoButton")
        setAlarm.addActionListener { ToastBar.showInfoMessage("Alarm set for: " + timePicker.picker.text) }

        val demoContainer = BorderLayout.center(BoxLayout.encloseY(timePicker))
        demoContainer.add(BorderLayout.SOUTH, setAlarm)
        demoContainer.uiid = "Wrapper"
        return BoxLayout.encloseY(demoContainer)
    }

    private fun createDateTimePickerDemo(): Container {
        val meetingPicker = PickerComponent.createDateTime(null).label("Select meeting schedule")
        meetingPicker.uiid = "DemoPicker"
        val scheduleMeeting = Button("Schedule Meeting", "DemoButton")
        scheduleMeeting.addActionListener { ToastBar.showInfoMessage("Meeting was scheduled at: " + meetingPicker.picker.text) }
        val demoContainer = BorderLayout.center(BoxLayout.encloseY(meetingPicker))
        demoContainer.add(BorderLayout.SOUTH, scheduleMeeting)
        demoContainer.uiid = "Wrapper"
        return BoxLayout.encloseY(demoContainer)
    }

    private fun createMinuteDurationPickerDemo(): Container {
        val durationPicker = PickerComponent.createDurationMinutes(0).label("Select Duration")
        durationPicker.uiid = "DemoPicker"
        val setTimer = Button("Set Timer", "DemoButton")
        setTimer.addActionListener { ToastBar.showInfoMessage("Timer set for: " + durationPicker.picker.text) }
        val demoContainer = BorderLayout.center(BoxLayout.encloseY(durationPicker))
        demoContainer.add(BorderLayout.SOUTH, setTimer)
        demoContainer.uiid = "Wrapper"
        return BoxLayout.encloseY(demoContainer)
    }

    private fun createMinuteHourPickerDemo(): Container {
        val durationPicker = PickerComponent.createDurationHoursMinutes(0, 0).label("Select Duration")
        durationPicker.uiid = "DemoPicker"
        val setTimer = Button("Set Timer", "DemoButton")
        setTimer.addActionListener { ToastBar.showInfoMessage("Timer set for: " + durationPicker.picker.text) }
        val demoContainer = BorderLayout.center(BoxLayout.encloseY(durationPicker))
        demoContainer.add(BorderLayout.SOUTH, setTimer)
        demoContainer.uiid = "Wrapper"
        return BoxLayout.encloseY(demoContainer)
    }

    private fun createListEntry(name: String, date: String, rating: String): Map<String, Any> {
        val entry: MutableMap<String, Any> = HashMap()
        entry["Line1"] = name
        entry["Line2"] = date
        entry["rating"] = rating
        return entry
    }

    init {
        init("Selection", Resources.getGlobalResources().getImage("selection-demo.png"), parentForm, "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/SelectionDemo.java")
    }
}