internal val number = JsonNumber( 101101)

internal val name = JsonString("Dave Farley")

internal val internacional = JsonBoolean(true)

internal val jsonArray = JsonArray(listOf(JsonBoolean(true), JsonBoolean(true), JsonBoolean(false)))

internal val expectedObjectIndentation =
    "{\n" +
    "\t \"numero\" : 101101,\n" +
    "\t \"nome\" : \"Dave Farley\",\n" +
    "\t \"internacional\" : true\n" +
    "}"
internal val jsonObj = mapOf("numero" to number,
    "nome" to name,
    "internacional" to internacional)

internal val fullExample01: String =
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

internal val fullExample02: String =
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
    "\t\t\t \"internacional\" : false,\n" +
    "\t\t\t \"inscritos\" : [\n" +
    "\t\t\t\t 26503 ,\n" +
    "\t\t\t\t null ,\n" +
    "\t\t\t\t{\n" +
    "\t\t\t\t\t \"numero\" : 101103\n" +
    "\t\t\t\t}\n" +
    "\t\t\t]\n" +
    "\t\t}\n" +
    "\t]\n" +
    "}"

internal val fullExample03: String =
    "{\n" +
    "\t \"uc\" : \"PA\",\n" +
    "\t \"ects\" : 6.0,\n" +
    "\t \"data-exame\" : null,\n" +
    "\t \"inscritos\" : [\n" +
    "\t\t{\n" +
    "\t\t\t \"numero\" : 101102,\n" +
    "\t\t\t \"nome\" : \"Martin Fowler\",\n" +
    "\t\t\t \"internacional\" : true\n" +
    "\t\t},\n" +
    "\t\t{\n" +
    "\t\t\t \"numero\" : 92888,\n" +
    "\t\t\t \"nome\" : \"Gustavo Ferreira\",\n" +
    "\t\t\t \"internacional\" : false,\n" +
    "\t\t\t \"inscritos\" : [\n" +
    "\t\t\t\t 26503 ,\n" +
    "\t\t\t\t null ,\n" +
    "\t\t\t\t{\n" +
    "\t\t\t\t\t \"numero\" : 101104,\n" +
    "\t\t\t\t\t \"test\" : {\n" +
    "\t\t\t\t\t\t \"emptyArray\" : [ ]\n" +
    "\t\t\t\t\t},\n" +
    "\t\t\t\t\t \"emptyObject\" : { }\n" +
    "\t\t\t\t}\n" +
    "\t\t\t]\n" +
    "\t\t}\n" +
    "\t]\n" +
    "}"

internal val fullExample04: String =
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
    "\t\t\t \"nome\" : \"Jane Doe\",\n" +
    "\t\t\t \"intern\" : {\n" +
    "\t\t\t\t \"x\" : true,\n" +
    "\t\t\t\t \"y\" : false\n" +
    "\t\t\t}\n" +
    "\t\t},\n" +
    "\t\t{\n" +
    "\t\t\t \"numero\" : 101103,\n" +
    "\t\t\t \"nome\" : \"John Who\",\n" +
    "\t\t\t \"internacional\" : true\n" +
    "\t\t},\n" +
    "\t\t{\n" +
    "\t\t\t \"numero\" : 101104,\n" +
    "\t\t\t \"nome\" : \"William That\",\n" +
    "\t\t\t \"x\" : true\n" +
    "\t\t},\n" +
    "\t\t{\n" +
    "\t\t\t \"numero\" : 101105,\n" +
    "\t\t\t \"nome\" : \"Who This\",\n" +
    "\t\t\t \"internacional\" : [\n" +
    "\t\t\t\t \"Me\" ,\n" +
    "\t\t\t\t{\n" +
    "\t\t\t\t\t \"notIt\" : true,\n" +
    "\t\t\t\t\t \"x\" : 1,\n" +
    "\t\t\t\t\t \"y\" : \"Hi\",\n" +
    "\t\t\t\t\t \"numero\" : 10\n" +
    "\t\t\t\t},\n" +
    "\t\t\t\t[\n" +
    "\t\t\t\t\t{\n" +
    "\t\t\t\t\t\t \"x\" : 2,\n" +
    "\t\t\t\t\t\t \"y\" : \"Hi\"\n" +
    "\t\t\t\t\t}\n" +
    "\t\t\t\t]\n" +
    "\t\t\t]\n" +
    "\t\t}\n" +
    "\t]\n" +
    "}"
internal val insc01 = JsonObject(mapOf(
    "numero" to JsonNumber(101101),
    "nome" to JsonString("Dave Farley"),
    "internacional" to JsonBoolean(true)
))

internal val insc02 = JsonObject(mapOf(
    "numero" to JsonNumber(101102),
    "nome" to JsonString("Martin Fowler"),
    "internacional" to JsonBoolean(true)
))

internal val insc03 = JsonObject(mapOf(
    "numero" to JsonNumber(92888),
    "nome" to JsonString("Gustavo Ferreira"),
    "internacional" to JsonBoolean(false)
))

internal val inscritos = JsonArray(listOf(insc01, insc02, insc03))

internal val inscricoes01 = JsonObject(mapOf(
    "uc" to JsonString("PA"),
    "ects" to JsonNumber(6.0),
    "data-exame" to JsonNull(),
    "inscritos" to inscritos
))

internal val insc01b = JsonObject(mapOf(
    "numero" to JsonNumber(101101),
    "nome" to JsonString("Dave Farley"),
    "internacional" to JsonBoolean(true),
    "subInscritos" to JsonObject(mapOf(
        "entry" to JsonBoolean(true)
    ))

))

