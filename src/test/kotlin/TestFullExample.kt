import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
class TestFullExample {
//os espaços passar a \t
    val inscricoesString: String =
            "{\n" +
            "\"uc\" : \"PA\",\n" +
            "  \"ects\" : 6.0,\n" +
            "  \"data-exame\" : null,\n" +
            "  \"inscritos\" : [\n" +
            "    {\n" +
            "      \"numero\" : 101101,\n" +
            "      \"nome\" : \"Dave Farley\",\n" +
            "      \"internacional\" : true\n" +
            "    },\n" +
            "    {\n" +
            "      \"numero\" : 101102,\n" +
            "      \"nome\" : \"Martin Fowler\",\n" +
            "      \"internacional\" : true\n" +
            "    },\n" +
            "    {\n" +
            "      \"numero\" : 92888,\n" +
            "      \"nome\" : \"Gustavo Ferreira\",\n" +
            "      \"internacional\" : false\n" +
            "    }\n" +
            "  ]\n" +
            "}"


            val insc01 = JsonObject(mapOf(
                "numero" to JsonNumber(101101),
                "nome" to JsonString("Dave Farley"),
                "internacional" to JsonBoolean(true)
            ))

            val insc02 = JsonObject(mapOf(
                "numero" to JsonNumber(101102),
                "nome" to JsonString("Martin Fowler"),
                "internacional" to JsonBoolean(true)
            ))

            val insc03 = JsonObject(mapOf(
                "numero" to JsonNumber(92888),
                "nome" to JsonString("Gustavo Ferreira"),
                "internacional" to JsonBoolean(false)
            ))

    val inscritos = JsonArray(listOf(insc01, insc02, insc03))

    val inscricoes = JsonObject(mapOf(
        "uc" to JsonString("PA"),
        "ects" to JsonNumber(6.0),
        "data-exame" to JsonNull(),
        "inscritos" to inscritos
    ))


    @Test
    fun testFullExample(){
        assertEquals(inscricoesString, inscricoes.toJsonString)
    }

    @Test
    fun testDepth(){
       // assertEquals(2, inscricoes.depth)
    }
    @Test
    fun testChildren(){
        println("Children: ")
        //inscricoes.children.forEach { println(it) }

    }
    @Test
    fun testGetValuesByPropertyName(){
        assertEquals(mutableListOf(" 101101 " ,  " 101102 " ,  " 92888 "), getValuesByPropertyName(inscricoes, "numero"))
    }

    @Test
    fun testGetObjectsWithSpecificNameValeu(){
        assertEquals(mutableListOf(insc01, insc02, insc03), getObjectsWithSpecificNameValue(inscricoes, listOf<String>("numero", "nome")))
    }

    //TODO
    // FAZER OS RESTANTES TESTES PARA AS OUTRAS PESQUISAS
    // IMPLEMENTAR O ChekcIfModelPropertyObeysStructure

    @Test
    fun testJsonSearch01(){
        val objectList = searchJson(inscricoes) { (it.properties as? Map<String, JsonValue>)?.containsKey("numero") == true  }

        assertEquals(
            mutableListOf(" 101101 " ,  " 101102 " ,  " 92888 "), objectList

        )
    }

    @Test
    fun testJsonSearch02(){
        assertEquals(
            mutableListOf(insc01, insc02, insc03),
            searchJson(inscricoes) { (it.properties as? Map<String, JsonValue>)?.containsKey("numero") == true && (it.properties as? Map<String, JsonValue>)?.containsKey("nome") == true }
        )
    }

    @Test
    fun testJsonSearch03(){
        assertEquals(
            mutableListOf(insc01, insc02, insc03),
            searchJson(inscricoes) { (it.properties as? Map<String, JsonValue>)?.containsKey("numero") == true && ((it.properties as? Map<String, JsonValue>)?.get("numero") as? JsonNumber != null) }
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

    @Test
    fun testInstanciateJson(){
        data class TestClass(val name: String, val number: Int)

        val testDataClass = TestClass("Gustavo", 92888)
        val testString = "Snow"
        val testNumber = 1234
        val testBoolean = true
        val testNull = null
        val testMap = mapOf<Any?, Any?>(
            "Test" to testString,
            123 to testBoolean,
            testString to testDataClass,
            't' to testNull
            )
        val testCollection = mutableListOf<Any>()
        val testEnum = "Snow"

        val jsonObj01 = instanciateJson(testDataClass)
        val jsonObj02 = instanciateJson(testString)
        val jsonObj03 = instanciateJson(testNumber)
        val jsonObj04 = instanciateJson(testBoolean)
        val jsonObj05 = instanciateJson(testNull)
        val jsonObj06 = instanciateJson(testMap).toJsonString
        val jsonObj07 = instanciateJson(testCollection)
        val jsonObj08 = instanciateJson(testEnum)

        println(jsonObj01)
        println(jsonObj02)
        println(jsonObj03)
        println(jsonObj04)
        println(jsonObj05)
        println(jsonObj06)
        println(jsonObj07)
        println(jsonObj08)

        //assertEquals("", jsonObj.toJsonString)
    }

    /**
     * Test instanciate json02 - Tests if it ignores properties that have the annotation @IgnoreProperty
     */

    @Test
    fun testInstanciateJson01(){
        data class Student(
            val number: Int,
            val name: String,
            val cool: Boolean
        )

        val st = Student(92888, "Gustavo", true)
        assertEquals(JsonObject(mapOf("number" to JsonNumber(92888), "name" to JsonString("Gustavo"), "cool" to JsonBoolean(true))), instanciateJson(st))
    }

    @Test
    fun testInstanciateJson02(){
        data class Student(
            @IgnoreProperty
            val number: Int,
            val name: String,
            val cool: Boolean
        )

        val st = Student(92888, "Gustavo", true)
        assertEquals(JsonObject(mapOf("name" to JsonString("Gustavo"), "cool" to JsonBoolean(true))), instanciateJson(st))
    }


    @Test
    fun testInstanciateJson03(){
        data class Student(
            @ForceJsonString
            val number: Int,
            val name: String,
            @ForceJsonString
            val cool: Boolean
        )

        val st = Student(92888, "Gustavo", true)
        assertEquals(JsonObject(mapOf("number" to JsonString("92888"), "name" to JsonString("Gustavo"), "cool" to JsonString("true"))), instanciateJson(st))
    }

    @Test
    fun testInstanciateJson04(){
        data class Student(
            @CustomIdentifier("boolean")
            val number: Int,
            val name: String,
            val cool: Boolean
        )

        val st = Student(92888, "Gustavo", true)
        assertEquals(JsonObject(mapOf("number" to JsonBoolean(false), "name" to JsonString("Gustavo"), "cool" to JsonBoolean(true))), instanciateJson(st))
    }
}