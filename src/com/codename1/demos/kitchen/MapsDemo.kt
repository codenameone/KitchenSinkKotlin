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

import com.codename1.components.FloatingActionButton
import com.codename1.components.SpanLabel
import com.codename1.components.ToastBar
import com.codename1.googlemaps.MapContainer
import com.codename1.maps.Coord
import com.codename1.ui.*
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.layouts.FlowLayout
import com.codename1.ui.layouts.LayeredLayout
import com.codename1.ui.plaf.UIManager
import com.codename1.ui.util.Resources
import java.util.*

/**
 * Class that demonstrate the usage of the Google Maps within the app.
 *
 * @author Sergey Gerashenko.
 */
class MapsDemo(parentForm: Form) : Demo() {
    // Should be replaced with real api key in order to activate the demo. 
    private val googleMapsHTMLKey: String? = null
    var markerList: MutableList<Coord> = ArrayList()

    override fun createContentPane(): Container? {
        val demoContainer = Container(BorderLayout(), "DemoContainer")

        demoContainer.add(BorderLayout.NORTH, createComponent(Resources.getGlobalResources().getImage("map-google-component.png"),
                "Google Map",
                "Google Map class") {
            if (googleMapsHTMLKey == null) {
                showDemo("Google Map", createKeysGuide())
            } else {
                showDemo("Google Map", createGoogleMapComponent())
            }
        })

        return demoContainer
    }

    private fun createGoogleMapComponent(): Component {
        val map = MapContainer(googleMapsHTMLKey)
        val moveToCurrentLocation = FloatingActionButton.createFAB(FontImage.MATERIAL_GPS_FIXED, "MapsCurrLocation")

        moveToCurrentLocation.addActionListener {
            val currLocation = Display.getInstance().locationManager.currentLocationSync
            if (currLocation != null) {
                map.zoom(Coord(currLocation.latitude, currLocation.longitude), (map.maxZoom + map.minZoom) / 2)
            } else {
                ToastBar.showInfoMessage("Turn on Location")
            }
        }

        val markerImg = FontImage.createMaterial(FontImage.MATERIAL_PLACE,
                UIManager.getInstance().getComponentStyle("MapsPlace"))
        val markerImgSize = CN.convertToPixels(10f)

        map.addTapListener { e ->
            val currLocation: Coord = map.getCoordAtPosition(e.x, e.y)
            val placeName = TextComponent().labelAndHint("Mark name: ")
            val ok = Command("Ok")
            val cancel = Command("Cancel")
            if (Dialog.show("Enter Note", placeName, ok, cancel) === ok && placeName.text.isNotEmpty()) {
                map.addMarker(EncodedImage.createFromImage(markerImg, false).scaledEncoded(markerImgSize, markerImgSize),
                        currLocation,
                        placeName.text,
                        "",
                        null)
                markerList.add(currLocation)
            }
        }

        val btnClearAll = Button("Clear All", "MapsButton")
        btnClearAll.addActionListener {
            map.clearMapLayers()
            markerList.clear()
        }

        val btnAddPath = Button("Add Path", "MapsButton")
        btnAddPath.addActionListener {
            if (markerList.size > 1) {
                map.addPath(*markerList.toTypedArray())
            } else {
                ToastBar.showInfoMessage("You need add more markers(try to press the map)")
            }
        }

        val root = LayeredLayout.encloseIn(BorderLayout.center(map),
                BorderLayout.south(FlowLayout.encloseBottom(btnAddPath, btnClearAll)))

        return moveToCurrentLocation.bindFabToContainer(root)
    }

    private fun createKeysGuide(): Container {
        val keysGuideContainer = Container(BoxLayout(BoxLayout.Y_AXIS))
        keysGuideContainer.add(Label("Configuration", "DemoHeaderLabel"))
        keysGuideContainer.add(SpanLabel("In order to make this demo work you should generate Api Keys and define the following build arguments within your project:", "DemoLabel"))

        val javascriptKey = TextArea("javascript.googlemaps.key=YOUR_JAVASCRIPT_API_KEY")
        javascriptKey.uiid = "DemoTextArea"

        val androidKey = TextArea("android.xapplication=<meta-data android:name=\"com.google.android.maps.v2.API_KEY\" android:value=\"YOUR_ANDROID_API_KEY\"/>")
        androidKey.uiid = "DemoTextArea"

        val iosKey = TextArea("ios.afterFinishLaunching=[GMSServices provideAPIKey:@\"YOUR_IOS_API_KEY\"];")
        iosKey.uiid = "DemoTextArea"
        keysGuideContainer.addAll(javascriptKey, androidKey, iosKey)
        keysGuideContainer.add(SpanLabel("Make sure to replace the values YOUR_ANDROID_API_KEY, YOUR_IOS_API_KEY, and YOUR_JAVASCRIPT_API_KEY with the values you obtained from the Google Cloud console by following the instructions.\n" +
            "Also you need to replace the \"googleMapsHTMLKey\" attribute in the source code.", "DemoLabel"))

        val javascriptButton = Button("Generate Javascript key", "DemoButton")
        javascriptButton.addActionListener { CN.execute("https://developers.google.com/maps/documentation/javascript/overview") }

        val iosButton = Button("Generate IOS Key", "DemoButton")
        iosButton.addActionListener { CN.execute("https://developers.google.com/maps/documentation/ios-sdk/start") }

        val androidButton = Button("Generate Android Key", "DemoButton")
        androidButton.addActionListener { CN.execute("https://developers.google.com/maps/documentation/android-sdk/start") }

        val infoButton = Button("For More Information", "DemoButton")
        infoButton.addActionListener { CN.execute("https://www.codenameone.com/blog/new-improved-native-google-maps.html") }
        keysGuideContainer.addAll(javascriptButton, iosButton, androidButton, infoButton)
        keysGuideContainer.uiid = "Wrapper"

        val demoContainer = BoxLayout.encloseY(keysGuideContainer)
        demoContainer.isScrollableY = true

        return demoContainer
    }

    init {
        init("Maps", Resources.getGlobalResources().getImage("demo-maps.png"), parentForm,
                "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/MapsDemo.java")
    }
}