internal val insc02b = JsonObject(mapOf(
    "numero" to JsonNumber(101102),
    "nome" to JsonString("Martin Fowler"),
    "internacional" to JsonBoolean(true),
    "subInscritos" to JsonObject(mapOf(
        "entry" to JsonBoolean(true)
    ))
))

internal val insc03b = JsonObject(mapOf(
    "numero" to JsonNumber(92888),
    "nome" to JsonString("Gustavo Ferreira"),
    "internacional" to JsonBoolean(true),
    "subInscritos" to JsonObject(mapOf(
        "entry" to JsonBoolean(true)
    ))
))

internal val inscritosb = JsonArray(listOf(insc01b, insc02b, insc03b))

internal val inscricoes01b = JsonObject(mapOf(
    "uc" to JsonString("PA"),
    "ects" to JsonNumber(6.0),
    "data-exame" to JsonNull(),
    "inscritos" to inscritosb
))



internal val insc01c = JsonObject(mapOf(
    "numero" to JsonNumber(101101),
    "nome" to JsonString("Dave Farley"),
    "internacional" to JsonBoolean(true),
    "subInscritos" to JsonObject(mapOf(
        "entry" to JsonBoolean(true),
        "b" to JsonArray(listOf(
            JsonString("abc"),
            JsonBoolean(true)
        ))
    ))

))

internal val insc02c = JsonObject(mapOf(
    "numero" to JsonNumber(101102),
    "nome" to JsonString("Martin Fowler"),
    "internacional" to JsonBoolean(true),
    "subInscritos" to JsonObject(mapOf(
        "entry" to JsonBoolean(true),
        "b" to JsonArray(listOf(
            JsonString("abc"),
            JsonBoolean(true)
        ))
    ))
))

internal val insc03c = JsonObject(mapOf(
    "numero" to JsonNumber(92888),
    "nome" to JsonString("Gustavo Ferreira"),
    "internacional" to JsonBoolean(true),
    "subInscritos" to JsonObject(mapOf(
        "entry" to JsonBoolean(true),
        "b" to JsonArray(listOf(
            JsonString("abc"),
            //JsonNumber(123)
            JsonBoolean(true)
        ))
    ))
))

internal val inscritosc = JsonArray(listOf(insc01c, insc02c, insc03c))

internal val inscricoes01c = JsonObject(mapOf(
    "uc" to JsonString("PA"),
    "ects" to JsonNumber(6.0),
    "data-exame" to JsonNull(),
    "inscritos" to inscritosc
))

internal val inscricoes02 = JsonObject(mapOf(
    "uc" to JsonString("PA"),
    "ects" to JsonNumber(6.0),
    "data-exame" to JsonNull(),
    "inscritos" to JsonArray(listOf(
        insc01,
        insc02,
        JsonObject(mapOf(
            "numero" to JsonNumber(92888),
            "nome" to JsonString("Gustavo Ferreira"),
            "internacional" to JsonBoolean(false),
            "inscritos" to JsonArray(listOf(
                JsonNumber(26503),
                JsonNull(),
                JsonObject(mapOf(
                    "numero" to JsonNumber(101103)
                ))
            ))
        ))
    ))
))

internal val inscricoes03 = JsonObject(mapOf(
    "uc" to JsonString("PA"),
    "ects" to JsonNumber(6.0),
    "data-exame" to JsonNull(),
    "inscritos" to JsonArray(listOf(
        insc02,
        JsonObject(mapOf(
            "numero" to JsonNumber(92888),
            "nome" to JsonString("Gustavo Ferreira"),
            "internacional" to JsonBoolean(false),
            "inscritos" to JsonArray(listOf(
                JsonNumber(26503),
                JsonNull(),
                JsonObject(mapOf(
                    "numero" to JsonNumber(101104),
                    "test" to JsonObject(mapOf(
                        "emptyArray" to JsonArray()
                    )),
                    "emptyObject" to JsonObject()
                ))
            ))
        ))
    ))
))

internal val inscricoes04 = JsonObject(mapOf(
    "uc" to JsonString("PA"),
    "ects" to JsonNumber(6.0),
    "data-exame" to JsonNull(),
    "inscritos" to JsonArray(listOf(
        JsonObject(mapOf(
            "numero" to JsonNumber(101101),
            "nome" to JsonString("Dave Farley"),
            "internacional" to JsonBoolean(true)
        )),
        JsonObject(mapOf(
            "numero" to JsonNumber(101102),
            "nome" to JsonString("Jane Doe"),
            "intern" to JsonObject(mapOf(
                "x" to JsonBoolean(true),
                "y" to JsonBoolean(false)
            ))
        )),
        JsonObject(mapOf(
            "numero" to JsonNumber(101103),
            "nome" to JsonString("John Who"),
            "internacional" to JsonBoolean(true)
        )),
        JsonObject(mapOf(
            "numero" to JsonNumber(101104),
            "nome" to JsonString("William That"),
            "x" to JsonBoolean(true)
        )),
        JsonObject(mapOf(
            "numero" to JsonNumber(101105),
            "nome" to JsonString("Who This"),
            "internacional" to JsonArray(listOf(
                JsonString("Me"),
                JsonObject(mapOf(
                    "notIt" to JsonBoolean(true),
                    "x" to JsonNumber(1),
                    "y" to JsonString("Hi"),
                    "numero" to JsonNumber(10)
                )),
                JsonArray(listOf(
                    JsonObject(mapOf(
                        "x" to JsonNumber(2),
                        "y" to JsonString("Hi")
                    ))
                ))
            ))
        ))
    ))
))