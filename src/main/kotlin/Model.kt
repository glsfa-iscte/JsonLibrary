/**
 * Json value - This interface represents a value in json, string in double quotes, or a number, or true or false or null, or an object or an array
 *
 * @constructor Create empty Json value
 */
sealed interface JsonValue {
    // Returns a string representation of this JsonValue in JSON format
    val toJsonString: String
}

/**
 * Json structure - This interface is used to represent a subtype of json values, the one that can hold other Json values, such as a Json Array or a Json Object
 *
 * @constructor Create empty Json structure
 */
sealed interface JsonStructure : JsonValue {
    // Used to calculate the indentation of nested Json Values in Json Structures
    var depth: Int
    override val toJsonString: String
        get() = ""
    abstract fun accept(visitor: Visitor)
}

/**
 * Json string - This dataclass is used to represent a Json String value
 *
 * @property value The string we want to represent as Json String value
 * @constructor Create Json String with specified value
 */
data class JsonString(val value: String) : JsonValue {
    override val toJsonString: String
        get() = " \"$value\" "
}

/**
 * Json number - This dataclass is used to represent a Json Number value
 *
 * @property value The numeric value we want to represent as a Json Number value
 * @constructor Create Json Number with specified value
 */
data class JsonNumber(val value: Number) : JsonValue {
    override val toJsonString: String
        get() = " $value "
}

/**
 * Json boolean - This dataclass is used to represent a Json Boolean value
 *
 * @property value The boolean value we want to represent as a Json Number value
 * @constructor Create empty Json boolean
 */
data class JsonBoolean(val value: Boolean) : JsonValue {
    override val toJsonString: String
        get() = " $value "
}

/**
 * Json null - This dataclass is used to represent a Json Null value
 *
 * @property value This parameter can be ommited and will create a Json Null value
 * @constructor Create empty Json null
 */
data class JsonNull(val value: Any? = null) : JsonValue {
    override val toJsonString: String
        get() = " null "
}

interface JsonObjectObserver {
    fun addProperty(key: String) { }
    fun removeProperty(key: String){ }
    fun modifyProperty(key: String, newValue: String, oldValue: String){ }
    fun refreshModel(){ }
}

class JsonObjectBuilder() {
    var data = mutableMapOf<String, JsonValue>()
    var jsonData = JsonObject(data)
    private val observers = mutableListOf<JsonObjectObserver>()

    fun addObserver(observer: JsonObjectObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: JsonObjectObserver) {
        observers.remove(observer)
    }

    fun addProperty(key: String) {
        data.put(key, JsonNull())
        observers.forEach {
            it.addProperty(key)
        }
    }
    fun removeProperty(key: String) {
        data.remove(key)
        observers.forEach {
            it.removeProperty(key)
        }
    }
    fun modifyValue(key:String, newValue: String, oldValue: String) {
        val jsonValue = instanciateJson(parseToOriginalReturnType(newValue))
        //SE O VALOR DO RESULTADO ANTIGO FOR IGUAL AO MODIFICADO ELE NAO FAZ NADA
        if(oldValue != jsonValue.toJsonString) {
            data.put(key, jsonValue)
            observers.forEach {
                it.modifyProperty(key, jsonValue.toJsonString, jsonValue.toJsonString)
            }
        }
    }
    //ADICIONADO DE FORMA A AVISAR OS OUVINTES, NESTE CASO SO OS TEXTAREAVIEW (JÁ QUE OS OUTROS NÃO IMPEMENTAM ESTE METODO), DE QUE UM NESTED FOI ADICIONADO E O TEXTO TEM QUE SER UPDATED
    fun refreshModel(){
        observers.forEach {
            it.refreshModel()
        }
    }
    fun parseToOriginalReturnType(input: String): Any? {
        return when {
            input == ":" -> mutableMapOf<Any, Any>()
            input == "N/A" || input == "null" -> null
            input.isNullOrBlank() -> mutableListOf<Any>()
            input.toIntOrNull() != null -> input.toInt()
            input.toDoubleOrNull() != null -> input.toDouble()
            input.toBooleanStrictOrNull() != null -> input.toBoolean()
            else -> input
        }
    }
    fun compareStrings(string1: String, string2: String): Boolean {
        val transformedString1 = string1.trim().replace("\\\"", "").trim()
        val transformedString2 = string2.trim().replace("\\\"", "").trim()
        return transformedString1 == transformedString2
    }
}

interface JsonArrayObserver {
    fun addValue(value: String) { }
    fun removeValue(value: String){ }
    fun modifyValue(key: String, newValue: String, oldValue: String){ }
    fun refreshModel(){ }
}

class JsonArrayBuilder() {
    var data = mutableListOf<JsonValue>()
    var jsonData = JsonArray(data)
    private val observers = mutableListOf<JsonArrayObserver>()

    fun addObserver(observer: JsonArrayObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: JsonArrayObserver) {
        observers.remove(observer)
    }

