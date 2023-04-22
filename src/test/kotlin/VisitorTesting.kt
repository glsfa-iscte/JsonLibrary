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
        "y" to JsonString("Hi")
    ))
    internal val x03 = JsonObject(mapOf(
        "numero" to JsonNumber(101105),
        "nome" to JsonString("Who This"),
        "internacional" to JsonArray(listOf(
            JsonString("Me"),
            x03a
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
        assertEquals(mutableListOf(" true " ,  " 1 "), getValuesByPropertyName(inscricoes04, "x"))
    }
    //good till here

    @Test
    fun testGetObjectsWithSpecificNameValue01(){
        assertEquals(listOf(insc01, insc02, insc03), getObjectsWithSpecificNameValue(inscricoes01, listOf<String>("numero", "nome")))
    }
    @Test
    fun testGetObjectsWithSpecificNameValue02(){
        assertEquals(listOf(x01a, x03a), getObjectsWithSpecificNameValue(inscricoes04, listOf<String>("x", "y")))
    }

    //TODO
    // FAZER OS RESTANTES TESTES PARA AS OUTRAS PESQUISAS
    // IMPLEMENTAR O ChekcIfModelPropertyObeysStructure

    @Test
    fun testJsonSearch01(){
        val objectList = searchJson(inscricoes01) { (it.properties as? Map<String, JsonValue>)?.containsKey("numero") == true  }

        assertEquals(
            mutableListOf(" 101101 " ,  " 101102 " ,  " 92888 "), objectList

        )
    }

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
}