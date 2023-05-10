import java.awt.Component
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.*
import javax.swing.*
//é possivel que isto possa ser dividido num controller
interface EditViewObserver {
    fun addProperty(key: String) { }
    fun removeProperty(key: String){ }
    fun propertyModified(key: String, newValue: JsonValue){ }
    fun addObject(key: String) { }
}
class EditorView(private val model: JsonObject): JPanel() {
    private val observers: MutableList<EditViewObserver> = mutableListOf()

    fun addObserver(observer: EditViewObserver) = observers.add(observer)
    init{
        model.addObserver(object: JsonObjectObserver{
            override fun addProperty(key: String) {
                addProperty(key)
            }

            override fun removeProperty(key: String) {
                //remove widget
            }

            override fun propertyModified(key: String, newValue: JsonValue) {
                //modifica propriedade do widget
            }

            override fun addObject(key: String) {
                //adiciona um widget nested
            }
        })
    testPanel()
    }

    fun addProperty(key: String){
        add(testWidget(key, "N/A"))
        println("HI")
        revalidate()
        repaint()
    }
//esta parte pode fazer parte de uma view
private fun testPanel(): JPanel =
        this.apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            add(testWidget("A", "um"))
            add(testWidget("B", "dois"))
            add(testWidget("C", "tres"))
            // menu
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        val add = JButton("add")
                        add.addActionListener {
                            val text = JOptionPane.showInputDialog("text")
                            //this will call this class's addProperty, which is responsible for adding a new test widget
                            addProperty(text)
                            //add(testWidget(text, "?"))
                            menu.isVisible = false
                        }
                        val del = JButton("delete all")
                        del.addActionListener {
                            components.forEach {
                                remove(it)
                            }
                            menu.isVisible = false
                            revalidate()
                            repaint()
                        }
                        menu.add(add);
                        menu.add(del)
                        menu.show(this@apply, 100, 100);
                    }
                }
            })
        }


    fun testWidget(key: String, value: String): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            add(JLabel(key))
            val text = JTextField(value)
            text.addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent) {
                    println("perdeu foco: ${text.text}")
                }
            })
            add(text)
        }
}