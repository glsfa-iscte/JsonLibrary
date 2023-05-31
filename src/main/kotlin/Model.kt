/**
 * Json value - This interface represents a value in json, string in double quotes, or a number, or true or false or null, or an object or an array
 *
 * @constructor Create empty Json value
 */
sealed interface JsonValue {
    // Returns a string representation of this JsonValue in JSON format
    val toJsonString: String
    fun getJsonValue(): Any?
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

    override fun getJsonValue(): String {
        return value
    }
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

    override fun getJsonValue(): Number {
        return value
    }
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
    override fun getJsonValue(): Boolean {
        return value
    }
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
    override fun getJsonValue(): Any? {
        return null
    }
}

internal fun parseToOriginalReturnType(input: String): Any? {
    return when {
        input == ":" -> mapOf<Any, Any>()
        input == "N/A" || input == "null" -> null
        input.isBlank() -> listOf<Any>()
        input.toIntOrNull() != null -> input.toInt()
        input.toDoubleOrNull() != null -> input.toDouble()
        input.toBooleanStrictOrNull() != null -> input.toBoolean()
        else -> input
    }
}
//CONDENSED JsonObjectObserver AND JsonArrayObserver INTO ONE INTERFACE, TO REDUCE DUPLICATE CODE
interface JsonBuilderObserver {
    fun addItem(key: String, value: JsonValue) { }
    fun removeItem(key: String) { }
    fun modifyItem(key: String, newValue: String, oldValue: String, associatedValue:JsonValue? = null) { }
    fun refreshModel() { }
}
//interface JsonObjectObserver {
//    fun addProperty(key: String) { }
//    fun removeProperty(key: String){ }
//    fun modifyProperty(key: String, newValue: String, oldValue: String){ }
//    fun refreshModel(){ }
//}
abstract class JsonBuilder{
    val data = mutableMapOf<String, JsonValue>()
    abstract val jsonData: JsonStructure

    val observers = mutableListOf<JsonBuilderObserver>()
    fun addObserver(observer: JsonBuilderObserver) {
        observers.add(observer)
    }
    fun removeObserver(observer: JsonBuilderObserver) {
        observers.remove(observer)
    }
    // CHANGED SO THAT I CAN ADD A VALUE THAT ISNT JUST JSON NULL (GOR THE UNDO STACK AND INSTANCIATION)
    abstract fun add(key: String, value:JsonValue)
    fun remove(key: String){
        data.remove(key)
        observers.forEach {
            it.removeItem(key)
        }
    }
     fun modify(key: String, newValue: String, oldValue: String, associatedValue:JsonValue? = null) {
        if (oldValue != newValue) {
            println("RCV MOD model ${data} key ${key} newvalue ${newValue} oldvalue ${oldValue} ")
            //isto verifica se veio da GUI (associated a null) ou do undo (associated != null)
            if(associatedValue == null) {
                val jsonValue = instanciateJson(parseToOriginalReturnType(newValue))
                data[key] = jsonValue
                observers.forEach {
                    it.modifyItem(key, newValue, newValue)
                }
            }else{
                println("associatedValue ${associatedValue}")
                data[key] = associatedValue
                println("DATA : ${data}")
                observers.forEach {
                    it.modifyItem(key, newValue, newValue, associatedValue)
                }
            }
            println("SND MOD model ${data} key ${key} newvalue ${newValue} oldvalue ${oldValue} ")

        }
    }
}
class JsonObjectBuilder(val initialJsonObject: JsonObject) : JsonBuilder() {
    override val jsonData: JsonStructure = JsonObject(data)
    init {
        //if(initialData.isNotEmpty()) data.putAll(initialData)
        if(!initialJsonObject.properties.isNullOrEmpty()) data.putAll(initialJsonObject.properties)
    }
    override fun add(key: String, value: JsonValue) {
        if (!data.containsKey(key)) {
            data[key] = value //JsonNull()
            observers.forEach {
                it.addItem(key, value)
            }
        }
    }
    fun refreshModel(){
        observers.forEach {
            it.refreshModel()
        }
    }
}
class JsonArrayBuilder(val initialJsonArray: JsonArray) : JsonBuilder() {
    //EU QUERIA MANTER USAR UMA CHAVE PARA IDENTIFICAR CADA ELEMENTO DO MODEL, PARA NAO APAGAR INCORRETAMENTE, MAS AO FAZER ISSO, TIVE QUE FAZER ESTA PROPRIEDADE CLACULADA
    //COM ISSO DEPOIS CRIA O PROBLEMA QUE AS MUDANÇAS NÃO SAO PROPAGADAS DE FORMA INSTANTANEA ATÉ O jsonData ser acedido
    override val jsonData: JsonStructure
        get() = JsonArray(data.values.toList())
    var lastIndex: Int = -1
    init {
        //if(initialData.isNotEmpty()) data.putAll(initialData)
        if(!initialJsonArray.valueList.isNullOrEmpty()) initialJsonArray.valueList.forEachIndexed { index, jsonValue ->
            data["$index"] = jsonValue
            lastIndex=index
        }
    }

    override fun add(key: String, value: JsonValue) {
        data[key] = value//JsonNull()
        observers.forEach {
            it.addItem(key, value)
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
    //ELVIS OPERATOR WAS REMOVED IN FAVOUR OF THE CHECK, SO THAT IF PROPERIES IS NULL OR EMPTY, IT RETURNS "{}" WHEREAS BEFORE IT ONLY RETURNED THAT IF PROPERTIES WAS NULL
    //TEST SUITE PASSED
    override val toJsonString: String
        get() {
            return if (properties.isNullOrEmpty()) {
                "{ }"
            } else {
                properties.entries.joinToString(
                    separator = ",\n",
                    // Removed to add "${"\t".repeat(depth)} in Json Array, so that Json Values are properly indented and so that Json Value doesn't have depth, causing every jsonValue to have init
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

    override fun getJsonValue(): Any? {
        return properties
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
            //ELVIS OPERATOR WAS REMOVED IN FAVOUR OF THE CHECK, SO THAT IF PROPERIES IS NULL OR EMPTY, IT RETURNS "{}" WHEREAS BEFORE IT ONLY RETURNED THAT IF PROPERTIES WAS NULL
            //TEST SUITE PASSED
            return if (valueList.isNullOrEmpty()) {
                "[ ]"
            } else {
                valueList.joinToString(
                separator = ",\n",
                prefix = "[\n",
                postfix = "\n${"\t".repeat(depth-1)}]",
            ) { "${"\t".repeat(depth)}${it.toJsonString}"
                }
            }
        }
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun getJsonValue(): Any? {
        return valueList
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