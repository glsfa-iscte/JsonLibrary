import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

//TODO ISSUES REGARDING THE TEXT AREA'S DEPTH COULD HAS SOMETHING TO DO WITH IT ONLY BEING UPDATED ON INIT, either change the model or change call "properties?.values?.updateDepth(depth)"
// MUDAR O BOTAO DE UNDO PARA NAO SER TAO GRANDE
// If i use the undo commands inside a JsonArray, The right side view is not warned somehow
// UNDO IS NOT WORKING FOR NESTED
internal val testJsonObj = JsonObject(mutableMapOf(
    "numero" to JsonNumber( 101101),
    "nome" to JsonString("Dave Farley"),
    "internacional" to JsonBoolean(true)
))

internal val insc01 = JsonObject(mapOf(
    "numero" to JsonNumber(101101),
    "nome" to JsonString("Dave Farley"),
    "internacional" to JsonBoolean(true)
))

internal val insc02 = JsonObject(mapOf(
    "numero" to JsonNumber(101102),
    "nome" to JsonString("Martin Fowler"),
    "internacional" to JsonBoolean(true)
))

internal val insc03 = JsonObject(mapOf(
    "numero" to JsonNumber(92888),
    "nome" to JsonString("Gustavo Ferreira"),
    "internacional" to JsonBoolean(false)
))

internal val inscritos = JsonArray(listOf(insc01, insc02, insc03))

internal val inscricoes01 = JsonObject(mapOf(
    "uc" to JsonString("PA"),
    "ects" to JsonNumber(6.0),
    "data-exame" to JsonNull(),
    "inscritos" to inscritos
))

