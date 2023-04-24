import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
class TestingReflexionInstantiation {
    //TODO MORE TESTS FOR THE instanciateJson and Annotations
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