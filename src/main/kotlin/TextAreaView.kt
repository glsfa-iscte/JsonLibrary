import javax.swing.JTextArea

class TextAreaView(private val model: JsonObjectBuilder): JTextArea(){
    init{
        text = model.jsonData.toJsonString
        model.addObserver(object: JsonObjectObserver{
            override fun addProperty(key: String, parentObjectKey: String) {
                text = model.jsonData.toJsonString
            }

            override fun removeProperty(key: String) {
                text = model.jsonData.toJsonString
            }

            override fun modifyProperty(key: String, newValue: JsonValue) {
                text = model.jsonData.toJsonString
            }

            override fun addObject(key: String, widgetId: Int) {
                text = model.jsonData.toJsonString
            }
        })
    }
}