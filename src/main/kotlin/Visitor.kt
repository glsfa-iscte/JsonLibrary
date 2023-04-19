interface Visitor {
    fun visit(jsonElement: JsonObject)
    fun visit(jsonElement: JsonArray)
}

/*
TODO
This file will contain all the operations on JsonElements
efetuar pesquisas, como por exemplo:
    obter todos os valores guardados em propriedades com identificador “numero”
    obter todos os objetos que têm as propriedades numero e nome
verificar que o modelo obedece a determinada estrutura, por exemplo:
    a propriedade numero apenas tem como valores números inteiros
    a propriedade inscritos consiste num array onde todos os objetos têm a mesma estutura

 */

class GetValuesByPropertyNameVisitor(val name:String): Visitor{
    var lst = mutableListOf<String>()
    override fun visit(jsonElement: JsonObject) {
        jsonElement.properties?.forEach {
            if(it.key == name)
                lst.add(it.value.toJsonString)
            if(it.value is JsonArray)
                (it.value as JsonArray).accept(this)
        }
    }

    override fun visit(jsonElement: JsonArray) {
        jsonElement.valueList?.forEach {
            if(it is JsonObject)
                it.accept(this)
        }
    }
}
fun getValuesByPropertyName(jsonElement: JsonStructure, name:String): MutableList<String> {
    val visitor = GetValuesByPropertyNameVisitor(name)
    jsonElement.accept(visitor)
    return visitor.lst
}

class GetObjectByPropertiesNamesVisitor(val names: List<String>): Visitor{
    var lst = mutableListOf<JsonObject>()
    override fun visit(jsonElement: JsonObject) {
        if(jsonElement.properties?.keys?.containsAll(names) == true
            //&& !jsonElement.properties.values.any { it is JsonArray }
             )
            lst.add(jsonElement)

        if(jsonElement.properties?.values?.any { it is JsonArray } == true)
            jsonElement.properties.values.filterIsInstance<JsonArray>().forEach {
                it.accept(this)
            }
        }


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