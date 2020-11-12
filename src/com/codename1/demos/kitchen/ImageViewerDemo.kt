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

import com.codename1.components.ImageViewer
import com.codename1.components.SpanLabel
import com.codename1.components.ToastBar
import com.codename1.io.Storage
import com.codename1.io.rest.Rest
import com.codename1.ui.*
import com.codename1.ui.events.DataChangedListener
import com.codename1.ui.events.SelectionListener
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.list.ListModel
import com.codename1.ui.util.EventDispatcher
import com.codename1.ui.util.Resources
import java.util.*

class ImageViewerDemo {
    private var placeholder: EncodedImage? = null

    fun createContentPane(): Container? {
        val imageViewerContainer = Container(BorderLayout())
        if (placeholder == null) {
            val tempPlaceHolder = Resources.getGlobalResources().getImage("blurred-puppy.jpg")
            placeholder = EncodedImage.createFromImage(tempPlaceHolder, true)
        }

        // Request the data from the server.
        val resultData = Rest.get("https://www.codenameone.com/files/kitchensink/dogs/list.json").acceptJson().asJsonMap
        if (resultData.responseCode != 200) {
            CN.callSerially { ToastBar.showErrorMessage("Error code from the server") }
            return null
        }

        val itemListData = resultData.responseData

        // Make list of items
        val itemList: MutableList<Item> = ArrayList()
        for (currItemMap in itemListData["items"] as ArrayList<Map<String, Any>>) {
            val currItem = Item(currItemMap["title"] as String, currItemMap["details"] as String, currItemMap["url"] as String, currItemMap["thumb"] as String)
            itemList.add(currItem)
        }

        val firstItem = itemList[0]
        val model = ImageList(itemList, 0)
        val imageViewer = ImageViewer(model.getItemAt(0))
        imageViewer.imageList = model

        val details = SpanLabel(firstItem.details, "WebServicesDetails")
        imageViewerContainer.add(BorderLayout.SOUTH, details)
        imageViewerContainer.add(BorderLayout.CENTER, imageViewer)

        // Refresh the details when switching between the images.
        model.addSelectionListener { _: Int, newIndex: Int ->
            val currDetails = model.getDetails(newIndex)
            details.text = currDetails
            CN.getCurrentForm().revalidate()
        }
        return imageViewerContainer
    }

    /**
     * Image model for the ImageViewer
     */
    private inner class ImageList(private val itemList: List<Item?>, private var selection: Int) : ListModel<Image> {
        private val selectionListeners = EventDispatcher()

        override fun getItemAt(index: Int): Image {
            val item = itemList[index]
            val url = item?.url
            val fileName = url?.substring(url.lastIndexOf("/") + 1)
            val currImage: Image
            currImage = if (!CN.existsInStorage(CN.getAppHomePath() + fileName)) {
                URLImage.createToStorage(placeholder, fileName, url, URLImage.RESIZE_SCALE_TO_FILL)
            } else {
                Storage.getInstance().readObject(fileName) as Image
            }
            return currImage
        }

        override fun addSelectionListener(l: SelectionListener) {
            selectionListeners.addListener(l)
        }

        override fun removeSelectionListener(l: SelectionListener) {
            selectionListeners.removeListener(l)
        }

        override fun getSelectedIndex(): Int {
            return selection
        }

        override fun getSize(): Int {
            return itemList.size
        }

        override fun setSelectedIndex(index: Int) {
            val oldIndex = selection
            selection = index
            selectionListeners.fireSelectionEvent(oldIndex, selection)
        }

        fun getDetails(index: Int): String {
            return itemList[index]!!.details
        }

        override fun removeDataChangedListener(l: DataChangedListener) {}
        override fun addDataChangedListener(l: DataChangedListener) {}
        override fun addItem(item: Image) {}
        override fun removeItem(index: Int) {}
    }

    private data class Item(val title: String, val details: String, val url: String, val thumb: String)
}