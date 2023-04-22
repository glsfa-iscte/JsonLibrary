import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
class ModelTesting {
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
        assertEquals( "[\n\t true ,\n\t true ,\n\t false \n]", jsonArray.toJsonString)
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
    fun testDepth01(){
        assertEquals(1, inscricoes01.depth)
    }
    @Test
    fun testDepth02(){
        assertEquals(2, inscritos.depth)
    }
    @Test
    fun testDepth03(){
         assertEquals(3, insc01.depth)
    }

    @Test
    fun testFullExample01(){
        assertEquals(fullExample01, inscricoes01.toJsonString)
    }

    @Test
    fun testFullExample02(){
        assertEquals(fullExample02, inscricoes02.toJsonString)
    }

    @Test
    fun testFullExample03(){
        assertEquals(fullExample03, inscricoes03.toJsonString)
    }
}