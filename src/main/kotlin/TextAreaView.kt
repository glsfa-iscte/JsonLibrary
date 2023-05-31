import javax.swing.JTextArea

class TextAreaView(private val model: JsonObjectBuilder): JTextArea(){
    init{
        text = model.jsonData.toJsonString
        model.addObserver(object: JsonBuilderObserver{
            override fun addItem(key: String, value: JsonValue) {
                text = model.jsonData.toJsonString
            }

            override fun removeItem(key: String) {
                text = model.jsonData.toJsonString
            }

            override fun modifyItem(key: String, newValue: String, oldValue: String) {
                text = model.jsonData.toJsonString
            }

            override fun refreshModel() {
                text = model.jsonData.toJsonString
            }
        })
    }
}