/**
 * Visitor Defines the Visitor pattern, allowing the addition of new operations without modifying those classes
 * Two methods are provided for visiting a Json Array or a Json Object
 *
 * @constructor Create empty instance of the Visitor interface
 */
interface Visitor {
    /**
     * Visit This method defines the behavior to be executed when visiting a JsonObject element
     *
     * @param jsonElement The JsonObject element being visited
     */
    fun visit(jsonElement: JsonObject)

    /**
     * Visit This method defines the behavior to be executed when visiting a JsonArray element
     *
     * @param jsonElement The JsonArray element being visited
     */
    fun visit(jsonElement: JsonArray)
}

/**
 * Get values by property name visitor
 *
 * @property name The name of the property to search for
 * @constructor Create new instance of the GetValuesByPropertyNameVisitor class with the given parameter
 */
class GetValuesByPropertyNameVisitor(val name:String): Visitor{
    val lst = mutableListOf<String>()

    /**
     * Visit Visits a JsonObject and searches for a property, a name/value pair, with the given name. If a property is
     * found, its value is added to the lst MutableList. If the property value implements the interface JsonStructure, a JsonArray or a JsonObject, this function calls the
     * corresponding "accept" method
     *
     * @param jsonElement The JsonObject to visit
     */
    override fun visit(jsonElement: JsonObject) {
        jsonElement.properties?.forEach {
            if(it.key == name)
                lst.add(it.value.toJsonString)
            if(it.value is JsonStructure) (it.value as JsonStructure).accept(this)
        }
    }

    /**
     * Visit Visits a JsonArray and checks if each value in the array and calls the "accept" method if the value is a JsonStructure
     *
     * @param jsonElement The JsonArray to visit
     */
    override fun visit(jsonElement: JsonArray) {
        jsonElement.valueList?.forEach {
            if(it is JsonStructure) it.accept(this)
        }
    }
}

/**
 * Get values by property name function
 *
 * @param jsonElement the JsonStructure to search for the property
 * @param name the name of the property to search for
 * @return a mutable list of the values found with the given property name
 */

fun getValuesByPropertyName(jsonElement: JsonStructure, name:String): MutableList<String> {
    val visitor = GetValuesByPropertyNameVisitor(name)
    jsonElement.accept(visitor)
    return visitor.lst
}

/**
 * Get object by properties names visitor
 *
 * @property names the list of property names to match
 * @constructor Creates a new instance of GetObjectByPropertiesNamesVisitor
 */

class GetObjectByPropertiesNamesVisitor(val names: List<String>): Visitor{
    val lst = mutableListOf<JsonObject>()

    /**
     * Visit Visits a JsonObject and checks if it has all the specified property names. If it does,
     * it adds itself to the list. If any property value is a JsonStructure, JsonArray or a JsonObject, the function
     * calls its corresponding "accept" method.
     *
     * @param jsonElement The JsonObject to visit
     */
    override fun visit(jsonElement: JsonObject) {
        if(jsonElement.properties?.keys?.containsAll(names) == true)
            lst.add(jsonElement)
        jsonElement.properties?.forEach {
            if(it.value is JsonStructure) (it.value as JsonStructure).accept(this)
        }
    }

    /**
     * Visit Visits a JSON array and calls accept on any contained JsonStructure.
     *
     * @param jsonElement The JsonArray to visit
     */
    override fun visit(jsonElement: JsonArray) {
        jsonElement.valueList?.forEach {
            if(it is JsonStructure) it.accept(this)
        }
    }
}

/**
 * Get objects with specific name value
 *
 * @param jsonElement  the JsonStructure to search for the property
 * @param properties the list of property names to search for
 * @return a mutable list of the objects found with the given properties
 */
fun getObjectsWithSpecificNameValue(jsonElement: JsonStructure, properties:List<String>): MutableList<JsonObject>{
    val visitor = GetObjectByPropertiesNamesVisitor(properties)
    jsonElement.accept(visitor)
    return visitor.lst
}
//ATE AQUI ESTA FEITO E VERIFICADO

/*
TODO
    verificar que o modelo obedece a determinada estrutura, por exemplo:
        a propriedade numero apenas tem como valores números inteiros
        a propriedade inscritos consiste num array onde todos os objetos têm a mesma estutura


//abaixo seria uma implementação explicitamente, mas no contexto da cadeira acho que faria mais sentido criar
//higher order function para fazer isto
// a propriedade numero apenas tem como valores números inteiros
// a propriedade inscritos consiste num array onde todos os objetos têm a mesma estutura
class ChekcIfModelPropertyObeysStructure(val name: String): Visitor{
    var obeysStructure: Boolean = true
    override fun visit(jsonElement: JsonObject) {
        "Not yet implemented"
    }

    override fun visit(jsonElement: JsonArray) {
        "Not yet implemented"
    }
}
 */
/**
 * Json search visitor
 *
 * @property searchPredicate A lambda function that takes a JsonObject and returns a Boolean
 * indicating whether the object matches the desired search criteria.
 * @constructor Creates a new JsonSearchVisitor object with the given searchPredicate.
 */
class JsonSearchVisitor(private val searchPredicate: (JsonObject) -> Boolean) : Visitor {
    val matchingObjects = mutableListOf<JsonObject>()

    /**
     * Visit The visit method for a JsonObject
     *
     * @param jsonElement The JsonObject to visit
     */
    override fun visit(jsonElement: JsonObject) {
        if(searchPredicate(jsonElement))
            matchingObjects.add(jsonElement)
        jsonElement.properties?.values?.forEach {
            if(it is JsonStructure) it.accept(this)
        }
    }

    /**
     * Visit The visit method for a JsonArray
     *
     * @param jsonElement The jsonArray to visit
     */
    override fun visit(jsonElement: JsonArray) {
        jsonElement.valueList?.forEach {
            if(it is JsonStructure) it.accept(this)
        }
    }
}

/**
 * Search json
 *
 * @param jsonElement The JsonStructure to search through
 * @param searchPredicate A lambda function that takes a JsonObject and returns a Boolean
 * indicating whether the object matches the desired search criteria.
 * @receiver
 * @return A list of all the JsonObjects in jsonElement that match the search criteria.
 */
fun searchJson(jsonElement: JsonStructure, searchPredicate: (JsonObject) -> Boolean): List<JsonObject> {
    val visitor = JsonSearchVisitor(searchPredicate)
    jsonElement.accept(visitor)
    return visitor.matchingObjects
}