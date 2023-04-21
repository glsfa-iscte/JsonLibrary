import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

//TODO CHECK LAST CLASS AND APPLY THE CONCEPTS TO THIS PROJECT
sealed interface JsonValue {
    val toJsonString: String
}

sealed interface JsonStructure : JsonValue {
    var depth: Int
    override val toJsonString: String
        get() = ""
    abstract fun accept(visitor: Visitor)
}

data class JsonString(
    val value: String,
) : JsonValue {
    override val toJsonString: String
        get() = " \"$value\" "
}

data class JsonNumber(
    val value: Number,
) : JsonValue {
    override val toJsonString: String
        get() = " $value "
}

data class JsonBoolean(
    val value: Boolean,
) : JsonValue {
    override val toJsonString: String
        get() = " $value "
}

data class JsonNull(
    val value: Any? = null,
) : JsonValue {
    override val toJsonString: String
        get() = " null "
}

data class JsonObject(
    val properties: Map<String, JsonValue>? = null
) : JsonStructure {

    override var depth: Int = 1

    init {
        properties?.values?.forEach {
            if (it is JsonStructure) {
                it.depth = this.depth + 1
            }
        }
    }

    override val toJsonString: String
        get() {
            return properties?.entries?.joinToString(
                separator = ",\n",
                prefix = "${"\t".repeat(depth - 1)}{\n",
                postfix = "\n${"\t".repeat(depth - 1)}}"
            ) { (name, value) ->
                "${"\t".repeat(depth)} \"$name\" :${value.toJsonString.trimEnd()} "
            } ?: "{ }"
        }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}


data class JsonArray(
    val valueList: List<JsonValue>? = null
) : JsonStructure {

    override var depth: Int = 1
    init {
        valueList?.forEach {
            if(it is JsonStructure)
                it.depth = this.depth+1
        }
    }

    override val toJsonString: String
        get() {
            return valueList?.joinToString(
                separator = ",\n",
                prefix = "[\n",
                postfix = "\n${" ".repeat(depth-1)}]",
            ) { it.toJsonString } ?: "[ ]"
        }
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

val KClass<*>.dataClassFields: List<KProperty<*>>
    get() {
        require(isData) { "instance must be data class" }
        return primaryConstructor!!.parameters.map { p ->
            declaredMemberProperties.find { it.name == p.name }!!

        }
    }

//TODO ADICIONAR UMA FUNCAO QUE VAI FAZER WHEN COM A ANOTACAO
fun instanciateJson(obj : Any?):JsonValue {
    return when (obj) {
        is String -> JsonString(obj)
        is Number -> JsonNumber(obj)
        is Boolean -> JsonBoolean(obj)
        null -> JsonNull()
        is Map<*, *> -> JsonObject(obj.entries.associate { it.key.toString() to instanciateJson(it.value) })
        is Collection<*> -> JsonArray(obj.map { instanciateJson(it) })
        is Enum<*> -> instanciateJson(obj)
        else -> {

            if (obj::class.isData) {
                val propertyList = obj::class.dataClassFields
                return JsonObject(
                    //posso fazer o filter do propertyList, que vai dar KProperty
                    propertyList.filter { !it.hasAnnotation<IgnoreProperty>() }.associate {
                        val value = it.call(obj)
                        if (it.hasAnnotation<ForceJsonString>() && allowedReturnType(it))
                            it.name to instanciateJson(value.toString())
                        else
                            if(it.hasAnnotation<CustomIdentifier>()){
                                val newType = it.findAnnotation<CustomIdentifier>()!!.newType
                                it.name to instanciateJson(changeReturnType(value, newType))
                            }
                        else
                                it.name to instanciateJson(value)
                    }
                )

            }
            return JsonObject()
        }
    }
}

@Target(AnnotationTarget.PROPERTY)
annotation class IgnoreProperty

@Target(AnnotationTarget.PROPERTY)
annotation class ForceJsonString
@Target(AnnotationTarget.PROPERTY)
annotation class CustomIdentifier(val newType: String)

//used in ForceJsonString so that it only converts to JsonString if the return type is in the wen structure
fun allowedReturnType(p: KProperty<*>):Boolean =
     when(p.returnType.classifier){
        Int::class ->  true
        Boolean::class ->  true
        else ->  false
}

fun changeReturnType(value: Any?, newType: String):Any{
    return when(newType){
        "string" -> value.toString()
        "boolean" -> value.toString().toBoolean()
        "int" -> value.toString().toInt()
        else -> throw IllegalArgumentException("Unrecognized type, try: string, boolean, int")
    }
}