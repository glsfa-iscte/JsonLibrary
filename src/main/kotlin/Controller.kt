import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane

//TODO
// EDITOR MUST SHOW CONTENTS OF A JSON                              DONE
// MUST BE ABLE TO EDIT VISIBLE VALUES                              DONE
// MUST BE ABLE TO ADD AND REMOVE PROPERTIES OF A JSON OBJECT       DONE
// MUST BE ABLE TO ADD AND REMOVE ELEMENTS OF A JSON ARRAY
// MUST HAVE A STACK TO PROVIDE UNDO
// ISSUES REGARDING THE TEXT AREA'S DEPTH COULD HAS SOMETHING TO DO WITH IT ONLY BEING UPDATED ON INIT, either change the model or change call "properties?.values?.updateDepth(depth)"
// NESTED ARRAY CANT PROPAGATE CHANGES TO PARENT, IT ONLY HAS THE INITIAL REFERENCE TO IT, SO THE ARR REMAINS EMPTY


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
//TODO se ele criar um painel, o que eu tenho que fazer é, o parentJPanel vai ter que escutar se o modelo do JsonArray filho mudou, se mudar ele tem que chamar o JsonData, de forma a ser recalculado
// Tera que ser tambem adicionado ao JsonArrayPanel
// na parte que sao adicionados os observadores vai adicionar
fun createNestedPanel(panelKey: String, newValue: String, parentJPanel: JPanel) {
    //DEBUG
    if(parentJPanel is JsonObjectPanel){
        println("OBJ PARENT DATA: |${parentJPanel.model.data}|")
    }
    if(parentJPanel is JsonArrayPanel){
        println("ARR PARENT DATA: |${parentJPanel.model.data}|")
    }
    if (newValue == ":") {
        val newNestedModel = JsonObjectBuilder()
        val newNestedPanel = JsonObjectPanel(newNestedModel)
        //ISTO TRATA DE LIGAR O NESTED AO PAI
        if(parentJPanel is JsonObjectPanel){
            parentJPanel.model.data.put(panelKey, newNestedModel.jsonData)
            parentJPanel.nestedPanels.put(panelKey, newNestedPanel)
        }else{
            if(parentJPanel is JsonArrayPanel){
                parentJPanel.model.data.put(panelKey, newNestedModel.jsonData)
                parentJPanel.nestedPanels.put(panelKey, newNestedPanel)
            }
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
    if(newValue == ""){
        println("1 CREATED ARR")
        val newNestedModel = JsonArrayBuilder()
        val newNestedPanel = JsonArrayPanel(newNestedModel)


        if(parentJPanel is JsonObjectPanel){
            parentJPanel.model.data.put(panelKey, newNestedModel.jsonData)
            parentJPanel.nestedPanels.put(panelKey, newNestedPanel)
        }else{
            if(parentJPanel is JsonArrayPanel){
                parentJPanel.model.data.put(panelKey, newNestedModel.jsonData)
                parentJPanel.nestedPanels.put(panelKey, newNestedPanel)
            }
        }
        //MUST CHECK LISTENERS TO UPDATE
        // newNestedModel.addParentObserver(object: JsonModelListener{

        //})
        // Add observers to the new panel
        newNestedPanel.addObserver(object : JsonArrayEditorViewObserver {
            override fun addValue(key: String) {
                println("2 CONTROLLER")
                newNestedModel.addValue(key)
                if(parentJPanel is JsonObjectPanel)
                    parentJPanel.model.data.put(panelKey, newNestedModel.jsonData)
                else
                    if(parentJPanel is JsonArrayPanel)
                        parentJPanel.model.data.put(panelKey, newNestedModel.jsonData)
                model.refreshModel()
            }

            override fun removeValue(key: String) {
                newNestedModel.removeValue(key)
                if(parentJPanel is JsonObjectPanel)
                    parentJPanel.model.data.put(panelKey, newNestedModel.jsonData)
                else
                    if(parentJPanel is JsonArrayPanel)
                        parentJPanel.model.data.put(panelKey, newNestedModel.jsonData)
                model.refreshModel()
            }

            override fun modifyValue(key: String, newValue: String, oldValue:String) {
                newNestedModel.modifyValue(key, newValue, oldValue)
                if(parentJPanel is JsonObjectPanel)
                    parentJPanel.model.data.put(panelKey, newNestedModel.jsonData)
                else
                    if(parentJPanel is JsonArrayPanel) {
                        parentJPanel.model.data.put(panelKey, newNestedModel.jsonData)
                        println("CONTROLLER ARR MOD |${parentJPanel.model.data}| |${newNestedModel.jsonData}|")
                        println("IT WASNT MODIFIED DUE TO CALCULATED PROPERTY MODEL DATA: |${model.jsonData}|")
                    }
                model.refreshModel()
            }
        })
        parentJPanel.add(newNestedPanel)

    }
}


