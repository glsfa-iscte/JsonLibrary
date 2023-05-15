import java.awt.Component
import java.awt.event.*
import javax.swing.*

interface EditViewObserver {
    fun addProperty(key: String, parentObjectKey: String) { }
    fun removeProperty(key: String){ }
    fun modifyProperty(key: String, newValue: JsonValue){ }
    fun addObject(key: String, widgetId: Int){ }
}
class EditorView(val model: JsonObjectBuilder): JPanel() {
    private val observers: MutableList<EditViewObserver> = mutableListOf()
    private var widgetID: Int = 1
    fun addObserver(observer: EditViewObserver) = observers.add(observer)
    init{
        model.addObserver(object: JsonObjectObserver{
            //ATENCAO AOS NOMES JA QUE PODE LEVAR AO NÃO FUNCIONAMENTO
            override fun addProperty(key: String, parentObjectKey: String) {
                propertyAdded(key)
            }

            override fun removeProperty(key: String) {
                propertyRemoved(key)
            }

            override fun modifyProperty(key: String, newValue: JsonValue) {
                propertyModified(key, newValue)
            }

            override fun addObject(key: String, widgetId: Int) {
                println("3")
                propertyAdded(key)
            }
        })
    testPanel()
    }

    fun propertyAdded(key: String){
        add(TestWidget(key, "N/A"))
        revalidate()
        repaint()
    }

    fun propertyRemoved(key: String){
        val find = components.find { it is TestWidget && it.getKey() == key}
        find?.let {
            remove(find)
        }
        revalidate()
        repaint()
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
                                    it.addObject(text.substringBefore(":"), widgetID)
                                }
                            } else {
                                observers.forEach {
                                    it.addProperty(text, "")
                                }
                            }
                            menu.isVisible = false
                        }

                        /*val deleteSelected = JButton("delete")
                        deleteSelected.addActionListener {
                            observers.forEach {
                                it.removeProperty("1")
                            }
                            println("Removed")
                        }

                         */
                        menu.add(add)
                        //menu.add(deleteSelected)
                        menu.show(this@apply, 100, 100);
                    }

                }
            })
        }


    inner class TestWidget(private val key: String, private var value: String) : JPanel() {

        private val label: JLabel
        private val textField: JTextField
        private val id:Int

        init {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            id = widgetID
            widgetID+=1

            label = JLabel(key)
            add(label)

            textField = JTextField(value)
            textField.addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent) {
                    if (textField.text != "N/A") {
                        observers.forEach {
                            // TODO PERHAPS MOVE TO CONTROLLER OR MODEL
                            it.modifyProperty(label.text, instanciateJson(textField.text))
                        }
                    }
                    println("perdeu foco: ${textField.text}")
                }
            })
            add(textField)

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
                                    println("0")
                                    //ELE INFORMA QUE VAI SER ADICIONADO UM OBJ NO WIDJET COM O ID X
                                    //id é do proprio id+1 vai ser o id do idget que vai ser criado que corresponde a um JsonObject
                                    it.addObject(text.substringBefore(":"), id+1)
                                }
                            } else {
                                observers.forEach {
                                    it.addProperty(text, key)
                                }
                            }
                            menu.isVisible = false
                        }

                        val deleteSelected = JButton("delete")
                        deleteSelected.addActionListener {
                            observers.forEach {
                                it.removeProperty(key)
                            }
                        }
                        menu.add(add)
                        menu.add(deleteSelected)
                        menu.show(this@TestWidget, 100, 100);
                    }
                }
            })

            println("NEW WIDGET ADDED ID:${id} -> ${key} : ${value}")
        }

        fun getKey(): String {
            return key
        }

        fun getValue(): String {
            return value
        }

        fun setValue(newValue: String) {
            value = newValue
            textField.text = value
        }

        fun getLabel(): JLabel {
            return label
        }

        fun getTextField(): JTextField {
            return textField
        }
    }

}