    fun addValue(value: String) {
        val instanciatedInput = instanciateJson(parseToOriginalReturnType(value))
        data.add(instanciatedInput)
        observers.forEach {
            it.addValue(value)
        }
    }
    fun removeValue(value: String) {
        val instanciatedInput = instanciateJson(parseToOriginalReturnType(value))
        data.remove(instanciatedInput)
        observers.forEach {
            it.removeValue(value)
        }
    }
    //TODO ACHO QUE ISTO ESÁ A CAUSAR O PROBLEMA DE ADICIONAR UM OBJ/ARR A UM ARRAY
    // 2 O MODEL RECEBE O VALUE, VALOR DO TEXT FIELD E O VALUE
    fun modifyValue(key:String, newValue: String, oldValue: String) {
        val jsonValue = instanciateJson(parseToOriginalReturnType(newValue))
        if(oldValue != jsonValue.toJsonString) {
            val index = data.indexOf(instanciateJson(parseToOriginalReturnType(oldValue)))
            data.set(index, jsonValue)
            observers.forEach {
                it.modifyValue(key, jsonValue.toJsonString, jsonValue.toJsonString)
            }
        }
    }
    fun parseToOriginalReturnType(input: String): Any? {
        return when {
            input == ":" -> mutableMapOf<Any, Any>()
            input == "N/A" || input == "null" -> null
            input.isNullOrBlank() -> mutableListOf<Any>()
            input.toIntOrNull() != null -> input.toInt()
            input.toDoubleOrNull() != null -> input.toDouble()
            input.toBooleanStrictOrNull() != null -> input.toBoolean()
            else -> input
        }
    }
}
/**
 * Json object - This dataclass is used to represent a Json Object
 *
 * @property properties  an unordered set of name/value pairs that can be omitted to create an empty Json Object
 * @constructor Create empty Json object
 */
data class JsonObject(val properties: Map<String, JsonValue>? = null) : JsonStructure {
    override var depth: Int = 1

    /**
     * Goes through each of the name/value pair in properties and if it's a Json Structure, it's depth is updated to reflect the indentation expected
     */
    init {
        properties?.values?.updateDepth(depth)
    }
    /**
     * To json string If the Json Object is empty, it returns the representation of an empty Json Object, { } else it returns each name value pair as "name" : value
     */
    //ELVIS OPERATOR WAS REMOVED IN FAVOUR OF THE CHECK, SO THAT IF PROPERIES IS NULL OR EMPTY, IT RETURNS "{}" WHERE AS BEFORE IT ONLY RETURNED THAT IF PROPERTIES WAS NULL
    //TEST SUITE PASSED
    override val toJsonString: String
        get() {
            return if (properties.isNullOrEmpty()) {
                "{ }"
            } else {
                properties.entries.joinToString(
                    separator = ",\n",
                    // Removed to add "${"\t".repeat(depth)} in Json Array, so that Json Values are properly indented and so that Json Value doesnt have depth, causing every jsonValue to have init
                    //prefix = "${"\t".repeat(depth-1)}{\n",
                    prefix = "{\n",
                    postfix = "\n${"\t".repeat(depth - 1)}}"
                ) { (name, value) ->
                    "${"\t".repeat(depth)} \"$name\" : ${value.toJsonString.trimStart().trimEnd()}"
                }
            }
        }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

/**
 * Json array - This dataclass is used to represent a Json Array
 *
 * @property valueList  an ordered collection of values that can be omitted to create an empty Json Array
 * @constructor Create empty Json array
 */
data class JsonArray(val valueList: List<JsonValue>? = null) : JsonStructure {
    override var depth: Int = 1
    init {
        valueList?.updateDepth(depth)
    }
    /**
     * To json string If the Json Array is empty, it returns the representation of an empty Json Object, [ ] else it returns each entrie toJsonString implementation
     */
    override val toJsonString: String
        get() {
            return valueList?.joinToString(
                separator = ",\n",
                prefix = "[\n",
                postfix = "\n${"\t".repeat(depth-1)}]",
            ) { "${"\t".repeat(depth)}${it.toJsonString}" } ?: "[ ]"
        }
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

}

/**
 * Updates the depth of each JsonStructure in the collection based on the depth of its parent object.
 * If the JsonValue object is a JsonObject, its properties are also updated recursively.
 * If the JsonValue object is a JsonArray, its elements are also updated recursively.
 * @param parentDepth the depth of the parent JsonValue object
 */
fun Collection<JsonValue>.updateDepth(parentDepth: Int) {
    this.forEach { childValue ->
        if (childValue is JsonStructure) {
            childValue.depth = parentDepth + 1
            if (childValue is JsonObject) {
                childValue.properties?.values?.updateDepth(childValue.depth)
            } else if (childValue is JsonArray) {
                childValue.valueList?.updateDepth(childValue.depth)
            }
        }
    }
}