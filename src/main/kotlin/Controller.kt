import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

//TODO
// EDITOR MUST SHOW CONTENTS OF A JSON                              DONE
// MUST BE ABLE TO EDIT VISIBLE VALUES                              DONE
// MUST BE ABLE TO ADD AND REMOVE PROPERTIES OF A JSON OBJECT       DONE
// MUST BE ABLE TO ADD AND REMOVE ELEMENTS OF A JSON ARRAY
// MUST HAVE A STACK TO PROVIDE UNDO
// ISSUES REGARDING THE TEXT AREA'S DEPTH COULD HAS SOMETHING TO DO WITH IT ONLY BEING UPDATED ON INIT, either change the model or change call "properties?.values?.updateDepth(depth)"
// IF ITS A createNestedPanel IF THE VALUE OF THE PROPERTY THAT CREATED IT CHANGED, THE VIEW IS NOT REMOVING THE PANEL DONE
// CRIAR UM WIDGET PARA O ARRAY E UM PARA OS ELEMENTOS DO ARRAY
val model = JsonObjectBuilder()
fun main() {
    val frame = JFrame("Josue - JSON Object Editor").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = GridLayout(0, 2)
        size = Dimension(600, 600)

        val left = JPanel()
        left.layout = GridLayout()

        val editorView = JsonObjectPanel(model)//EditorView(model)

        editorView.addObserver(object : EditViewObserver{
            override fun addProperty(key: String) {
                model.addProperty(key)
            }

            override fun removeProperty(key: String) {
                model.removeProperty(key)
            }

            override fun modifyProperty(key: String, newValue: String, oldValue: String) {
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
fun createNestedPanel(key: String, newValue: String, parentJPanel: JPanel) {
    println("NEW VALUE RECEIVED${ newValue }")
    if (newValue == "{ }") {
        val newNestedModel = JsonObjectBuilder()
        val newNestedPanel = JsonObjectPanel(newNestedModel)
        //ISTO TRATA DE LIGAR O NESTED AO PAI
        if(parentJPanel is JsonObjectPanel){
            parentJPanel.model.data.put(key, newNestedModel.jsonData)
            parentJPanel.nestedPanels.put(key, newNestedPanel)
        }
        // Add observers to the new panel
        newNestedPanel.addObserver(object : EditViewObserver {
            override fun addProperty(key: String) {
                newNestedModel.addProperty(key)
                model.refreshModel()
            }

            override fun removeProperty(key: String) {
                newNestedModel.removeProperty(key)
                model.refreshModel()
            }

            override fun modifyProperty(key: String, newValue: String, oldValue:String) {
                newNestedModel.modifyValue(key, newValue, oldValue)
                model.refreshModel()
            }
        })
        parentJPanel.add(newNestedPanel)
    }
    if(newValue == "[ ]"){
        val newNestedModel = JsonArrayBuilder()
        val newNestedPanel = JsonArrayPanel(newNestedModel)
        if(parentJPanel is JsonObjectPanel){
            parentJPanel.model.data.put(key, newNestedModel.jsonData)
            parentJPanel.nestedPanels.put(key, newNestedPanel)
        }
        // Add observers to the new panel
        newNestedPanel.addObserver(object : JsonArrayEditorViewObserver {
            override fun addValue(key: String) {
                newNestedModel.addValue(key)
                model.refreshModel()
            }

            override fun removeValue(key: String) {
                newNestedModel.removeValue(key)
                model.refreshModel()
            }

            override fun modifyValue(key: String, newValue: String, oldValue:String) {
                newNestedModel.modifyValue(key, newValue, oldValue)
                model.refreshModel()
            }
        })
        parentJPanel.add(newNestedPanel)
    }
}
