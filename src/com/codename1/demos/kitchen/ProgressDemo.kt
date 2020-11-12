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

import com.codename1.components.InfiniteProgress
import com.codename1.components.ScaleImageLabel
import com.codename1.components.SliderBridge
import com.codename1.components.SpanLabel
import com.codename1.io.ConnectionRequest
import com.codename1.io.NetworkManager
import com.codename1.io.Util
import com.codename1.io.rest.Rest
import com.codename1.ui.*
import com.codename1.ui.CommonProgressAnimations.CircleProgress
import com.codename1.ui.CommonProgressAnimations.LoadingTextAnimation
import com.codename1.ui.animations.CommonTransitions
import com.codename1.ui.geom.Dimension
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.plaf.UIManager
import com.codename1.ui.util.Resources
import com.codename1.util.EasyThread

/**
 * Class that demonstrate the usage of the infiniteProgress, Slider, CircleAnimation and TextLoadingAnimation Components.
 * The progress components are used to inform the user that certain operation is in progress.
 *
 * @author Sergey Gerashenko.
 */
class ProgressDemo(parentForm: Form) : Demo() {
    override fun createContentPane(): Container? {
        val demoContainer = Container(BoxLayout(BoxLayout.Y_AXIS), "DemoContainer")
        demoContainer.isScrollableY = true

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("infinite-progress.png"),
                "Infinite Progress",
                "Shows a \"Washing Machine\" infinite",
                "progress indication animation, to customize the image " +
                        "you can either use the infiniteImage theme constant or the setAnimation method. The image " +
                        "is rotated automatically so don't use an animated image or anything like that as it would " +
                        "fail with the rotation logic."
        ) { showDemo("Infinite Progress", createInfiniteProgressDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("slider.png"),
                "Slider",
                "The slider component serves both as a",
                "slider widget to allow users to select a value on a scale via touch/arrows and also to indicate progress. The slider " +
                        "defaults to percentage display but can represent any positive set of values."
        ) { showDemo("Slider", createSliderDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("circle-animation.png"),
                "Circle Animation",
                "A CommonProgressAnimations which shows",
                "radial coloring to show circular progress, like a Pac-Man"
        ) { showDemo("Circle Animation", createCircleAnimationDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("text-loading-animation.png"),
                "Text Loading Animation",
                "A CommonProgressAnimations item used ",
                "to show the text is loading when we are fetching some text data from network/database"
        ) { showDemo("Text Loading Animation", createTextLoadAnimationDemo()) })

        return demoContainer
    }

    private fun createInfiniteProgressDemo(): Container {
        val ip = InfiniteProgress().showInfiniteBlocking()
        CN.invokeAndBlock {
            Util.sleep(3000) // do some long operation here.
            CN.callSerially { ip.dispose() }
        }
        val progress = InfiniteProgress()
        progress.animation = FontImage.createMaterial(FontImage.MATERIAL_AUTORENEW, UIManager.getInstance().getComponentStyle("DemoInfiniteProgress"))
        return BorderLayout.centerAbsolute(progress)
    }

    private fun createSliderDemo(): Container {
        val progress = Slider()
        val download = Button("Download", "DemoButton")
        val demoContainer = BorderLayout.south(progress).add(BorderLayout.NORTH, download)
        download.addActionListener {
            val cr = ConnectionRequest("https://www.codenameone.com/img/blog/new_icon.png", false)
            SliderBridge.bindProgress(cr, progress)
            NetworkManager.getInstance().addToQueueAndWait(cr)
            if (cr.responseCode == 200) {
                demoContainer.add(BorderLayout.CENTER, ScaleImageLabel(EncodedImage.create(cr.responseData)))
                demoContainer.revalidate()
            }
        }
        return demoContainer
    }

    private fun createCircleAnimationDemo(): Container {
        val nameLabel = SpanLabel("placeholder", "CenterAlignmentLabel")
        val circleSize = CN.convertToPixels(10f)
        val nameContainer: Container = object : Container(BorderLayout()) {
            override fun calcPreferredSize(): Dimension {
                return Dimension(circleSize, circleSize)
            }
        }
        nameContainer.add(BorderLayout.CENTER, nameLabel)
        val demoContainer = BorderLayout.centerCenter(nameContainer)
        // Replace the label by a CircleProgress to indicate that it is loading.
        CircleProgress.markComponentLoading(nameLabel).uiid = "BlueColor"

        // This code block should work without the EasyThread and callSerially()
        // its here only for the demonstration purpose.
        EasyThread.start("").run {
            Util.sleep(3000)
            val jsonData = Rest.get("https://anapioficeandfire.com/api/characters/583").acceptJson().asJsonMap
            CN.callSerially {
                nameLabel.text = (jsonData.responseData as Map<String?, String?>)["name"]
                // Replace the progress with the nameLabel now that
                // it is ready, using a fade transition
                CircleProgress.markComponentReady(nameLabel, CommonTransitions.createFade(300))
            }
        }
        return demoContainer
    }

    private fun createTextLoadAnimationDemo(): Container {
        val profileText = SpanLabel("placeholder", "DemoLabel")
        val demoContainer = BorderLayout.center(profileText)
        // Replace the label by a CircleProgress to indicate that it is loading.
        LoadingTextAnimation.markComponentLoading(profileText)


        // This code block should work without the EasyThread and callSerially()
        // its here only for the demonstration purpose.
        EasyThread.start("").run {
            Util.sleep(3000)
            val response = Rest.get("https://anapioficeandfire.com/api/characters/583").acceptJson().asJsonMap
            val data: Map<Any?, Any?> = response.responseData
            val sb = StringBuilder()
            sb.append("name: ${data["name"]} \n")
            sb.append("gender: ${data["gender"]} \n")
            sb.append("culture: ${data["culture"]} \n")
            sb.append("born: ${data["born"]} \n")

            val aliases: List<String>? = data["aliases"] as ArrayList<String>
            sb.append("aliases: \n")
            for (alias in aliases!!) {
                sb.append("$alias \n")
            }

            CN.callSerially {
                demoContainer.removeAll()
                demoContainer.add(BorderLayout.NORTH, profileText)
                profileText.text = sb.toString()
                // Replace the progress with the nameLabel now that
                // it is ready, using a fade transition
                LoadingTextAnimation.markComponentReady(profileText)
            }
        }
        return demoContainer
    }

    init {
        init("Progress", Resources.getGlobalResources().getImage("progress-demo.png"), parentForm,
                "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/ProgressDemo.java")
    }
}