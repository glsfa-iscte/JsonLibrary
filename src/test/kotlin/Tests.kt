import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
class Tests {
    val numero = "numero" to JsonNumber( 101101)
    val nome = "nome" to JsonString("Dave Farley")
    val internacional = "internacional" to JsonBoolean(true)
    val jsonObjArray = JsonArray(listOf(JsonBoolean(true), JsonBoolean(true), JsonBoolean(false)))
    val jsonObj = mapOf(numero, nome, internacional)
    val expectedObjectWithTabs =
            "{\n" +
                "\t \"numero\" : 101101 ,\n" +
                "\t \"nome\" : \"Dave Farley\" ,\n" +
                "\t \"internacional\" : true \n" +
            "}"

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

        assertEquals( expectedObjectWithTabs, JsonObject(jsonObj).toJsonString)
    }

    @Test
    fun testJsonObjectDepth(){
        assertEquals(1, JsonObject(jsonObj).depth)
    }

    @Test
    fun testJsonObjectArray(){
        assertEquals( "[\n true ,\n true ,\n false \n]", jsonObjArray.toJsonString)
    }

    @Test
    fun testEmptyJsoObject(){
        assertEquals( "{ }", JsonObject().toJsonString)
    }

    @Test
    fun testEmptyJsoArray(){
        assertEquals( "[ ]", JsonArray().toJsonString)
    }


}
