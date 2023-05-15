import java.awt.Color
import java.awt.Component
import java.awt.event.*
import javax.swing.*
//TODO PASS A MODEL AND CHECK IF IT SHOWS UP FINE
// REMOVE ADDOBJECT SINCE ITS A JUST A PROPERY IN DISGUISE          DONE
// REMOVE WIDGET ID                                                 DONE
// modify PROPERTY ACHO QUE TINHA QUE TER REFERENCIA AO NOVO TIPO
// CREATE A PANEL FOR EACH JSON OBJECT AND THEN FOR EACH ARRAY      WORKING ON IT
interface EditViewObserver {
    fun addProperty(key: String) { }
    fun removeProperty(key: String){ }
    fun modifyProperty(key: String, newValue: JsonValue){ }
    //fun addObject(key: String, widgetId: Int){ }
}
class EditorView(val model: JsonObjectBuilder): JPanel() {
    private val observers: MutableList<EditViewObserver> = mutableListOf()
    fun addObserver(observer: EditViewObserver) = observers.add(observer)
    init{
        model.addObserver(object: JsonObjectObserver{
            override fun addProperty(key: String) {
                propertyAdded(key)
            }

            override fun removeProperty(key: String) {
                propertyRemoved(key)
            }

            override fun modifyProperty(key: String, newValue: JsonValue) {
                propertyModified(key, newValue)
            }

            /*override fun addObject(key: String, widgetId: Int) {
                println("3")
                propertyAdded(key)
            }

             */
        })
        add(JsonObjectPanel())
    }

    fun propertyAdded(key: String){
        add(JsonObjectProperty(key, "N/A"))
        revalidate()
        repaint()
    }

    fun propertyRemoved(key: String){
        val find = components.find { it is JsonObjectProperty && it.getKey() == key}
        find?.let {
            remove(find)
        }
        revalidate()
        repaint()
    }

    fun propertyModified(key: String, newValue: JsonValue) {
        //TODO CASO A NOVA PROPRIEDADE SEJA UM JSOOBJECT ELE VAI TER QUE CRIAR UM NOVO PAINEL
        println("${key} : ${newValue}")
    }
//este painel irá representar cada nível de um json object portanto, ao ser adicionado um novo objeto, ele cria um novo painel para o mesmo
private inner class JsonObjectPanel : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        alignmentX = Component.LEFT_ALIGNMENT
        alignmentY = Component.TOP_ALIGNMENT
        background = Color.RED

        // menu
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    val menu = JPopupMenu("Message")
                    val add = JButton("add")
                    add.addActionListener {
                        val text = JOptionPane.showInputDialog("text")
                        //this will call this class's addProperty, which is responsible for adding a new test widget and will also warn every observer of it
                        /*if(text.endsWith(":")){
                            observers.forEach {
                                it.addObject(text.substringBefore(":"), widgetID)
                            }
                        } else {
                       */     observers.forEach {
                        it.addProperty(text)
                    }
                        //}
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
                    menu.show(this@JsonObjectPanel, 100, 100);
                }

            }
        })
    }
}



    inner class JsonObjectProperty(private val key: String, private var value: String) : JPanel() {

        private val label: JLabel
        private val textField: JTextField
        init {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

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
                            /*if(text.endsWith(":")){
                                observers.forEach {
                                    println("0")
                                    //ELE INFORMA QUE VAI SER ADICIONADO UM OBJ NO WIDJET COM O ID X
                                    //id é do proprio id+1 vai ser o id do idget que vai ser criado que corresponde a um JsonObject
                                    it.addObject(text.substringBefore(":"), id+1)
                                }
                            } else {
                             */   observers.forEach {
                                    it.addProperty(text)
                                }
                            //}
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
                        menu.show(this@JsonObjectProperty, 100, 100);
                    }
                }
            })
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