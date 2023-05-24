import javax.swing.JTextArea

class TextAreaView(private val model: JsonObjectBuilder): JTextArea(){
    init{
        text = model.jsonData.toJsonString
        model.addObserver(object: JsonObjectObserver{
            override fun addProperty(key: String) {
                text = model.jsonData.toJsonString
                println("Added "+model.data)
            }

            override fun removeProperty(key: String) {
                text = model.jsonData.toJsonString
                println("Removed "+model.data)
            }

            override fun modifyProperty(key: String, newValue: String, oldValue: String) {
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