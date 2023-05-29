import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

//TODO
// EDITOR MUST SHOW CONTENTS OF A JSON                              DONE
// MUST BE ABLE TO EDIT VISIBLE VALUES                              DONE
// MUST BE ABLE TO ADD AND REMOVE PROPERTIES OF A JSON OBJECT       DONE
// MUST BE ABLE TO ADD AND REMOVE ELEMENTS OF A JSON ARRAY          DONE
// MUST HAVE A STACK TO PROVIDE UNDO                                DONE (se eu tiver um arr, que tiver coisas, se apagar e clicar undo ele vai adicionar um JsonNull, mas ele deveria de modificar para o que lá estava antes)
// ISSUES REGARDING THE TEXT AREA'S DEPTH COULD HAS SOMETHING TO DO WITH IT ONLY BEING UPDATED ON INIT, either change the model or change call "properties?.values?.updateDepth(depth)"

//TODO VOU TER QUE MUDAR O FUNCIONAMENTO DO EDITOR VIEW, DE FORMA A RESOLVER O PROBLEMA DE APAGAR UM ARR COM CONTEUDO, SE FIZER UNDO ELE CRIA UM JsonNull
// MUDAR O BOTAO DE UNDO PARA NAO SER TAO GRANDE

internal val model = JsonObjectBuilder()
internal val undoStack = mutableListOf<Command>()
fun main() {

    val frame = JFrame("JSON Object Editor").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = GridLayout(0, 2)
        size = Dimension(600, 600)

        val left = JPanel()
        left.layout = GridLayout()

        val editorView = JsonObjectPanel(model)//EditorView(model)

        editorView.addObserver(object : EditorViewObserver{
            override fun addItem(key: String) {
                //model.add(key)
                val addCmd = AddCommand(model, key)
                undoStack.add(addCmd)
                addCmd.run()
            }

            override fun removeItem(key: String) {
                //model.remove(key)
                val rmCmd = RemoveCommand(model, key)
                undoStack.add(rmCmd)
                rmCmd.run()
            }

            override fun modifyItem(key: String, newValue: String, oldValue: String) {
                //model.modify(key, newValue, oldValue)
                if (oldValue != newValue) {
                    val modCmd = ModifyCommand(model, key, newValue, oldValue)
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
        val undoButton = JButton("Undo")
        undoButton.addActionListener {
            println(undoStack.size)
            if (undoStack.isNotEmpty()) {
                val last = undoStack.removeLast()
                last.undo()
            }
        }
        add(undoButton)
        val right = JPanel()
        right.layout = GridLayout()
        val srcArea = TextAreaView(model)
        srcArea.tabSize = 2
        right.add(srcArea)
        add(right)
    }
    frame.isVisible = true
}
internal fun createNestedPanel(panelKey: String, newValue: String, parentJPanel: JPanel) {
    if (newValue == ":") {
        val newNestedModel = JsonObjectBuilder()
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
            override fun addItem(key: String) {
                //newNestedModel.add(key)
                val addCmd = AddCommand(newNestedModel, key)
                undoStack.add(addCmd)
                addCmd.run()
                model.refreshModel()
            }

            override fun removeItem(key: String) {
                //newNestedModel.remove(key)
                val rmCmd = RemoveCommand(newNestedModel, key)
                undoStack.add(rmCmd)
                rmCmd.run()
                model.refreshModel()
            }

            override fun modifyItem(key: String, newValue: String, oldValue:String) {
                //newNestedModel.modify(key, newValue, oldValue)
                if (oldValue != newValue) {
                    val modCmd = ModifyCommand(newNestedModel, key, newValue, oldValue)
                    undoStack.add(modCmd)
                    modCmd.run()
                    model.refreshModel()
                }
            }
        })
        parentJPanel.add(newNestedPanel)
    }
    if(newValue == ""){
        val newNestedModel = JsonArrayBuilder()
        val newNestedPanel = JsonArrayPanel(newNestedModel, parentJPanel)


        if(parentJPanel is JsonObjectPanel){
            parentJPanel.model.data[panelKey] = newNestedModel.jsonData
            parentJPanel.nestedPanels[panelKey] = newNestedPanel
        }else if(parentJPanel is JsonArrayPanel){
            parentJPanel.model.data[panelKey] = newNestedModel.jsonData
            parentJPanel.nestedPanels[panelKey] = newNestedPanel
        }

        newNestedPanel.addObserver(object : EditorViewObserver {
            override fun addItem(key: String) {
                //println("2 CONTROLLER")
                //newNestedModel.add(key)
                val addCmd = AddCommand(newNestedModel, key)
                undoStack.add(addCmd)
                addCmd.run()
                updateParentAndRefreshModel(parentJPanel, panelKey, newNestedModel, model)
            }

            override fun removeItem(key: String) {
                //newNestedModel.remove(key)
                val rmCmd = RemoveCommand(newNestedModel, key)
                undoStack.add(rmCmd)
                rmCmd.run()
                updateParentAndRefreshModel(parentJPanel, panelKey, newNestedModel, model)
            }

            override fun modifyItem(key: String, newValue: String, oldValue: String) {
                //newNestedModel.modify(key, newValue, oldValue)
                if (oldValue != newValue) {
                    val modCmd = ModifyCommand(newNestedModel, key, newValue, oldValue)
                    undoStack.add(modCmd)
                    modCmd.run()
                    updateParentAndRefreshModel(parentJPanel, panelKey, newNestedModel, model)
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

class AddCommand(val model: JsonBuilder, val key: String):Command{
    override fun run() {
        model.add(key)
    }

    override fun undo() {
        model.remove(key)
    }

}
class RemoveCommand(val model: JsonBuilder, val key: String):Command{
    override fun run() {
        model.remove(key)
        //TODO ELE TEM QUE GUARDAR O QUE ESTIVER ASSOCIADO A ESTA CHAVE
    }

    override fun undo() {
        model.add(key)
        //TODO SE O QUE ESTIVER ASSOCIADO A ESTA CHAVE FOR != JsonNull ele adiciona, senao ele tem que chamar o modify para colocar o conteudo correto
    }

}
class ModifyCommand(val model: JsonBuilder, val key: String, val newValue: String, val oldValue: String):Command{
    override fun run() {
        model.modify(key, newValue, oldValue)
    }

    override fun undo() {
        model.modify(key, oldValue, newValue)
    }

}