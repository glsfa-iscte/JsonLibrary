import java.awt.Component
import java.awt.event.*
import javax.swing.*
//TODO perhaps move add if its a JsonObject and remove function to inside the widget and not in the panel, so that i can provide the correct widget to be removed
interface EditViewObserver {
    fun addProperty(key: String) { }
    fun removeProperty(key: String){ }
    fun modifyProperty(key: String, newValue: JsonValue){ }
    fun addObject(key: String){ }
}
class EditorView(private val model: JsonObjectBuilder): JPanel() {
    private val observers: MutableList<EditViewObserver> = mutableListOf()

    fun addObserver(observer: EditViewObserver) = observers.add(observer)
    init{
        model.addObserver(object: JsonObjectObserver{
            //ATENCAO AOS NOMES JA QUE PODE LEVAR AO NÃO FUNCIONAMENTO
            override fun addProperty(key: String) {
                propertyAdded(key)
            }

            override fun removeProperty(key: String) {
                propertyRemoved(key)
            }

            override fun modifyProperty(key: String, newValue: JsonValue) {
                propertyModified(key, newValue)
            }

            override fun addObject(key: String) {
                println("3")
                propertyAdded(key)
            }
        })
    testPanel()
    }

    fun propertyAdded(key: String){
        add(testWidget(key, "N/A"))
        revalidate()
        repaint()
    }

    fun propertyRemoved(key: String){
        //REMOVE WIDGET NOT WORKING
        val find = components.find { it is JPanel && it.name == key }
        find?.let {
            remove(find)
        }
        revalidate()
        repaint()
        println("no find")
        //println(model.jsonData.toJsonString)
    }

    fun propertyModified(key: String, newValue: JsonValue) {
        println("${key} : ${newValue}")
    }
//esta parte pode fazer parte de uma view
private fun testPanel(): JPanel =
        this.apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            // menu
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        val add = JButton("add")
                        add.addActionListener {
                            val text = JOptionPane.showInputDialog("text")
                            //this will call this class's addProperty, which is responsible for adding a new test widget and will also warn every observer of it
                            if(text.endsWith(":")){
                                observers.forEach {
                                    it.addObject(text.substringBefore(":"))
                                }
                            } else {
                                observers.forEach {
                                    it.addProperty(text)
                                }
                            }
                            menu.isVisible = false
                        }

                        val deleteSelected = JButton("delete")
                        deleteSelected.addActionListener {
                            observers.forEach {
                                it.removeProperty("1")
                            }
                            println("Removed")
                        }


                        menu.add(add)
                        menu.add(deleteSelected)
                        menu.show(this@apply, 100, 100);
                    }

                }
            })
        }


    fun testWidget(key: String, value: String): JPanel =
        JPanel().apply {
            name = key
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT
            val label = JLabel(key)
            label.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        val clickedLabel = e.source as JLabel
                        if(model.jsonObjectList.containsKey(clickedLabel.text) && model.jsonData != model.jsonObjectList[clickedLabel.text]){
                            model.data = model.jsonObjectList[clickedLabel.text]?.properties as MutableMap<String, JsonValue>
                            model.jsonData = model.jsonObjectList[clickedLabel.text]!!

                        }
                        println("Clicked label text: ${clickedLabel.text}")
                    }
                }
            })
            add(label)
            val text = JTextField(value)
            text.addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent) {
                    if(text.text != "N/A") {
                        observers.forEach {
                            //TODO MOVE TO CONTROLLER OR MODEL
                            it.modifyProperty(label.text, instanciateJson(text.text))
                        }
                    }
                    println("perdeu foco: ${text.text}")
                }

                override fun focusGained(e: FocusEvent?) {
                    println("ganhou foco: ${text.text}")
                }
            })
            add(text)
        }
}