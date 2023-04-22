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

/*
TODO
This file will contain all the operations on JsonElements
efetuar pesquisas, como por exemplo:
    obter todos os valores guardados em propriedades com identificador “numero”                                     DONE
    obter todos os objetos que têm as propriedades numero e nome
verificar que o modelo obedece a determinada estrutura, por exemplo:
    a propriedade numero apenas tem como valores números inteiros
    a propriedade inscritos consiste num array onde todos os objetos têm a mesma estutura

 */
/**
 * Get values by property name visitor
 *
 * @property name
 * @constructor Create new instance of the GetValuesByPropertyNameVisitor class with the given parameter
 */
class GetValuesByPropertyNameVisitor(val name:String): Visitor{
    var lst = mutableListOf<String>()

    /**
     * Visit Visits a JsonObject and searches for a property, a name/value pair, with the given name. If a property is
     * found, its value is added to the lst MutableList. If the property value is a JsonArray, this function calls the
     * accept method on the JsonArrayIf the property value is a JsonArray, this function calls the accept method on the JsonArray
     *
     * @param jsonElement The JsonObject to visit
     */
    override fun visit(jsonElement: JsonObject) {
        jsonElement.properties?.forEach {
            if(it.key == name)
                lst.add(it.value.toJsonString)
            if(it.value is JsonArray)
                (it.value as JsonArray).accept(this)
        }
    }

    /**
     * Visit Visits a JsonArray and checks if each value in the array is a JsonObject. If a JsonObject is found,
     * this function calls the accept method on the JsonObject
     *
     * @param jsonElement The JsonArray to visit
     */
    override fun visit(jsonElement: JsonArray) {
        jsonElement.valueList?.forEach {
            if(it is JsonObject)
                it.accept(this)
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
    var lst = mutableListOf<JsonObject>()

    /**
     * Visit Visits a JSON object and checks if it has all the required property names. If it does,
     * it adds the object to the list. If any property value is an array or an object, it visits the array or object, respectively.
     *
     * @param jsonElement The JsonObject to visit
     */
    override fun visit(jsonElement: JsonObject) {
        if(jsonElement.properties?.keys?.containsAll(names) == true
            //&& !jsonElement.properties.values.any { it is JsonArray }
             )
            lst.add(jsonElement)
        if(jsonElement.properties?.values?.any { it is JsonArray } == true)
            jsonElement.properties.values.filterIsInstance<JsonArray>().forEach {
                it.accept(this)
            }
        if(jsonElement.properties?.values?.any { it is JsonObject } == true)
            jsonElement.properties.values.filterIsInstance<JsonObject>().forEach {
                it.accept(this)
            }
        }

    /**
     * Visit Visits a JSON array and calls accept on any contained JSON objects.
     *
     * @param jsonElement The JsonArray to visit
     */
    override fun visit(jsonElement: JsonArray) {
        jsonElement.valueList?.forEach {
            if(it is JsonObject) it.accept(this)
        }
    }
}
//abaixo seria uma implementação explicitamente, mas no contexto da cadeira acho que faria mais sentido criar
//higher order function para fazer isto
// a propriedade numero apenas tem como valores números inteiros
// a propriedade inscritos consiste num array onde todos os objetos têm a mesma estutura
fun getObjectsWithSpecificNameValue(jsonElement: JsonStructure, name:List<String>): MutableList<JsonObject>{
    val visitor = GetObjectByPropertiesNamesVisitor(name)
    jsonElement.accept(visitor)
    return visitor.lst
}

class ChekcIfModelPropertyObeysStructure(val name: String): Visitor{
    var obeysStructure: Boolean = true
    override fun visit(jsonElement: JsonObject) {
        TODO("Not yet implemented")
    }

    override fun visit(jsonElement: JsonArray) {
        TODO("Not yet implemented")
    }
}

class JsonSearchVisitor(private val searchPredicate: (JsonObject) -> Boolean) : Visitor {
    val matchingObjects = mutableListOf<JsonObject>()
    override fun visit(jsonElement: JsonObject) {
        if(searchPredicate(jsonElement))
            matchingObjects.add(jsonElement)
        jsonElement.properties?.values?.forEach {
            if(it is JsonObject) it.accept(this)
            else
                if(it is JsonArray) it.accept(this)
        }
    }

    override fun visit(jsonElement: JsonArray) {
        jsonElement.valueList?.forEach {
            if(it is JsonObject) it.accept(this)
        }
    }
}
fun searchJson(jsonElement: JsonStructure, searchPredicate: (JsonObject) -> Boolean): List<JsonObject> {
    val visitor = JsonSearchVisitor(searchPredicate)
    jsonElement.accept(visitor)
    return visitor.matchingObjects
}