internal val parentModel = JsonObjectBuilder(inscricoes01)//JsonObjectBuilder(testJsonObj)
internal val undoStack = mutableListOf<Command>()
fun main() {

    val frame = JFrame("JSON Object Editor").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = GridLayout(0, 2)
        size = Dimension(600, 600)

        val left = JPanel()
        left.layout = GridLayout()

        val editorView = JsonObjectPanel(parentModel)//EditorView(model)

        editorView.addObserver(object : EditorViewObserver{
            override fun addItem(key: String, value: JsonValue) {
                //model.add(key)
                val addCmd = AddCommand(parentModel, key, value)
                undoStack.add(addCmd)
                addCmd.run()
            }

            override fun removeItem(key: String) {
                //model.remove(key)
                val rmCmd = RemoveCommand(parentModel, key)
                undoStack.add(rmCmd)
                rmCmd.run()
            }

            override fun modifyItem(key: String, newValue: String, oldValue: String) {
                //model.modify(key, newValue, oldValue)
                if (oldValue != newValue) {
                    val modCmd = ModifyCommand(parentModel, key, newValue, oldValue)
                    undoStack.add(modCmd)
                    modCmd.run()
                }
            }
        })
        val scrollPane = JScrollPane(editorView).apply {
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        }
        left.add(scrollPane)
        add(left)


        val right = JPanel()
        right.layout = GridLayout()
        val srcArea = TextAreaView(parentModel)
        srcArea.tabSize = 2
        right.add(srcArea)
        add(right)

        val undoButton = JButton("Undo")
        undoButton.addActionListener {
            println(undoStack.size)
            if (undoStack.isNotEmpty()) {
                val last = undoStack.removeLast()
                last.undo()
            }
        }
        undoButton.maximumSize = Dimension(50, 50)
        right.add(undoButton)
    }
    frame.isVisible = true
}
internal fun createNestedPanel(panelKey: String, newValue: String, parentJPanel: JPanel, jsonStructure: JsonStructure? = null) {
    if (newValue == ":") {
        val newNestedModel = if(jsonStructure != null) JsonObjectBuilder(jsonStructure as JsonObject) else JsonObjectBuilder(JsonObject())
        val newNestedPanel = JsonObjectPanel(newNestedModel)
        //ISTO TRATA DE LIGAR O NESTED AO PAI
        if(parentJPanel is JsonObjectPanel){
            parentJPanel.model.data[panelKey] = newNestedModel.jsonData
            parentJPanel.nestedPanels[panelKey] = newNestedPanel
        }else if(parentJPanel is JsonArrayPanel){
            parentJPanel.model.data[panelKey] = newNestedModel.jsonData
            parentJPanel.nestedPanels[panelKey] = newNestedPanel
        }
        // Add observers to the new panel
        newNestedPanel.addObserver(object : EditorViewObserver {
            override fun addItem(key: String, value: JsonValue) {
                //newNestedModel.add(key)
                val addCmd = AddCommand(newNestedModel, key, value)
                undoStack.add(addCmd)
                addCmd.run()
                parentModel.refreshModel()
            }

            override fun removeItem(key: String) {
                //newNestedModel.remove(key)
                val rmCmd = RemoveCommand(newNestedModel, key)
                undoStack.add(rmCmd)
                rmCmd.run()
                parentModel.refreshModel()
            }

            override fun modifyItem(key: String, newValue: String, oldValue:String) {
                //newNestedModel.modify(key, newValue, oldValue)
                if (oldValue != newValue) {
                    val modCmd = ModifyCommand(newNestedModel, key, newValue, oldValue)
                    undoStack.add(modCmd)
                    modCmd.run()
                    parentModel.refreshModel()
                }
            }
        })
        parentJPanel.add(newNestedPanel)
    }
    if(newValue == ""){
        val newNestedModel = if(jsonStructure != null) JsonArrayBuilder(jsonStructure as JsonArray) else JsonArrayBuilder(JsonArray())
        val newNestedPanel = JsonArrayPanel(newNestedModel, parentJPanel)


        if(parentJPanel is JsonObjectPanel){
            parentJPanel.model.data[panelKey] = newNestedModel.jsonData
            parentJPanel.nestedPanels[panelKey] = newNestedPanel
        }else if(parentJPanel is JsonArrayPanel){
            parentJPanel.model.data[panelKey] = newNestedModel.jsonData
            parentJPanel.nestedPanels[panelKey] = newNestedPanel
        }

        newNestedPanel.addObserver(object : EditorViewObserver {
            override fun addItem(key: String, value: JsonValue) {
                //println("2 CONTROLLER")
                //newNestedModel.add(key)
                val addCmd = AddCommand(newNestedModel, key, value)
                undoStack.add(addCmd)
                addCmd.run()
                updateParentAndRefreshModel(parentJPanel, panelKey, newNestedModel, parentModel)
            }

            override fun removeItem(key: String) {
                //newNestedModel.remove(key)
                val rmCmd = RemoveCommand(newNestedModel, key)
                undoStack.add(rmCmd)
                rmCmd.run()
                updateParentAndRefreshModel(parentJPanel, panelKey, newNestedModel, parentModel)
            }

            override fun modifyItem(key: String, newValue: String, oldValue: String) {
                //newNestedModel.modify(key, newValue, oldValue)
                if (oldValue != newValue) {
                    val modCmd = ModifyCommand(newNestedModel, key, newValue, oldValue)
                    undoStack.add(modCmd)
                    modCmd.run()
                    updateParentAndRefreshModel(parentJPanel, panelKey, newNestedModel, parentModel)
                }
            }
        })
        parentJPanel.add(newNestedPanel)

    }
}

/**
 * Update parent reference updates the JsonArray references along the hierarquies, since JsonArray JsonData is declared as a calculated property,
 * even i data changes, unless its called, it won't reflect those changes
 * Recusively traverses up the hierarqui, updating that parent's model with the new addition/subtraction or modification of a JsonArrayProperty
 *
 * @param panelKey
 * @param parentJPanel
 * @param newNestedModel
 */
