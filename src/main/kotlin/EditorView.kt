import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.event.*
import javax.swing.*

interface EditorViewObserver {
    fun addItem(key: String, value: JsonValue) { }
    fun removeItem(key: String){ }
    fun modifyItem(key: String, newValue: String, oldValue: String){ }
}
    class JsonObjectPanel(val model: JsonObjectBuilder) : JPanel() {
        private val observers: MutableList<EditorViewObserver> = mutableListOf()
        // CHANGED TO JPANEL TO ALLOW NESTED OBJECT AND ARRAY val nestedPanels: MutableMap<String, JsonObjectPanel> = mutableMapOf()
        val nestedPanels: MutableMap<String, JPanel> = mutableMapOf()
        fun addObserver(observer: EditorViewObserver) = observers.add(observer)
        init {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT
            background = Color.RED
            border =  BorderFactory.createLineBorder(Color.BLACK, 5)
            maximumSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)

            //INITIALIZER
            model.data.forEach{
                propertyAdded(it.key, it.value)
            }

            // menu
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        val add = JButton("add")
                        add.addActionListener {
                            val text = JOptionPane.showInputDialog("text")
                            observers.forEach {
                                it.addItem(text, JsonNull())
                            }
                            menu.isVisible = false
                        }
                        menu.add(add)
                        menu.show(this@JsonObjectPanel, 100, 100)
                    }

                }
            })
            model.addObserver(object: JsonBuilderObserver{
                override fun addItem(key: String, value: JsonValue) {
                    propertyAdded(key, value)
                }

                override fun removeItem(key: String) {
                    propertyRemoved(key)
                }

                override fun modifyItem(key: String, newValue: String, oldValue: String) {
                    propertyModified(key, newValue, oldValue)
                }
            })

        }
        fun propertyAdded(key: String, value: JsonValue){
            when (value) {
                is JsonNull -> add(JsonObjectProperty(key, "N/A"))
                is JsonObject -> {
                    add(JsonObjectProperty(key, ":"))
                    createNestedPanel(key, ":", this, value)
                }

                is JsonArray -> {
                    add(JsonObjectProperty(key, ""))
                    createNestedPanel(key, "", this, value)
                }

                else -> add(JsonObjectProperty(key, value.getJsonValue().toString()))
            }
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
            if(nestedPanels.containsKey(key)){
                remove(nestedPanels[key])
                nestedPanels.remove(key)
            }
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
                        it.modifyItem(label.text, textField.text, value)
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
                                it.removeItem(key)
                            }
                        }
                        //menu.add(add)
                        menu.add(deleteSelected)
                        menu.show(this@JsonObjectProperty, 100, 100)
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
    }

}
class JsonArrayPanel(val model: JsonArrayBuilder, val parentPanel: JPanel) : JPanel() {
    private val observers: MutableList<EditorViewObserver> = mutableListOf()
    val nestedPanels: MutableMap<String, JPanel> = mutableMapOf()
    var keys = if(model.lastIndex != -1) model.lastIndex+1 else 1
    fun getAssociatedModel()= model
    fun addObserver(observer: EditorViewObserver) = observers.add(observer)

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        alignmentX = Component.LEFT_ALIGNMENT
        alignmentY = Component.TOP_ALIGNMENT
        background = Color.ORANGE
        border = BorderFactory.createLineBorder(Color.BLACK, 5)
        maximumSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)

        //INITIALIZER
        model.data.forEach{
            propertyAdded(it.key, it.value)
        }

        // menu
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    val menu = JPopupMenu("Message")
                    val add = JButton("add")
                    add.addActionListener {
                        observers.forEach {
                            it.addItem(keys.toString(), JsonNull())
                            keys++
                        }
                        menu.isVisible = false
                    }
                    menu.add(add)
                    menu.show(this@JsonArrayPanel, 100, 100)
                }

            }
        })
        model.addObserver(object : JsonBuilderObserver {
            override fun addItem(key: String, value: JsonValue) {
                propertyAdded(key, value)
            }

            override fun removeItem(key: String) {
                propertyRemoved(key)
            }

            override fun modifyItem(key: String, newValue: String, oldValue: String) {
                propertyModified(key, newValue, oldValue)
            }
        })

    }
    fun propertyAdded(key: String, value: JsonValue) {
        when (value) {
            is JsonNull -> add(JsonArrayProperty(key, "N/A"))
            is JsonObject -> {
                add(JsonArrayProperty(key, ":"))
                createNestedPanel(key, ":", this, value)
            }

            is JsonArray -> {
                add(JsonArrayProperty(key, ""))
                createNestedPanel(key, "", this, value)
            }

            else -> add(JsonArrayProperty(key, value.getJsonValue().toString()))
        }
        revalidate()
        repaint()
    }


    fun propertyRemoved(key:String) {
        val find = components.find { it is JsonArrayProperty && it.getKey() == key}
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
        if (nestedPanels.containsKey(key)) {
            remove(nestedPanels[key])
            nestedPanels.remove(key)
        }
        //ADDED TO UPDATE THE oldValue
        val properties = components.filterIsInstance<JsonArrayProperty>()

        val property = properties.find { it.getKey() == key }
        property?.setValue(newValue)
        createNestedPanel(key, newValue, this)
        revalidate()
        repaint()
    }

    inner class JsonArrayProperty(private val key: String, private var value: String) : JPanel() {

        private val textField: JTextField

        init {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT
            maximumSize = Dimension(150, 50)


            textField = JTextField(value)
            textField.addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent) {
                    observers.forEach {
                        it.modifyItem(key, textField.text, value)
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
                                it.removeItem(key)
                            }
                        }
                        menu.add(deleteSelected)
                        menu.show(this@JsonArrayProperty, 100, 100)
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
            textField.text = value
        }


    }

}