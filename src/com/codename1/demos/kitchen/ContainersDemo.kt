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
import com.codename1.components.SplitPane.Settings
import com.codename1.demos.kitchen.charts.DemoCharts
import com.codename1.io.rest.Rest
import com.codename1.ui.*
import com.codename1.ui.geom.Dimension
import com.codename1.ui.layouts.*
import com.codename1.ui.plaf.Style
import com.codename1.ui.plaf.UIManager
import com.codename1.ui.table.TableLayout
import com.codename1.ui.util.Resources
import kotlin.collections.ArrayList


/**
 * Class that demonstrate the usage of the Accordion, InfiniteContainer, SplitPane, and Tabs Containers.
 * The Containers are Components that allow to Contain other Components inside them and to arrange them using layout manager.
 * The Containers are derived from Component class so they able to contain another containers as well.
 *
 * @author Sergey Gerashenko.
 */
class ContainersDemo(parentForm: Form) : Demo() {
    private var colorLabelList: MutableList<Component>? = null
    private var colorsContainer: Container = Container(BoxLayout(BoxLayout.Y_AXIS))

    override fun createContentPane(): Container? {
        val demoContainer = Container(BoxLayout(BoxLayout.Y_AXIS), "DemoContainer")
        demoContainer.isScrollableY = true
        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("accordion.png"),
                "Accordion",
                "This Accordion ui pattern is a vertically",
                    "stacked list of items. Each items can be opened/closed to reveal more content similar to a Tree however unlike " +
                    "the the Tree the Accordion is designed to include containers or arbitrary components rather than model based data." +
                    "This makes the Accordion more convenient as a tool for folding/collapsing UI elements known in advance whereas a " +
                    "tree makes more sense as a tool to map data e.g filesystem structure, XML hierarchy etc." +
                    "Note that the Accordion like many composite components in Codename One is scrollable by default which " +
                    "means you should use it within a non-scrollable hierarchy. If you wish to add it into a scrollable " +
                    "Container you should disable it's default scrollability using setScrollable(false)."
                    ) { showDemo("Accordion", createAccordionDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("infinite-container.png"),
                "Infinite Container",
                "This abstract Container can scroll",
                    "indefinitely (or at least until we run out of data). This class uses the InfiniteScrollAdapter to bring more data " +
                         "and the pull to refresh feature to refresh current displayed data." +
                         "The sample code shows the usage of the nestoria API to fill out an infinitely scrolling list."
                    ) { showDemo("Infinite Container", createInfiniteContainerDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("split-pane.png"),
                "Split Pane",
                "A split pane can either be horizontal or",
                    "vertical, and provides a draggable divider between two components. If the orientation is HORIZONTAL_SPLIT, " +
                            "then the child components will be laid out horizontally (side by side with a vertical bar as a divider). If " +
                            "the orientation is VERTICAL_SPLIT, then the components are laid out vertically. One above the other." +
                            "The bar divider bar includes to collapse and expand the divider also."
                            ) { showDemo("Split Pane", createSplitPaneDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("tabs.png"),
                "Tabs",
                "A component that lets the user switch",
                "between a group if components by clicking on a tab with a given title and/or icon." +
                        "Tabs/components are added to a Tabs object by using the addTab and insertTab methods. A tab is represented by an index " +
                        "corresponding to the position it was added in, where the first tab has an index equal to 0 and the last tab has an index " +
                        "equal to the tab count minus 1. The Tabs uses a SingleSelectionModel to represent the set of tab indices and the currently " +
                        "selected index. If the tab count is greater that 0, then there will always be a selected index, which by default will be " +
                        "initialized to the first tab. If the tab count is 0, then the selected index will be -1. A simple Tabs looks like a bit " +
                        "like this.") { showDemo("Tabs", createTabsDemo()) })

        return demoContainer
    }

    private fun createAccordionDemo(): Container {
        val accordion = Accordion()
        accordion.headerUIID = "DemoAccordionHeader"
        accordion.openCloseIconUIID = "DemoAccordionIcon"
        accordion.setOpenIcon(FontImage.MATERIAL_EXPAND_LESS)
        accordion.setCloseIcon(FontImage.MATERIAL_EXPAND_MORE)
        accordion.addContent("Does this product have what I need?", SpanLabel("Totally. Totally does", "DemoAccordionLabel"))
        accordion.addContent("Can I use it all the time?", SpanLabel("Of course you can, we won't stop you", "DemoAccordionLabel"))
        accordion.addContent("Are there any restrictions?", SpanLabel("Only your imagination my friend. Go for it!", "DemoAccordionLabel"))
        return accordion
    }

    private fun createInfiniteContainerDemo(): Container {
        val firstURL = "https://www.codenameone.com/files/kitchensink/dogs/list.json"
        val tempPlaceHolder = Resources.getGlobalResources().getImage("blurred-puppy.jpg")
        val placeholder = EncodedImage.createFromImage(tempPlaceHolder, true)

        val infiniteContainer= object : InfiniteContainer(10) {
            var nextURL: String? = firstURL

            override fun fetchComponents(index: Int, amount: Int): Array<Component?>? {
                // pull to refresh resets the position
                if (index == 0) {
                    nextURL = firstURL
                }
                // nextUrl is null when there is no more data to fetch.
                if (nextURL == null) {
                    return null
                }

                // Request the data from the server.
                val resultData = Rest.get(nextURL).acceptJson().asJsonMap
                if (resultData.responseCode != 200) {
                    CN.callSerially { ToastBar.showErrorMessage("Error code from the server") }
                    return null
                }

                val itemListData: Map<*, *> = resultData.responseData

                nextURL = if (itemListData["nextPage"] != null){
                    itemListData["nextPage"] as String
                }else{
                    null
                }

                val itemList: MutableList<Item> = ArrayList()
                for (currItemMap in (itemListData["items"] as ArrayList<Map<String, Any>>)) {
                    val currItem = Item(currItemMap["title"] as String, currItemMap["details"] as String, currItemMap["url"] as String, currItemMap["thumb"] as String)
                    itemList.add(currItem)
                }

                val result: Array<Component?> = arrayOfNulls(itemList.size)
                for (i in 0 until itemList.size) {
                    // Get all the necessary data.
                    val currItem = itemList[i]
                    val title = currItem.title
                    val url = currItem.url
                    val fileName: String? = url.substring(url.lastIndexOf("/") + 1)
                    val image = URLImage.createToStorage(placeholder, fileName, url, URLImage.RESIZE_SCALE_TO_FILL)

                    // Build the components.
                    val imageLabel = object : ScaleImageLabel(image) {
                        override fun calcPreferredSize(): Dimension {
                            val dm = super.calcPreferredSize()
                            dm.height = CN.convertToPixels(30f)
                            return dm
                        }
                    }

                    imageLabel.backgroundType = Style.BACKGROUND_IMAGE_SCALED_FILL
                    result[i] = (LayeredLayout.encloseIn(imageLabel,
                            BorderLayout.south(Label(title, "InfiniteComponentTitle"))))
                }
                return result
            }
        }
        return BorderLayout.center(infiniteContainer)
    }

    private fun createTabsDemo(): Container {
        val tabsContainer = Tabs()
        tabsContainer.uiid = "DemoTabsContainer"
        tabsContainer.addTab("Categories", FontImage.createMaterial(FontImage.MATERIAL_PIE_CHART, UIManager.getInstance().getComponentStyle("Tab")),
                DemoCharts.createCategoriesContainer())

        tabsContainer.addTab("Annual review", FontImage.createMaterial(FontImage.MATERIAL_SHOW_CHART, UIManager.getInstance().getComponentStyle("Tab")),
                DemoCharts.createAnnualContainer())

        return tabsContainer
    }

    private fun createSplitPaneDemo(): Container {
        val flow = Button("Flow Layout")
        flow.uiid = "DemoButton"
        flow.addActionListener {
            resetMargin(colorsContainer)
            colorsContainer.layout = FlowLayout()
            colorsContainer.animateLayout(1000)
        }

        val flowCenter = Button("Flow Center Layout")
        flowCenter.uiid = "DemoButton"
        flowCenter.addActionListener {
            resetMargin(colorsContainer)
            colorsContainer.layout = FlowLayout(Component.CENTER)
            colorsContainer.animateLayout(1000)
        }

        val border = Button("border Layout")
        border.uiid = "DemoButton"
        border.addActionListener {
            resetMargin(colorsContainer)
            colorsContainer.removeAll()
            colorsContainer.layout = BorderLayout()
            colorsContainer.add(BorderLayout.CENTER, colorLabelList!![0]).add(BorderLayout.WEST, colorLabelList!![1]).add(BorderLayout.EAST, colorLabelList!![2]).add(BorderLayout.NORTH, colorLabelList!![3]).add(BorderLayout.SOUTH, colorLabelList!![4])
            colorsContainer.animateLayout(1000)
        }

        val absoluteBorder = Button("Absolute Border Layout")
        absoluteBorder.uiid = "DemoButton"
        absoluteBorder.addActionListener {
            resetMargin(colorsContainer)
            colorsContainer.removeAll()
            colorsContainer.layout = BorderLayout(CN.CENTER_BEHAVIOR_CENTER)
            colorsContainer.add(BorderLayout.CENTER, colorLabelList!![0]).add(BorderLayout.WEST, colorLabelList!![1]).add(BorderLayout.EAST, colorLabelList!![2]).add(BorderLayout.NORTH, colorLabelList!![3]).add(BorderLayout.SOUTH, colorLabelList!![4])
            colorsContainer.animateLayout(1000)
        }

        val boxX = Button("Box X Layout")
        boxX.uiid = "DemoButton"
        boxX.addActionListener {
            resetMargin(colorsContainer)
            colorsContainer.layout = BoxLayout(BoxLayout.X_AXIS)
            colorsContainer.animateLayout(1000)
        }

        val boxY = Button("Box Y Layout")
        boxY.uiid = "DemoButton"
        boxY.addActionListener {
            resetMargin(colorsContainer)
            colorsContainer.layout = BoxLayout(BoxLayout.Y_AXIS)
            colorsContainer.animateLayout(1000)
        }

        val grid = Button("Grid Layout")
        grid.uiid = "DemoButton"
        grid.addActionListener {
            resetMargin(colorsContainer)
            colorsContainer.layout = GridLayout(1, 1)
            colorsContainer.animateLayout(1000)
        }

        val simpleTable = Button("Table Layout(simple)")
        simpleTable.uiid = "DemoButton"
        simpleTable.addActionListener {
            resetMargin(colorsContainer)
            colorsContainer.layout = TableLayout(3, 2)
            colorsContainer.removeAll()
            colorsContainer.addAll(colorLabelList!![0],
                    colorLabelList!![1],
                    colorLabelList!![2],
                    colorLabelList!![3],
                    colorLabelList!![4])
            colorsContainer.animateLayout(1000)
        }

        val complexTable = Button("Table Layout(complex)")
        complexTable.uiid = "DemoButton"
        complexTable.addActionListener {
            resetMargin(colorsContainer)
            colorsContainer.removeAll()
            buildComplexTableUI(colorsContainer)
            colorsContainer.animateLayout(1000)
        }

        val layered = Button("Layered Layout")
        layered.uiid = "DemoButton"
        layered.addActionListener {
            resetMargin(colorsContainer)
            colorsContainer.layout = LayeredLayout()

            // Increase the margin by 3 mm for every Component in the container for better
            //   visual effect of the LayeredLayout.
            setMarginForLayeredLayout(colorsContainer)
            colorsContainer.animateLayout(1000)
        }


        // Make a Button container 
        val buttonList = BoxLayout.encloseY(flow,
                flowCenter,
                border,
                absoluteBorder,
                boxX,
                boxY,
                grid,
                simpleTable,
                complexTable,
                layered)
        buttonList.isScrollableY = true

        // Make some blank Labels with background colors from the CSS file.
        colorLabelList = arrayListOf(Label("                    ", "RedLabel"),
                Label("                    ", "BlueLabel"),
                Label("                    ", "GreenLabel"),
                Label("                    ", "OrangeLabel"),
                Label("                    ", "PurpleLabel")
        )

        // Make an anonymous claas that override calcPreferredSize to fit exactly a half of the screen.
        // Alternatively you could use TableLayout instead of BorderLayout where i could explicitly define the height in percentages.
        //   or GridLayout that would divide the ContentPane by 2 for every Component within it.
        colorsContainer.addAll(colorLabelList?.get(0),
                                colorLabelList?.get(1),
                                colorLabelList?.get(2),
                                colorLabelList?.get(3),
                                colorLabelList?.get(4))

        colorsContainer.setShouldCalcPreferredSize(true)
        return SplitPane(Settings().orientation(SplitPane.VERTICAL_SPLIT), colorsContainer, buttonList)
    }

    // Reset the margin for all the components inside the given container.
    private fun resetMargin(colorsContainer: Container) {
        val margin = 0.5f
        for (cmp in colorsContainer) {
            cmp.allStyles.setMargin(margin, margin, margin, margin)
        }
    }

    private fun setMarginForLayeredLayout(colorsContainer: Container) {
        var margin = 0f
        for (cmp in colorsContainer) {
            cmp.allStyles.setMargin(margin, margin, margin, margin)
            margin += 3f
        }
    }

    private fun buildComplexTableUI(colorsContainer: Container) {
        val tl = TableLayout(2, 3)
        colorsContainer.layout = tl
        colorsContainer.add(tl.createConstraint().widthPercentage(20),
                colorLabelList!![0])
        colorsContainer.add(tl.createConstraint().horizontalSpan(2).heightPercentage(80).verticalAlign(Component.CENTER).horizontalAlign(Component.CENTER),
                colorLabelList!![1])
        colorsContainer.add(colorLabelList!![2])
        colorsContainer.add(tl.createConstraint().widthPercentage(60).heightPercentage(20),
                colorLabelList!![3])
        colorsContainer.add(tl.createConstraint().widthPercentage(20),
                colorLabelList!![4])
    }

    init {
        init("Containers", Resources.getGlobalResources().getImage("containers-demo.png"), parentForm, "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/ContainersDemo.java")
    }

    private data class Item(val title: String, val details: String, val url: String, val thumb: String)
}