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

import com.codename1.capture.Capture
import com.codename1.components.InfiniteProgress
import com.codename1.components.MediaPlayer
import com.codename1.components.MultiButton
import com.codename1.components.ToastBar
import com.codename1.io.*
import com.codename1.media.MediaManager
import com.codename1.ui.*
import com.codename1.ui.events.ActionEvent
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.plaf.UIManager
import com.codename1.ui.util.Resources
import java.io.IOException

/**
 * Class that demonstrate the usage of the MediaPlayer and the MediaManager components.
 * The MediaPlayer allows you to control video playback. To use the MediaPlayer we need to first load the Media object from the MediaManager.
 * The MediaManager is the core class responsible for media interaction in Codename One.
 *
 * @author Sergey Gerashenko.
 */
class MediaDemo(parentForm: Form) : Demo() {
    override fun createContentPane(): Container? {
        val demoContainer = Container(BoxLayout(BoxLayout.Y_AXIS), "VideoContainer")
        val iconStyle = UIManager.getInstance().getComponentStyle("MediaIcon")

        val downloadButton = createVideoComponent("Hello (Download)", "Download to FileSystem", FontImage.createMaterial(FontImage.MATERIAL_ARROW_CIRCLE_DOWN, iconStyle)
        ) {
            if (!CN.existsInFileSystem(DOWNLOADED_VIDEO)) {
                downloadFile("https://www.codenameone.com/files/hello-codenameone.mp4")
            } else {
                ToastBar.showErrorMessage("File is already downloaded", FontImage.MATERIAL_SYSTEM_UPDATE.toInt())
            }
        }

        val playOfflineButton = createVideoComponent("Hello (Offline)", "Play from FileSystem", FontImage.createMaterial(FontImage.MATERIAL_PLAY_CIRCLE_FILLED, iconStyle)
        ) {
            if (CN.existsInFileSystem(DOWNLOADED_VIDEO)) {
                playVideoOnNewForm(DOWNLOADED_VIDEO, demoContainer.componentForm)
            } else {
                ToastBar.showErrorMessage("For playing the video in offline mode you should first to download the video")
            }
        }

        val playOnlineButton = createVideoComponent("Hello (Online)", "Play thru http", FontImage.createMaterial(FontImage.MATERIAL_PLAY_CIRCLE_FILLED, iconStyle)
        ) { playVideoOnNewForm("https://www.codenameone.com/files/hello-codenameone.mp4", demoContainer.componentForm) }

        val captureVideoButton = createVideoComponent("Capture", "Record video and save to FileSystem", FontImage.createMaterial(FontImage.MATERIAL_VIDEOCAM, iconStyle)
        ) {
            val capturedVideo = Capture.captureVideo()
            if (capturedVideo != null) {
                try {
                    Util.copy(CN.openFileInputStream(capturedVideo), CN.openFileOutputStream(CAPTURED_VIDEO))
                } catch (err: IOException) {
                    Log.e(err)
                }
            }
        }

        val playCaptured = createVideoComponent("Play", "Play captured video", FontImage.createMaterial(FontImage.MATERIAL_PLAY_CIRCLE_FILLED, iconStyle)
        ) {
            if (CN.existsInFileSystem(CAPTURED_VIDEO)) {
                playVideoOnNewForm(CAPTURED_VIDEO, demoContainer.componentForm)
            } else {
                ToastBar.showErrorMessage("you should to capture video first")
            }
        }

        demoContainer.addAll(downloadButton, playOfflineButton, playOnlineButton, captureVideoButton, playCaptured)
        return demoContainer
    }

    private fun playVideoOnNewForm(fileURI: String, parentForm: Form) {
        val videoForm = Form("Video", BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER))
        videoForm.contentPane.uiid = "ComponentDemoContainer"

        val toolbar = videoForm.toolbar
        toolbar.uiid = "DemoToolbar"
        toolbar.titleComponent.uiid = "DemoTitle"
        videoForm.add(CN.CENTER, InfiniteProgress())

        val backCommand = Command.create("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, UIManager.getInstance().getComponentStyle("DemoTitleCommand"))
        ) { parentForm.showBack() }

        toolbar.setBackCommand(backCommand)
        videoForm.show()
        CN.scheduleBackgroundTask {
            try {
                val video = MediaManager.createMedia(fileURI, true)
                if (video != null) {
                    video.prepare()
                    video.isNativePlayerMode = !(CN.isDesktop() || CN.isSimulator())
                    val player = MediaPlayer(video)
                    player.isAutoplay = true
                    CN.callSerially {
                        videoForm.removeAll()
                        videoForm.layout = BorderLayout()
                        videoForm.add(BorderLayout.CENTER, player)
                        videoForm.revalidate()
                    }
                }
            } catch (error: IOException) {
                Log.e(error)
                ToastBar.showErrorMessage("Error loading video")
            }
        }
    }

    private fun downloadFile(url: String) {
        val cr = ConnectionRequest()
        cr.isPost = false
        cr.isFailSilently = true
        cr.isReadResponseForErrors = false
        cr.isDuplicateSupported = true
        cr.url = url
        cr.destinationFile = DOWNLOADED_VIDEO
        ToastBar.showConnectionProgress("Downloading", cr, null, null)
        NetworkManager.getInstance().addToQueue(cr)
    }

    private fun createVideoComponent(firstLine: String, secondLine: String, icon: Image, listener: (ActionEvent?)-> Unit): Component {
        val videoComponent = MultiButton(firstLine)
        videoComponent.textLine2 = secondLine
        videoComponent.uiid = "VideoComponent"
        videoComponent.icon = icon
        videoComponent.iconPosition = "East"
        videoComponent.addActionListener(listener)
        videoComponent.uiidLine1 = "MediaComponentLine1"
        videoComponent.uiidLine2 = "MediaComponentLine2"
        return videoComponent
    }

    companion object {
        private val CAPTURED_VIDEO = FileSystemStorage.getInstance().appHomePath + "captured.mp4"
        private val DOWNLOADED_VIDEO = FileSystemStorage.getInstance().appHomePath + "hello-codenameone.mp4"
    }

    init {
        init("Media", Resources.getGlobalResources().getImage("media-demo-icon.png"), parentForm,
                "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/MediaDemo.java")
    }
}