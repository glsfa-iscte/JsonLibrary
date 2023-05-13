import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

//TODO
// EDITOR MUST SHOW CONTENTS OF A JSON
// MUST BE ABLE TO EDIT VISIBLE VALUES
// MUST BE ABLE TO ADD AND REMOVE PROPERTIES OF A JSON OBJECT
// MUST BE ABLE TO ADD AND REMOVE ELEMENTS OF A JSON ARRAY
// MUST HAVE A STACK TO PROVIDE UNDO

fun main() {
    val model = JsonObjectBuilder()

    val frame = JFrame("Josue - JSON Object Editor").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = GridLayout(0, 2)
        size = Dimension(600, 600)

        val left = JPanel()
        left.layout = GridLayout()

        val editorView = EditorView(model)
        editorView.addObserver(object : EditViewObserver{
            override fun addProperty(key: String) {
                model.addProperty(key)
            }

            override fun removeProperty(key: String) {
                model.removeProperty(key)
            }

            override fun addObject(key: String) {
                println("2")
                model.addObject(key)
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
        val srcArea = JTextArea()
        srcArea.tabSize = 2
        srcArea.text = "TODO"
        right.add(srcArea)
        add(right)
    }
    frame.isVisible = true
}