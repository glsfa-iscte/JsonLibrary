import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
class TestingReflexionInstantiation {

    //TODO add test to enumerate (check if instanciateJson enumerate needs to be changed)and more annotations
    internal val insc01 = mapOf(
        "numero" to 101101,
        "nome" to "Dave Farley",
        "internacional" to true
    )

    internal val insc02 = mapOf(
        "numero" to 101102,
        "nome" to "Martin Fowler",
        "internacional" to true
    )

    internal val insc03 = mapOf(
        "numero" to 92888,
        "nome" to "Gustavo Ferreira",
        "internacional" to false
    )

    internal val inscritos = listOf(insc01, insc02, insc03)

    internal val inscricoes = mapOf(
        "uc" to "PA",
        "ects" to 6.0,
        "data-exame" to null,
        "inscritos" to inscritos
    )

    internal val inscricoes03b = (mapOf(
        "uc" to ("PA"),
        "ects" to (6.0),
        "data-exame" to null,
        "inscritos" to (listOf(
            insc02,
            (mapOf(
                "numero" to (92888),
                "nome" to ("Gustavo Ferreira"),
                "internacional" to (false),
                "inscritos" to (listOf(
                    (26503),
                    null,
                    (mapOf(
                        "numero" to (101104),
                        "test" to (mapOf(
                            "emptyArray" to emptyList<Any>()
                        )),
                        "emptyObject" to emptyMap<Any, Any>()
                    ))
                ))
            ))
        ))
    ))

    internal val inscricoes04b = mapOf(
        "uc" to "PA",
        "ects" to 6.0,
        "data-exame" to null,
        "inscritos" to (listOf(
            (mapOf(
                "numero" to (101101),
                "nome" to ("Dave Farley"),
                "internacional" to (true)
            )),
            (mapOf(
                "numero" to (101102),
                "nome" to ("Jane Doe"),
                "intern" to (mapOf(
                    "x" to (true),
                    "y" to (false)
                ))
            )),
            (mapOf(
                "numero" to (101103),
                "nome" to ("John Who"),
                "internacional" to (true)
            )),
            (mapOf(
                "numero" to (101104),
                "nome" to ("William That"),
                "x" to (true)
            )),
            (mapOf(
                "numero" to (101105),
                "nome" to ("Who This"),
                "internacional" to (listOf(
                    ("Me"),
                    (mapOf(
                        "notIt" to (true),
                        "x" to (1),
                        "y" to ("Hi"),
                        "numero" to (10)
                    )),
                    (listOf(
                        (mapOf(
                            "x" to (2),
                            "y" to ("Hi")
                        ))
                    ))
                ))
            ))
        ))
    )

    enum class TestEnumNumbers(val value: Int) {
        ONE(1),
        TWO(2),
        THREE(3)
    }

    enum class TestEnumString {
        A,
        B,
        C
    }

    enum class Color(val rgb: Int) {
        RED(0xFF0000),
        GREEN(0x00FF00),
        BLUE(0x0000FF)
    }

    @Test
    fun testEnumJsonConversion() {

        assertEquals(JsonString("RED"), instanciateJson(Color.RED))
        assertEquals(JsonString("GREEN"), instanciateJson(Color.GREEN))
        assertEquals(JsonString("BLUE"), instanciateJson(Color.BLUE))
        assertEquals(JsonString("A"), instanciateJson(TestEnumString.A))
        assertEquals(JsonString("B"), instanciateJson(TestEnumString.B))
        assertEquals(JsonString("C"), instanciateJson(TestEnumString.C))
        assertEquals(JsonString("ONE"), instanciateJson(TestEnumNumbers.ONE))
        assertEquals(JsonString("TWO"), instanciateJson(TestEnumNumbers.TWO))
        assertEquals(JsonString("THREE"), instanciateJson(TestEnumNumbers.THREE))
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

        val jsonObj09 = instanciateJson(inscricoes)
        val jsonObj10 = instanciateJson(inscricoes03b)
        val jsonObj11 = instanciateJson(inscricoes04b)

        println(jsonObj01)
        println(jsonObj02)
        println(jsonObj03)
        println(jsonObj04)
        println(jsonObj05)
        println(jsonObj06)
        println(jsonObj07)
        println(jsonObj08)

        assertEquals(fullExample01,jsonObj09.toJsonString)
        assertEquals(fullExample03,jsonObj10.toJsonString)
        assertEquals(fullExample04,jsonObj11.toJsonString)

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