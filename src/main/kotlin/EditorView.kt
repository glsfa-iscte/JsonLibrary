import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
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
    fun modifyProperty(key: String, newValue: String){ }
}

/*class EditorView(val model: JsonObjectBuilder): JPanel() {
    private val observers: MutableList<EditViewObserver> = mutableListOf()
    fun addObserver(observer: EditViewObserver) = observers.add(observer)

    init {
        //val jsonObjectPanel = JsonObjectPanel()
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        alignmentX = Component.LEFT_ALIGNMENT
        alignmentY = Component.TOP_ALIGNMENT
        background = Color.BLUE
        add(JsonObjectPanel(model))
    }

}
 */

    //este painel irá representar cada nível de um json object portanto, ao ser adicionado um novo objeto, ele cria um novo painel para o mesmo
    //private inner
    class JsonObjectPanel(val model: JsonObjectBuilder) : JPanel() {
        private val observers: MutableList<EditViewObserver> = mutableListOf()
        fun addObserver(observer: EditViewObserver) = observers.add(observer)
        init {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)//FlowLayout(FlowLayout.LEFT, 10, 10) // set gap between components to 10 pixels
            //border = BorderFactory.createBevelBorder(1)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT
            background = Color.RED
            border =  BorderFactory.createLineBorder(Color.BLACK, 5)
            maximumSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)

            // menu
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        val add = JButton("add")
                        add.addActionListener {
                            val text = JOptionPane.showInputDialog("text")
                            observers.forEach {
                                println("3")
                                it.addProperty(text)
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
                        menu.show(this@JsonObjectPanel, 100, 100);
                    }

                }
            })
            model.addObserver(object: JsonObjectObserver{
                override fun addProperty(key: String) {
                    propertyAdded(key)
                }

                override fun removeProperty(key: String) {
                    propertyRemoved(key)
                }

                override fun modifyProperty(key: String, newValue: String) {
                    propertyModified(key, newValue)
                }
            })

        }
        fun propertyAdded(key: String){
            println("4")
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

        fun propertyModified(key: String, newValue: String) {
            //TODO CASO A NOVA PROPRIEDADE SEJA UM JSOOBJECT ELE VAI TER QUE CRIAR UM NOVO PAINEL
            // PASSAR ISTO PARA O CONTROLLER, CRIAR UMA FUNCAO QUE RECEBE UM PAINEL E ADICIONA
            if(newValue == "{ }"){
                val newNestedModel = JsonObjectBuilder()
                val newNestedPanel = JsonObjectPanel(newNestedModel)
                //newNestedPanel.background = Color.GRAY
                maximumSize = Dimension(Int.MAX_VALUE-50, Int.MAX_VALUE)

                // Add observers to the new panel
                newNestedPanel.addObserver(object : EditViewObserver {
                    override fun addProperty(key: String) {
                        newNestedModel.addProperty(key)
                    }

                    override fun removeProperty(key: String) {
                        newNestedModel.removeProperty(key)
                    }

                    override fun modifyProperty(key: String, newValue: String) {
                        newNestedModel.modifyValue(key, newValue)
                    }
                })
                add(newNestedPanel)
            }
            //propertyRemoved(key)
            //propertyAdded(key)
            println("${key} : ${newValue}")
            revalidate()
            repaint()
        }




    inner class JsonObjectProperty(private val key: String, private var value: String) : JPanel() {

        private val label: JLabel
        private val textField: JTextField
        init {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT
            //perhaps a label should have a size and the text field should also have one
            maximumSize = Dimension(150, 50)

            label = JLabel(key)
            add(label)

            textField = JTextField(value)
            textField.addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent) {
                    if (textField.text != "N/A") {
                        observers.forEach {
                            //assim ele vai estar SEMPRE a adicionar um JsonString
                            it.modifyProperty(label.text, textField.text)
                        }
                    }
                    println("perdeu foco: ${textField.text}")
                }
            })
            add(textField)
        //NAO FAZ SENTIDO O WIDGET TER UM ADD JÁ QUE SERÁ O PAINEL JSONOBJECT PANEL ONDE SE ADICIONA
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {

                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        /*
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


                         */
                        val deleteSelected = JButton("delete")
                        deleteSelected.addActionListener {
                            observers.forEach {
                                it.removeProperty(key)
                            }
                        }
                        //menu.add(add)
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