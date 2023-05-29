import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane

//TODO
// EDITOR MUST SHOW CONTENTS OF A JSON                              DONE
// MUST BE ABLE TO EDIT VISIBLE VALUES                              DONE
// MUST BE ABLE TO ADD AND REMOVE PROPERTIES OF A JSON OBJECT       DONE
// MUST BE ABLE TO ADD AND REMOVE ELEMENTS OF A JSON ARRAY          DONE
// MUST HAVE A STACK TO PROVIDE UNDO
// ISSUES REGARDING THE TEXT AREA'S DEPTH COULD HAS SOMETHING TO DO WITH IT ONLY BEING UPDATED ON INIT, either change the model or change call "properties?.values?.updateDepth(depth)"
// PREVENT JSONOBJECT TO HAVE DUPLICATE KEY (added check in the model)  DONE

internal val model = JsonObjectBuilder()

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
                model.addProperty(key)
            }

            override fun removeItem(key: String) {
                model.removeProperty(key)
            }

            override fun modifyItem(key: String, newValue: String, oldValue: String) {
                model.modifyValue(key, newValue, oldValue)
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
                newNestedModel.addProperty(key)
                model.refreshModel()
            }

            override fun removeItem(key: String) {
                newNestedModel.removeProperty(key)
                model.refreshModel()
            }

            override fun modifyItem(key: String, newValue: String, oldValue:String) {
                newNestedModel.modifyValue(key, newValue, oldValue)
                model.refreshModel()
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
                newNestedModel.addValue(key)
                updateParentAndRefreshModel(parentJPanel, panelKey, newNestedModel, model)
            }

            override fun removeItem(key: String) {
                newNestedModel.removeValue(key)
                updateParentAndRefreshModel(parentJPanel, panelKey, newNestedModel, model)
            }

            override fun modifyItem(key: String, newValue: String, oldValue: String) {
                newNestedModel.modifyValue(key, newValue, oldValue)
                updateParentAndRefreshModel(parentJPanel, panelKey, newNestedModel, model)
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
class AddCommand(val model: JsonObjectBuilder)