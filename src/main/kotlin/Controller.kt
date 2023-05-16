import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

//TODO
// EDITOR MUST SHOW CONTENTS OF A JSON                              DONE
// MUST BE ABLE TO EDIT VISIBLE VALUES                              DONE
// MUST BE ABLE TO ADD AND REMOVE PROPERTIES OF A JSON OBJECT       (IN ROOT JSONOBJEC)
// MUST BE ABLE TO ADD AND REMOVE ELEMENTS OF A JSON ARRAY
// MUST HAVE A STACK TO PROVIDE UNDO

fun main() {
    /*val inscricoes04 = JsonObject(mapOf(
        "uc" to JsonString("PA"),
        "ects" to JsonNumber(6.0),
        "data-exame" to JsonNull(),
        "inscritos" to JsonObject(mapOf(
                "numero" to JsonNumber(101101),
                "nome" to JsonString("Dave Farley"),
                "internacional" to JsonBoolean(true)
            ))
        )
    )

     */
    val model = JsonObjectBuilder()

    val frame = JFrame("Josue - JSON Object Editor").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = GridLayout(0, 2)
        size = Dimension(600, 600)

        val left = JPanel()
        left.layout = GridLayout()

        val editorView = JsonObjectPanel(model)//EditorView(model)

        editorView.addObserver(object : EditViewObserver{
            override fun addProperty(key: String) {
                println("1")
                model.addProperty(key)
            }

            override fun removeProperty(key: String) {
                model.removeProperty(key)
            }

            override fun modifyProperty(key: String, newValue: String) {
                model.modifyValue(key, newValue)
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