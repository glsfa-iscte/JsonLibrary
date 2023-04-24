import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
class VisitorTesting {
    internal val x01a = JsonObject(mapOf(
            "x" to JsonBoolean(true),
            "y" to JsonBoolean(false)
    ))
    internal val x01 = JsonObject(mapOf(
        "numero" to JsonNumber(101102),
        "nome" to JsonString("Jane Doe"),
        "intern" to x01a
    ))

    internal val x02 = JsonObject(mapOf(
        "numero" to JsonNumber(101104),
        "nome" to JsonString("William That"),
        "x" to JsonBoolean(true)
    ))
    internal val x03a = JsonObject(mapOf(
        "notIt" to JsonBoolean(true),
        "x" to JsonNumber(1),
        "y" to JsonString("Hi"),
        "numero" to JsonNumber(10)
    ))
    internal val x03b = JsonObject(mapOf(
        "x" to JsonNumber(2),
        "y" to JsonString("Hi")
    ))
    internal val x03 = JsonObject(mapOf(
        "numero" to JsonNumber(101105),
        "nome" to JsonString("Who This"),
        "internacional" to JsonArray(listOf(
            JsonString("Me"),
            x03a,
            JsonArray(listOf(
                x03b
            ))
        ))
    ))
    @Test
    fun testGetValuesByPropertyName01(){
        assertEquals(mutableListOf(" 101101 " ,  " 101102 " ,  " 92888 "), getValuesByPropertyName(inscricoes01, "numero"))
    }
    @Test
    fun testGetValuesByPropertyName02(){
        assertEquals(mutableListOf(" 101101 " ,  " 101102 " ,  " 92888 ", " 101103 "), getValuesByPropertyName(inscricoes02, "numero"))
    }
    @Test
    fun testGetValuesByPropertyName03(){
        assertEquals(mutableListOf(" 101102 " ,  " 92888 " ,  " 101104 "), getValuesByPropertyName(inscricoes03, "numero"))
    }

    @Test
    fun testGetValuesByPropertyName04(){
        assertEquals(mutableListOf(" true ", " true ",  " 1 ", " 2 "), getValuesByPropertyName(inscricoes04, "x"))
    }
    //good till here

    @Test
    fun testGetObjectsWithSpecificNameValue01(){
        assertEquals(listOf(insc01, insc02, insc03), getObjectsWithSpecificNameValue(inscricoes01, listOf<String>("numero", "nome")))
    }
    @Test
    fun testGetObjectsWithSpecificNameValue02(){
        assertEquals(listOf(x01a, x03a, x03b), getObjectsWithSpecificNameValue(inscricoes04, listOf<String>("x", "y")))
    }
    @Test
    fun testGetObjectsWithSpecificNameValue03(){
        assertEquals(listOf(x01a, x02, x03a, x03b), getObjectsWithSpecificNameValue(inscricoes04, listOf<String>("x")))
    }


    @Test
    fun testSearchJsonStructure01(){
        val predicate = { jsonObject: JsonObject ->
            jsonObject.properties?.containsKey("inscritos") == true && (jsonObject as JsonObject).properties!!.getValue("inscritos") is JsonArray
        }

        val matchingObjects = searchJson(inscricoes01, predicate)
        assertEquals(1, matchingObjects.size)

        val inscritosArray = matchingObjects[0].properties!!.getValue("inscritos") as JsonArray
        val inscritosStructureFirstElement = inscritosArray.valueList!![0].toString()
        val pattern = "([^=,]+)=(Json[A-Za-z<]+)"

        val firstElementStructure = getJsonPropertyTypes(inscritosStructureFirstElement, pattern)
        println("FIRST ELEM: " + firstElementStructure)

        (inscritosArray as JsonArray).valueList!!.drop(1).forEach { jsonObject ->
            val properties = (jsonObject as JsonObject).properties!!.toString()
            val elementStructure = getJsonPropertyTypes(properties, pattern)
            println("OTHER ELEM: " + elementStructure)
            assertEquals(firstElementStructure, elementStructure)
        }
    }
    //TODO ADICIONAR UM QUE TENHA UM OBJETO DENTRO DO OBJETO (dentro de cada inscricao tem um obj tambem)
    @Test
    fun testSearchJsonStructure02(){
        val objectList = searchJson(inscritos) {
                (it as JsonObject).properties!!.containsKey("numero")
        }
        val firstElement = objectList[0].properties!!.entries.filter { (name, value) -> name == "numero" && value is JsonNumber }.toString()
        val pattern = "([^=,]+)=(Json[A-Za-z<]+)"
        val firstElementStructure = getJsonPropertyTypes(firstElement, pattern)
        objectList.drop(1).forEach { obj ->
            val otherElement = obj.properties!!.entries.filter { (name, value) -> name == "numero" && value is JsonNumber  }.toString()
            val elementStructure = getJsonPropertyTypes(otherElement, pattern)
            assertEquals(firstElementStructure, elementStructure)
        }
    }
    //ALL GOOD ABOVE
    //TODO MORE TESTS FOR THE SECOND REQUIREMENT OF THE VISITORS
    //FINNISH THE TESTS BELLOW
    //more tests for the JsonSearch!! only have 2 above
    /*
    @Test
    fun testJsonSearch02(){
        assertEquals(
            mutableListOf(insc01, insc02, insc03),
            searchJson(inscricoes01) { (it.properties as? Map<String, JsonValue>)?.containsKey("numero") == true && (it.properties as? Map<String, JsonValue>)?.containsKey("nome") == true }
        )
    }

    @Test
    fun testJsonSearch03(){
        assertEquals(
            mutableListOf(insc01, insc02, insc03),
            searchJson(inscricoes01) { (it.properties as? Map<String, JsonValue>)?.containsKey("numero") == true && ((it.properties as? Map<String, JsonValue>)?.get("numero") as? JsonNumber != null) }
        )
    }


    @Test
    fun testJsonSearch04() {
        val expectedStructure = mapOf<String, JsonValue>(
            "numero" to JsonNumber(0),
            "nome" to JsonString(""),
            "internacional" to JsonBoolean(false)
        )
        //TODO MISSING CHECK IF ITS AN JSON ARRAY
        val inscritoPredicate: (JsonObject) -> Boolean = { (it.properties?.keys == expectedStructure.keys) }
        assertEquals(
            listOf(insc01, insc02, insc03),
            searchJson(inscritos, inscritoPredicate)
        )
    }
     */
}