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
import com.codename1.components.SpanLabel
import com.codename1.ui.*
import com.codename1.ui.geom.Dimension
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.layouts.GridLayout
import com.codename1.ui.plaf.Style
import com.codename1.ui.util.Resources

/**
 * Class that demonstrate a simple usage of the Label, SpanLabel, and ScaleImageLabel components.
 * The Label are one of the most basic Components of Codename One that allow to display text/image on the form.
 *
 * @author Sergey Gerashenko.
 */
class LabelsDemo(parentForm: Form) : Demo() {
    override fun createContentPane(): Container? {
        val demoContainer = Container(BoxLayout(BoxLayout.Y_AXIS), "DemoContainer")
        demoContainer.isScrollableY = true
        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("label.png"),
                "Label",
                "Allows displaying a single line of text and",
                "icon (both optional) with different alignment options. This class is a base class for several " +
                        "components allowing them to declare alignment/icon appearance universally.") { showDemo("Label", createLabelDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("span-label.png"),
                "Span Label",
                "A multi line label component that can be",
                "easily localized, this is simply based on a text area combined with a label.") { showDemo("SpanLabel", createSpanLabelDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("scale-image-label.png"),
                "Scale Image Label",
                "Label that simplifies the usage of scale to",
                """
                    fill/fit. This is effectively equivalent to just setting the style image on a label but more convenient for some special circumstances

                    One major difference is that preferred size equals the image in this case. The default UIID for this component is label
                    """.trimIndent()) { showDemo("Scale image label", createScaleImageLabelDemo()) })

        return demoContainer
    }

    private fun createLabelDemo(): Container {
        val cnt1 = BoxLayout.encloseY(Label("Text Label:", "DemoHeader"),
                Label("label", "DemoLabel"))
        cnt1.uiid = "LabelContainer"

        val cnt2 = BoxLayout.encloseY(Label("Image Label:", "DemoHeader"),
                Label(Resources.getGlobalResources().getImage("code-name-one-icon.png"), "DemoLabel"))
        cnt2.uiid = "LabelContainer"

        val cnt3 = BoxLayout.encloseY(Label("text and image Label:", "DemoHeader"),
                Label("label", Resources.getGlobalResources().getImage("code-name-one-icon.png"), "DemoLabel"))
        cnt3.uiid = "LabelContainer"

        return BoxLayout.encloseY(cnt1, cnt2, cnt3)
    }

    private fun createSpanLabelDemo(): Container {
        val cnt1 = BoxLayout.encloseY(Label("SpanLabel:", "DemoHeader"),
                SpanLabel("A multi line label component that can be easily localized, this is simply based on a text area combined with a label.", "DemoLabel"))
        cnt1.uiid = "LabelContainer"

        val labelWithIconWest = SpanLabel("A multi line label component that can be easily localized, this is simply based on a text area combined with a label.", "DemoLabel")
        labelWithIconWest.materialIcon = FontImage.MATERIAL_INFO
        labelWithIconWest.iconUIID = "DemoSpanLabelIcon"
        labelWithIconWest.iconPosition = "West"

        val cnt2 = BoxLayout.encloseY(Label("SpanLabel with icon (West):", "DemoHeader"), labelWithIconWest)
        cnt2.uiid = "LabelContainer"

        val labelWithIconNorth = SpanLabel("A multi line label component that can be easily localized, this is simply based on a text area combined with a label.", "DemoLabel")
        labelWithIconNorth.materialIcon = FontImage.MATERIAL_INFO
        labelWithIconNorth.iconUIID = "DemoSpanLabelIcon"
        labelWithIconNorth.iconPosition = "North"

        val cnt3 = BoxLayout.encloseY(Label("SpanLabel with icon (North):", "DemoHeader"), labelWithIconNorth)
        cnt3.uiid = "LabelContainer"

        return BoxLayout.encloseY(cnt1, cnt2, cnt3)
    }

    private fun createScaleImageLabelDemo(): Container {
        val labelContainer = Container(BoxLayout(BoxLayout.Y_AXIS))
        labelContainer.add(Label("Scale image label:", "DemoLabel"))

        val imageLabel: ScaleImageLabel = object : ScaleImageLabel(Resources.getGlobalResources().getImage("scale-image-label.png")) {
            override fun calcPreferredSize(): Dimension {
                val d = super.calcPreferredSize()
                d.height = Display.getInstance().displayHeight / 7
                return d
            }
        }
        imageLabel.backgroundType = Style.BACKGROUND_IMAGE_SCALED
        labelContainer.add(imageLabel)
        labelContainer.add(Label("   "))
        labelContainer.add(Label("3 Scale image labels", "DemoLabel"))
        labelContainer.add(Label("(auto scaled to fit available space)", "GreyLabel"))

        val threeImagesContainer = Container(GridLayout(1, 3))

        val label1 = ScaleImageLabel(Resources.getGlobalResources().getImage("scale-image-label.png"))
        label1.backgroundType = Style.BACKGROUND_IMAGE_SCALED
        label1.uiid = "DemoScaleImageLabel"

        val label2 = ScaleImageLabel(Resources.getGlobalResources().getImage("scale-image-label.png"))
        label2.backgroundType = Style.BACKGROUND_IMAGE_SCALED
        label2.uiid = "DemoScaleImageLabel"

        val label3 = ScaleImageLabel(Resources.getGlobalResources().getImage("scale-image-label.png"))
        label3.backgroundType = Style.BACKGROUND_IMAGE_SCALED
        label3.uiid = "DemoScaleImageLabel"
        threeImagesContainer.addAll(label1, label2, label3)
        labelContainer.add(threeImagesContainer)
        labelContainer.uiid = "LabelContainer"

        val scaledFill = ScaleImageLabel(Resources.getGlobalResources().getImage("scale-image-label.png"))
        scaledFill.backgroundType = Style.BACKGROUND_IMAGE_SCALED_FILL
        labelContainer.add(Label("   "))
        labelContainer.add(Label("Scaled Fill:", "DemoLabel"))
        labelContainer.add(scaledFill)

        return BoxLayout.encloseY(labelContainer)
    }

    init {
        init("Labels", Resources.getGlobalResources().getImage("demo-labels.png"), parentForm,
                "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/LabelsDemo.java")
    }
}