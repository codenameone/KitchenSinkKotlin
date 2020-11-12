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
import com.codename1.ui.Calendar
import com.codename1.ui.geom.Dimension
import com.codename1.ui.layouts.BorderLayout
import com.codename1.ui.layouts.BoxLayout
import com.codename1.ui.layouts.FlowLayout
import com.codename1.ui.plaf.UIManager
import com.codename1.ui.util.Resources
import java.util.*

/**
 * Class that demonstrate the usage of the BrowserComponent, SignatureComponent, Calendar, FileTree and ImageViewer components.
 *
 * @author Sergey Gerashenko.
 */
class AdvancedDemo(parentForm: Form) : Demo() {
    private val allNotes = HashMap<String, MutableList<String>>()

    override fun createContentPane(): Container? {
        val demoContainer = Container(BoxLayout(BoxLayout.Y_AXIS), "DemoContainer")
        demoContainer.isScrollableY = true

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("advanced-browser.png"),
                "Browser Component",
                "The browser component is an interface",
                """
                    to an embeddable native platform browser on platforms that support embedding the native browser in place, if you need wide compatibility and flexibility you should check out the HTML Component which provides a lightweight 100% cross platform web component.
                    
                    This component will only work on platforms that support embedding a native browser which exclude earlier versions of Blackberry devices and J2ME devices.
                    
                    It's recommended that you place this component in a fixed position (none scrollable) on the screen without other focusable components to prevent confusion between focus authority and allow the component to scroll itself rather than CodenameOne making that decision for it.
                    
                    On Android this component might show a native progress indicator dialog. You can disable that functionality using the call.
                    """.trimIndent()
                    ) {
            val browser = BrowserComponent()
                        browser.url = "https://www.codenameone.com/"
                        Display.getInstance().setProperty("WebLoadingHidden", "true")
                        showDemo("Browser", browser) })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("advanced-signature.png"),
                "Signature Component",
                "A component to allow a user to enter",
                """
                    their signature. This is just a button that, when pressed, will pop up a dialog where the user can draw their signature with their finger.
                    
                    The user is given the option to save/reset/cancel the signature. On save, the signature Image property will be set with a full-size of the signature, and the icon on the button will show a thumbnail of the image.
                    """.trimIndent()
        ) { showDemo("Signature", createSignatureDemo()) })


        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("advanced-calendar.png"),
                "Calendar",
                "Date widget for selecting a date/time value.",
                """To localize stings for month names use the values Calendar. Month using 3 first characters of the month name in the resource localization e.g. Calendar. Jan, Calendar.Feb etc …

                    To localize stings for day names use the values Calendar. Day in the resource localization e.g. "Calendar.Sunday", "Calendar.Monday" etc …
                    
                    Note that we recommend using the picker class which is superior when running on the device for most use cases."""
        ) { showDemo("Calendar", createCalendarDemo()) })


        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("advanced-tree-file.png"),
                "File Tree",
                "Simple class showing off the file system as",
                "a tree component."
        ) {
            val xmlTree = FileTree(FileTreeModel(true))
            val treeContainer = BorderLayout.center(xmlTree)
            val height = Display.getInstance().convertToPixels(4f)
            val width = Display.getInstance().convertToPixels(4f)
            FileTree.setFolderIcon(Resources.getGlobalResources().getImage("close-folder.png").scaled(width, height))
            FileTree.setFolderOpenIcon(Resources.getGlobalResources().getImage("open-folder.png").scaled(width, height))
            FileTree.setNodeIcon(Resources.getGlobalResources().getImage("file.png").scaled(width, height))

            // Refresh the root image.
            xmlTree.refreshNode((xmlTree.getComponentAt(0) as Container).getComponentAt(0))
            showDemo("File Tree", treeContainer)
        })

        demoContainer.add(createComponent(Resources.getGlobalResources().getImage("advanced-image-viewer.png"),
                "Image Viewer",
                "Image Viewer allows zooming/panning an",
                "image and potentially flicking between multiple images within a list of images"
        ) {
            val ip = InfiniteProgress().showInfiniteBlocking()
            CN.invokeAndBlock {
                val demo: Container? = ImageViewerDemo().createContentPane()
                CN.callSerially {
                    ip.dispose()
                    showDemo("Image Viewer", demo!!)
                    val status = ToastBar.getInstance().createStatus()
                    status.message = "Swipe right/left to browse the images"
                    status.setExpires(3500)
                    status.showDelayed(500)
                }
            }
        })

        return demoContainer
    }

    private fun createSignatureDemo(): Container {
        val demoContainer = Container(BoxLayout(BoxLayout.Y_AXIS))
        val costSummary = Container(BoxLayout(BoxLayout.Y_AXIS))
        costSummary.uiid = "CostSummary"
        costSummary.add(Label("Cost Summary", "CostSummaryLabel")).add(BorderLayout.centerEastWest(null, Label("$30.00", "SignatureCost"), Label("Subtotal", "SignatureLabel"))).add(BorderLayout.centerEastWest(null, Label("$5", "SignatureCost"), Label("Shipping", "SignatureLabel"))).add(BorderLayout.centerEastWest(null, Label("$3.00", "SignatureCost"), Label("Estimated Tax ", "SignatureLabel"))).add(Label(" ", "Separator")).add(BorderLayout.centerEastWest(null, Label("$38.00", "SignatureTotalCost"), Label("Total", "SignatureLabel")))

        val creditCard: ScaleImageLabel = object : ScaleImageLabel(Resources.getGlobalResources().getImage("credit-card.png")) {
            override fun calcPreferredSize(): Dimension {
                val d = super.calcPreferredSize()
                d.width = Display.getInstance().displayWidth
                d.height = d.width / 2
                return d
            }
        }

        creditCard.uiid = "CreditCard"
        val sig = SignatureComponent()
        val confirmAndPay = Button("Confirm & Pay", "SignatureConfirm")
        confirmAndPay.addActionListener {
            if (sig.signatureImage == null) {
                ToastBar.showInfoMessage("you need to sign")
            } else {
                ToastBar.showInfoMessage("purchase was successfully completed")
            }
        }

        val clear = Button("Clear", "SignatureClear")
        clear.addActionListener { sig.signatureImage = null }

        val confirmContainer = Container(BorderLayout())
        confirmContainer.uiid = "ConfirmContainer"
        confirmContainer.add(BorderLayout.NORTH, sig).add(BorderLayout.SOUTH, FlowLayout.encloseCenter(clear, confirmAndPay))
        demoContainer.addAll(costSummary, creditCard, confirmContainer)
        demoContainer.isScrollableY = true
        return demoContainer
    }

    private fun createCalendarDemo(): Container {
        val notes = Container(BoxLayout(BoxLayout.Y_AXIS))
        notes.add(createNote("Empty", null, notes))
        notes.isScrollableY = true

        val cld = Calendar()
        cld.selectedDaysUIID = "CalendarSelected"
        cld.addActionListener {
            notes.removeAll()
            var currentNotes = allNotes[cld.date.toString()]
            if (currentNotes == null) {
                currentNotes = ArrayList()
                allNotes[cld.date.toString()] = currentNotes
            }

            val notesCount = currentNotes.size
            if (notesCount == 0) {
                notes.add(createNote("Empty", currentNotes, notes))
            } else {
                for (note in currentNotes) {
                    notes.add(createNote(note, currentNotes, notes))
                }
            }
        }

        val addNote = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD, "CalendarAddNew")
        addNote.addActionListener {
            var currentNotes = allNotes[cld.date.toString()]
            if (currentNotes == null) {
                currentNotes = ArrayList<String>()
                allNotes[cld.date.toString()] = currentNotes as ArrayList<String>
            }

            val currNote = TextComponent().labelAndHint("Note")
            val ok = Command("Ok")
            val cancel = Command("Cancel")
            if (Dialog.show("Enter Note", currNote, ok, cancel) === ok && currNote.text.isNotEmpty()) {
                if (currentNotes!!.size == 0) {
                    notes.removeAll()
                }
                currentNotes!!.add(currNote.text)
                notes.add(createNote(currNote.text, currentNotes, notes))
                notes.revalidate()
            }
        }

        val demoContainer = BorderLayout.north(cld).add(BorderLayout.CENTER, notes)
        return addNote.bindFabToContainer(demoContainer)
    }

    private fun createNote(noteText: String, currNotes: MutableList<String>?, notes: Container): Component {
        val deleteButton = Button("", FontImage.createMaterial(FontImage.MATERIAL_DELETE, UIManager.getInstance().getComponentStyle("DeleteButton")), "DeleteButton")
        val noteTextLabel = SpanLabel(noteText, "Note")
        val note = SwipeableContainer(deleteButton, noteTextLabel)
        deleteButton.addActionListener {
            notes.removeComponent(note)
            notes.revalidate()
            currNotes?.remove(noteText)
        }
        return note
    }

    init {
        init("Advanced", Resources.getGlobalResources().getImage("advanced-icon.png"), parentForm, "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/AdvancedDemo.java")
    }
}