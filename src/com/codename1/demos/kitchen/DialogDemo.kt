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
import com.codename1.contacts.Contact
import com.codename1.contacts.ContactsManager
import com.codename1.ui.*
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.layouts.FlowLayout
import com.codename1.ui.util.Resources
import java.util.*

/**
 * Class that demonstrate the usage of the InteractionDialog, Dialog, Sheet, and ToastBar Components.
 * The Dialog are Forms that occupies a part of the screen, and used mostly to display information and to get information from the user.
 *
 * @author Sergey Gerashenko.
 */
class DialogDemo(parentForm: Form) : Demo() {
    private var isInteractionDialogOpen = false
    private var roundMaskImage: Image? = null
    private val statusList: MutableList<ToastBar.Status> = ArrayList()

    override fun createContentPane(): Container? {
        val demoContainer = Container(BoxLayout(BoxLayout.Y_AXIS), "DemoContainer")
        demoContainer.isScrollableY = true

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("interaction-dialog.png"),
                "Interaction Dialog",
                "Unlike a regular dialog the interaction",
                "dialog only looks like a dialog, it resides in the layered pane and can be used to implement features " +
                        "where interaction with the background form is still required. Since this code is designed for interaction " +
                        "all \"dialogs\" created through there are modeless and never block."
                ) { showDemo("Interaction Dialog", createInteractionDialogDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("dialog.png"),
                "Dialog",
                "A dialog is a form that occupies a part of",
                    "the screen and appears as a modal entity to the developer. Dialogs allow us to prompt users for information and rely on the information being available on the next line after the show method." +
                    "Modality indicates that a dialog will block the calling thread even if the calling thread is the EDT. Notice that a dialog will not release the block until dispose is called " +
                     "even if show() from another form is called! Events are still performed thanks to the Display.invokeAnd Block(java.lang.Runnable) capability of the Display class." +
                    "To determine the size of the dialog use the show method that accepts 4 integer values, notice that these values accept margin from the four sides than x,y, width and height values!" +
                    "It's important to style a Dialog using getDialogStyle() or setDialogUIID(java.lang. String) methods rather than styling the dialog object directly." +
                    "The Dialog class also includes support for popup dialog which is a dialog type that is positioned text to a component or screen area and points an arrow at the location."
                ) { showDemo("Dialog", createDialogDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("sheet.png"),
                "Sheet",
                "A light-weight dialog that slides up from",
                "the bottom of the screen on mobile devices. Sheets include a \"title\" bar, with a back/close button, a title "+
                "label and a \"commands container\" (getCommandsContainer()) which allows you to insert your own custom "+
                "components (usually buttons) in the upper right. Custom content should be placed inside the content pane "+
                "which can be retrieved via getContentPane()\n\nUsage:\nThe general usage is to create new sheet instance "+
                "(or subclass), then call show() to make it appear over the current form. If a different sheet that is "+
                "currently being displayed, then calling show() will replace it."
                ) { showDemo("Sheet", createSheetDemo()) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("toast-bar.png"),
                "ToastBar",
                "An API to present status messages to the",
                "user in an unobtrusive manner. This is useful if there are background tasks that need to display " +
                "information to the user. E.g.p If a network request fails, of let the user know that \"Jobs are being " +
                "synchronized\""
                ) { showDemo("ToastBar", createToastBarDemo()) })

        return demoContainer
    }

    private fun createInteractionDialogDemo(): Container {
        val dlg = InteractionDialog("Header", BorderLayout())
        dlg.add(BorderLayout.CENTER, SpanLabel("Dialog body", "DialogDemoSpanLabel"))
        val openClose = Button("Open/Close", "DialogDemoButton")
        openClose.addActionListener {
            if (isInteractionDialogOpen) {
                dlg.dispose()
            } else {
                dlg.show(0, Display.getInstance().displayHeight / 2, 0, 0)
            }
            isInteractionDialogOpen = !isInteractionDialogOpen
        }
        return BorderLayout.south(openClose)
    }

    private fun createDialogDemo(): Container {
        val showDialog = Button("Show Dialog", "DialogDemoButton")
        val showPopup = Button("Show Popup", "DialogDemoButton")
        showDialog.addActionListener {
            val ok = Command("Ok")
            val cancel = Command("Cancel")
            val body = SpanLabel("This is the body of the popup", "DialogDemoSpanLabel")
            val commands = arrayOf(ok, cancel)
            Dialog.show("Dialog Title", body, commands, Dialog.TYPE_INFO, null, 0)
        }
        showPopup.addActionListener {
            val d = Dialog("Popup Title")
            val body = SpanLabel("This is the body of the popup", "DialogDemoSpanLabel")
            d.add(body)
            d.showPopupDialog(showPopup)
        }
        val demoContainer = BoxLayout.encloseY(showDialog, showPopup)
        demoContainer.uiid = "Wrapper"
        return BoxLayout.encloseY(demoContainer)
    }

    private fun createSheetDemo(): Container {
        val demoContainer = Container(BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE))

        // getAllContacts can take long time so we add infiniteProgress to modify user experience.
        demoContainer.add(BorderLayout.CENTER, InfiniteProgress())

