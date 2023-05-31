import javax.swing.JTextArea

class TextAreaView(private val model: JsonObjectBuilder): JTextArea(){
    init{
        text = model.jsonData.toJsonString
        model.addObserver(object: JsonBuilderObserver{
            override fun addItem(key: String, value: JsonValue) {
                text = model.jsonData.toJsonString
                println("Added "+model.data)
            }

            override fun removeItem(key: String) {
                text = model.jsonData.toJsonString
                println("Removed "+model.data)
            }

            override fun modifyItem(key: String, newValue: String, oldValue: String, associatedValue: JsonValue?) {
                text = model.jsonData.toJsonString
                println("Modified "+model.data)
            }

            override fun refreshModel() {
                text = model.jsonData.toJsonString
                println("Refreshed "+model.data)
            }
        })
    }
}