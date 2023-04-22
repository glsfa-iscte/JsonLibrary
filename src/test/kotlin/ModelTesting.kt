import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
class ModelTesting {
    private val number = JsonNumber( 101101)
    private val name = JsonString("Dave Farley")
    private val internacional = JsonBoolean(true)
    private val jsonArray = JsonArray(listOf(JsonBoolean(true), JsonBoolean(true), JsonBoolean(false)))
    private val expectedObjectIndentation =     "{\n" +
                                                "\t \"numero\" : 101101,\n" +
                                                "\t \"nome\" : \"Dave Farley\",\n" +
                                                "\t \"internacional\" : true\n" +
                                                "}"
    private val jsonObj = mapOf("numero" to number,
                                "nome" to name,
                                "internacional" to internacional)

    private val fullExample01: String =
                "{\n" +
                "\t \"uc\" : \"PA\",\n" +
                "\t \"ects\" : 6.0,\n" +
                "\t \"data-exame\" : null,\n" +
                "\t \"inscritos\" : [\n" +
                "\t\t{\n" +
                "\t\t\t \"numero\" : 101101,\n" +
                "\t\t\t \"nome\" : \"Dave Farley\",\n" +
                "\t\t\t \"internacional\" : true\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t \"numero\" : 101102,\n" +
                "\t\t\t \"nome\" : \"Martin Fowler\",\n" +
                "\t\t\t \"internacional\" : true\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t \"numero\" : 92888,\n" +
                "\t\t\t \"nome\" : \"Gustavo Ferreira\",\n" +
                "\t\t\t \"internacional\" : false\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}"

    private val insc01 = JsonObject(mapOf(
        "numero" to JsonNumber(101101),
        "nome" to JsonString("Dave Farley"),
        "internacional" to JsonBoolean(true)
    ))

    private val insc02 = JsonObject(mapOf(
        "numero" to JsonNumber(101102),
        "nome" to JsonString("Martin Fowler"),
        "internacional" to JsonBoolean(true)
    ))

    private val insc03 = JsonObject(mapOf(
        "numero" to JsonNumber(92888),
        "nome" to JsonString("Gustavo Ferreira"),
        "internacional" to JsonBoolean(false)
    ))

    private val inscritos = JsonArray(listOf(insc01, insc02, insc03))

    private val inscricoes = JsonObject(mapOf(
        "uc" to JsonString("PA"),
        "ects" to JsonNumber(6.0),
        "data-exame" to JsonNull(),
        "inscritos" to inscritos
    ))

    @Test
    fun testJsonString(){
        assertEquals(" \"PA\" ", JsonString("PA").toJsonString)
    }
    @Test
    fun testJsonNumber(){
        assertEquals(" 26503 ", JsonNumber( 26503).toJsonString)
    }
    @Test
    fun testJsonBoolean(){
        assertEquals(" false ", JsonBoolean(false).toJsonString)
    }
    @Test
    fun testJsonNull(){
        assertEquals(" null ", JsonNull().toJsonString)
    }

    @Test
    fun testJsonObject(){
        assertEquals( expectedObjectIndentation, JsonObject(jsonObj).toJsonString)
    }

    @Test
    fun testJsonObjectDepth(){
        assertEquals(1, JsonObject(jsonObj).depth)
    }

    @Test
    fun testJsonObjectArray(){
        assertEquals( "[\n true ,\n true ,\n false \n]", jsonArray.toJsonString)
    }

    @Test
    fun testEmptyJsoObject(){
        assertEquals( "{ }", JsonObject().toJsonString)
    }

    @Test
    fun testEmptyJsoArray(){
        assertEquals( "[ ]", JsonArray().toJsonString)
    }

    @Test
    fun testFullExample(){
        assertEquals(fullExample01, inscricoes.toJsonString)
    }
}