        // Create new background Thread that will get all the contacts.
        CN.scheduleBackgroundTask {
            val contacts = Display.getInstance().getAllContacts(true, true, false, true, false, false)
            // Return to the EDT for edit the UI (the UI should be edited only within the EDT).
            if (contacts == null || contacts.isEmpty()) {
                CN.callSerially { parentForm!!.showBack() }
                return@scheduleBackgroundTask
            }
            val contactBox = Container(BoxLayout(BoxLayout.Y_AXIS))
            contactBox.isScrollableY = true
            for (currentContact in contacts) {
                contactBox.add(createContactComponent(currentContact))
            }
            CN.callSerially {
                demoContainer.removeAll()
                demoContainer.layout = BorderLayout()
                demoContainer.add(BorderLayout.CENTER, contactBox)
                demoContainer.revalidate()
                val status = ToastBar.getInstance().createStatus()
                status.message = "Click a contact to see the Sheet popup"
                status.setExpires(3500)
                status.showDelayed(500)
            }
        }
        return demoContainer
    }

    private fun createToastBarDemo(): Container {
        val basic = Button("Basic", "DialogDemoButton")
        val progress = Button("Progress", "DialogDemoButton")
        val expires = Button("Expires (after 3 seconds)", "DialogDemoButton")
        val delayed = Button("Delayed (by 1 second)", "DialogDemoButton")
        val clear = Button("Clear All", "DialogDemoButton")
        clear.addActionListener {
            for (currStatus in statusList) {
                currStatus.clear()
            }
            statusList.clear()
        }
        basic.addActionListener {
            val status = ToastBar.getInstance().createStatus()
            status.message = "Hello world"
            statusList.add(status)
            status.show()
        }
        progress.addActionListener {
            val status = ToastBar.getInstance().createStatus()
            status.message = "Hello world"
            status.isShowProgressIndicator = true
            statusList.add(status)
            status.show()
        }
        expires.addActionListener {
            val status = ToastBar.getInstance().createStatus()
            status.message = "Hello world"
            status.setExpires(3000) // only show the status for 3 seconds, then have it automatically clear
            status.show()
        }
        delayed.addActionListener {
            val status = ToastBar.getInstance().createStatus()
            status.message = "Hello world"
            statusList.add(status)
            status.showDelayed(1000) // Wait 1000 ms to show the status
        }
        return BoxLayout.encloseY(basic, progress, expires, delayed, clear)
    }

    private fun createContactComponent(contact: Contact): Component {
        val contactComponent = MultiButton(contact.displayName)
        contactComponent.uiid = "ContactComponent"
        var contactImage = contact.photo
        // Set default avatar for contacts without avatar picture.
        if (contactImage == null) {
            contactImage = Resources.getGlobalResources().getImage("default-contact-pic.jpg")
        }
        contactImage = contactImage!!.fill(CN.convertToPixels(8f), CN.convertToPixels(8f))
        contactImage = contactImage.applyMask(getRoundMask(contactImage.height))
        contactComponent.icon = contactImage
        contactComponent.addActionListener {
            val contactInfo = Sheet(null, contact.displayName)
            contactInfo.layout = BoxLayout(BoxLayout.Y_AXIS)
            contactInfo.add(Label("Phone: " + contact.primaryPhoneNumber, "ContactDetails"))
            if (contact.primaryEmail != null) {
                contactInfo.add(Label("Email: " + contact.primaryEmail, "ContactDetails"))
            }
            if (contact.birthday != 0L) {
                contactInfo.add(Label("Birthday: " + contact.birthday, "ContactDetails"))
            }
            val call = Button(FontImage.MATERIAL_CALL, 6f, "ContactsGreenButton")
            call.addActionListener { CN.dial(contact.primaryPhoneNumber) }
            val share = ShareButton()
            share.uiid = "ContactsGreenButton"
            share.setMaterialIcon(FontImage.MATERIAL_SHARE, 6f) // Change the size of the icon.
            share.textToShare = contact.displayName + " phone: " + contact.primaryPhoneNumber
            val delete = Button(FontImage.MATERIAL_DELETE, 6f, "ContactsRedButton")
            delete.addActionListener {
                if (Dialog.show("Delete", "This will delete the contact permanently!\nAre you sure?", "Delete", "Cancel")) {
                    if (contact.id != null) {
                        ContactsManager.deleteContact(contact.id)
                        contactComponent.remove()
                        Display.getInstance().current.revalidate()
                    }
                }
            }
            val contactActions = FlowLayout.encloseCenter(call, share, delete)
            contactActions.uiid = "contactActions"
            contactInfo.add(contactActions)
            contactInfo.show()
        }
        return contactComponent
    }

    init {
        init("Dialog", Resources.getGlobalResources().getImage("dialog-demo.png"), parentForm,
                "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/DialogDemo.java")
    }

    private fun getRoundMask(width: Int): Any {
        if (roundMaskImage == null) {
            roundMaskImage = Image.createImage(width, width, -0x1000000)
            val gr: Graphics? = roundMaskImage?.graphics
            gr!!.color = 0xffffff
            gr.isAntiAliased = true
            gr.fillArc(0, 0, width, width, 0, 360)
        } else {
            roundMaskImage = roundMaskImage?.fill(width, width)
        }
        return roundMaskImage!!.createMask()
    }
}