private fun updateParentReference(panelKey: String, parentJPanel: JPanel, newNestedModel: JsonArrayBuilder){
    if(parentJPanel is JsonObjectPanel){
        parentJPanel.model.data[panelKey] = newNestedModel.jsonData
        //println("OBJ MODEL ${parentJPanel.model.data}")
    }else{
        val parentArrPanel = (parentJPanel as JsonArrayPanel)
        //conects the child to the parent
        parentJPanel.model.data[panelKey] = newNestedModel.jsonData
        val gradParentPanel = parentArrPanel.parentPanel
        //TODO CHECK THIS KEY
        var grandparentePanelKey = "1"
        if(gradParentPanel is JsonObjectPanel)
            grandparentePanelKey = getKeyByPanel(parentArrPanel, gradParentPanel.nestedPanels)!!
        else
            if(gradParentPanel is JsonArrayPanel)
                grandparentePanelKey = getKeyByPanel(parentArrPanel, gradParentPanel.nestedPanels)!!

        //println("panelKey: |${panelKey}|, parentJPanel: |${parentJPanel}|, newNestedModel: |${newNestedModel}|")
        //println("grandparentePanelKey: |${grandparentePanelKey}|, parentModel: |${parentJPanel.model.data}|")
        updateParentReference(grandparentePanelKey, gradParentPanel, parentJPanel.getAssociatedModel())
    }
}

private fun getKeyByPanel(panel: JPanel, map: MutableMap<String, JPanel>): String? {
    for ((key, value) in map) {
        if (value == panel) {
            return key
        }
    }
    return null
}

private fun updateParentAndRefreshModel(parentJPanel: JPanel, panelKey: String, newNestedModel: JsonArrayBuilder, model: JsonObjectBuilder) {
    model.refreshModel()
    if (parentJPanel is JsonObjectPanel) {
        parentJPanel.model.data[panelKey] = newNestedModel.jsonData
    } else if (parentJPanel is JsonArrayPanel) {
        updateParentReference(panelKey, parentJPanel, newNestedModel)
    }
    model.refreshModel()
}

interface Command{
    fun run()
    fun undo()
}
//TODO UNDO STACK NOT WORKING FOR NESTED ELEMENTS, IT UPDATES THE VIEW BUT NOT THE MODEL, ITS BECAUSE WHEN IT RUNS, IT CALLS updateParentReference(panelKey, parentJPanel, newNestedModel),
// BUT WHEN I PRESS UNDO IT DOENST CALL IT, SO IT DOESNT PROPAGATE THE CHANGES TO THE PARENT
class AddCommand(val model: JsonBuilder, val key: String, val value: JsonValue):Command{
    override fun run() {
        model.add(key, value)
    }

    override fun undo() {
        model.remove(key)

    }

}
class RemoveCommand(val model: JsonBuilder, val key: String):Command{
    var associatedValue:JsonValue = JsonNull()
    override fun run() {
        if(model.data[key] != JsonNull())
            associatedValue = model.data[key]!!
        model.remove(key)
        //TODO ELE TEM QUE GUARDAR O QUE ESTIVER ASSOCIADO A ESTA CHAVE
    }

    override fun undo() {
        model.add(key, associatedValue)
        if(associatedValue != JsonNull()){
            model.data[key] = associatedValue
        }
        //TODO SE O QUE ESTIVER ASSOCIADO A ESTA CHAVE FOR != JsonNull ele adiciona, senao ele tem que chamar o modify para colocar o conteudo correto
    }

}
class ModifyCommand(val model: JsonBuilder, val key: String, val newValue: String, val oldValue: String):Command{
    override fun run() {
        println("RUN MOD model ${model.data} key ${key} newvalue ${newValue} oldvalue ${oldValue} ")
        model.modify(key, newValue, oldValue)

    }

    override fun undo() {
        println("RUN UNDO model ${model.data} key ${key} newvalue ${newValue} oldvalue ${oldValue} ")
        model.modify(key, oldValue, newValue)
    }

}