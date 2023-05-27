import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.event.*
import javax.swing.*


//TODO
// CREATE A PANEL FOR EACH JSON OBJECT AND THEN FOR EACH ARRAY      DONE
// USAR SO UMA INTERFACE PARA OS OBJETOS E PARA O ARRAY, NO MODEL E NA VIEW
interface EditViewObserver {
    fun addProperty(key: String) { }
    fun removeProperty(key: String){ }
    fun modifyProperty(key: String, newValue: String, oldValue: String){ }
}
    class JsonObjectPanel(val model: JsonObjectBuilder) : JPanel() {
        private val observers: MutableList<EditViewObserver> = mutableListOf()
        // CHANGED TO JPANEL TO ALLOW NESTED OBJECT AND ARRAY val nestedPanels: MutableMap<String, JsonObjectPanel> = mutableMapOf()
        val nestedPanels: MutableMap<String, JPanel> = mutableMapOf()
        //fun getJsonObjectPanelModel() = model
        fun addObserver(observer: EditViewObserver) = observers.add(observer)
        init {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
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
                                it.addProperty(text)
                            }
                            menu.isVisible = false
                        }
                        menu.add(add)
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

                override fun modifyProperty(key: String, newValue: String, oldValue: String) {
                    propertyModified(key, newValue, oldValue)
                }
            })

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
            if(nestedPanels.containsKey(key)){
                remove(nestedPanels[key])
                nestedPanels.remove(key)
            }
            revalidate()
            repaint()
        }

        fun propertyModified(key: String, newValue: String, oldValue: String) {
            //ADDED TO REMOVE NESTED PANELS IF THERE ARE ANY (If it changes from JsonObject to any other JsonValue it should remove the panel)
            if(nestedPanels.containsKey(key)){
                remove(nestedPanels[key])
                nestedPanels.remove(key)
            }
            //ADDED TO UPDATE THE oldValue
            println("FOUND KEY |${key}|, NEWVALUE |${newValue}| OLDVALUE |${oldValue}|")
            val properties = components.filterIsInstance<JsonObjectProperty>()
            val property = properties.find { it.getKey() == key }
            property?.setValue(newValue)
            createNestedPanel(key, newValue, this)
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
            maximumSize = Dimension(150, 50)

            label = JLabel(key)
            add(label)

            textField = JTextField(value)
            textField.addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent) {

                    observers.forEach {
                        it.modifyProperty(label.text, textField.text, value)
                    }
                }
            })
            add(textField)
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {

                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
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
            //textField.text = value
        }

        fun getLabel(): JLabel {
            return label
        }

        fun getTextField(): JTextField {
            return textField
        }
    }

}

interface JsonArrayEditorViewObserver {
    fun addValue(key: String) { }
    fun removeValue(key: String){ }
    fun modifyValue(key: String, newValue: String, oldValue: String){ }
}

class JsonArrayPanel(val model: JsonArrayBuilder) : JPanel() {
    private val observers: MutableList<JsonArrayEditorViewObserver> = mutableListOf()
    val nestedPanels: MutableMap<String, JPanel> = mutableMapOf()
    var keys = 1
    fun addObserver(observer: JsonArrayEditorViewObserver) = observers.add(observer)

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        alignmentX = Component.LEFT_ALIGNMENT
        alignmentY = Component.TOP_ALIGNMENT
        background = Color.ORANGE
        border = BorderFactory.createLineBorder(Color.BLACK, 5)
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
                            println("1 CLICK ADD VIEW")
                            it.addValue(text)
                        }
                        menu.isVisible = false
                    }
                    menu.add(add)
                    menu.show(this@JsonArrayPanel, 100, 100);
                }

            }
        })
        model.addObserver(object : JsonArrayObserver {
            override fun addValue(key: String) {
                propertyAdded(key)
            }

            override fun removeValue(key: String) {
                propertyRemoved(key)
            }

            override fun modifyValue(key: String, newValue: String, oldValue: String) {
                propertyModified(key, newValue, oldValue)
            }
        })

    }
    //TODO ALTERAR ISTO E OUTRAS (JSON ARRAY OBSERVER) QUE RECEBEM "KEY" JA QUE DEVERIA SER VALUE
    fun propertyAdded(key: String) {
        println("4 UPDATE VIEW")
        println("ARR PROPERTY ADDED: |${keys.toString()}| |${key}|")
        add(JsonArrayProperty(keys.toString(), key))
        keys++
        revalidate()
        repaint()
    }


    fun propertyRemoved(value: String) {
        println("VIEW REMOVING VALUE |${value}|")
        //val find = components.find { it is JsonArrayProperty && it.getKey() == key }
        val find = components.find { it is JsonArrayProperty && it.getValue()==value }
        val key = (find as JsonArrayProperty).getKey()
        println("ASSOCIATED KEY |${key}|")
        remove(find)
        if (nestedPanels.containsKey(key)) {
            remove(nestedPanels[key])
            nestedPanels.remove(key)
        }
        revalidate()
        repaint()
    }

    fun propertyModified(key: String, newValue: String, oldValue: String) {
        //ADDED TO REMOVE NESTED PANELS IF THERE ARE ANY (If it changes from JsonObject to any other JsonValue it should remove the panel)
        if (nestedPanels.containsKey(key)) {
            remove(nestedPanels[key])
            nestedPanels.remove(key)
        }
        //ADDED TO UPDATE THE oldValue
        val properties = components.filterIsInstance<JsonArrayProperty>()

        val property = properties.find { it.getKey() == key }
        println("FOUND KEY |${key}|, NEWVALUE |${newValue}| OLDVALUE |${oldValue}|")
        property?.setValue(newValue)
        createNestedPanel(key, newValue, this)
        revalidate()
        repaint()
    }

    inner class JsonArrayProperty(private val key: String, private var value: String) : JPanel() {

        private val textField: JTextField

        init {
            //println("CREATING ARR PROPERTY: |${key}| |${value}|")
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT
            maximumSize = Dimension(150, 50)


            textField = JTextField(value)
            textField.addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent) {
                    observers.forEach {
                        println("MODIFYING TO: |${key}| |${textField.text}| |${value}|")
                        it.modifyValue(key, textField.text, value)
                    }
                }
            })
            //MOCED REMOVE FUNCTION TO TEXTFIELD
            textField.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {

                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        val deleteSelected = JButton("delete")
                        deleteSelected.addActionListener {
                            observers.forEach {
                                println("CLICKED TO REMOVE KEY: ${key} VALUE: |${value}|")
                                //TODO ele vai ter que mandar mais alguma coisa de forma a informar qual é o painel associado
                                println("HAS ASSOCIATED PANEL |${nestedPanels.containsKey(key)}| |${(nestedPanels[key] as JsonArrayPanel).model.jsonData}|")
                                //TODO MUDAR O REMOVE VALUE DE FORMA A MANDAR nestedPanels[key], PARA O MODELO RECEBER E APAGAR ESSE PAINEL
                                // ELE TERÁ QUE DEPOIS VAI TER QUE IR BUSCAR O JSONDATA, CONFORME ESTA EM CIMA E IR APAGAR O QUE É IGUAL
                                it.removeValue(value)
                            }
                        }
                        //menu.add(add)
                        menu.add(deleteSelected)
                        menu.show(this@JsonArrayProperty, 100, 100);
                    }
                }
            })
            add(textField)

        }

        fun getKey(): String {
            return key
        }

        fun getValue(): String {
            return value
        }

        fun setValue(newValue: String) {
            value = newValue
            //textField.text = value
        }

        fun getTextField(): JTextField {
            return textField
        }
    }

}