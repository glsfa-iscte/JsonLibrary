import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

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
fun instanciateJson(obj : Any?):JsonValue{
    when (obj) {
        is String -> return JsonString(obj)
        is Number -> return JsonNumber(obj)
        is Boolean -> return JsonBoolean(obj)
        null -> return JsonNull()
        is Map<*, *> -> return JsonObject(obj.entries.associate { it.key.toString() to instanciateJson(it.value) })
        is Collection<*> -> return JsonArray(obj.map { instanciateJson(it) })
        is Enum<*> -> return instanciateJson(obj)
    }
    //Preciso de adicionar uma maneira para, caso tenha uma anotacao, ele fazer esse comportamento
    if (obj!!::class.isData && !hasIgnorePropertyAnnotation(obj::class)) {
        val propertyList = obj::class.dataClassFields
        return JsonObject(
            //posso fazer o filter do propertyList, que vai dar KProperty
            propertyList.filter{!hasIgnorePropertyAnnotation(it)}.associate { it.name to instanciateJson(it.call(obj)) }
        )

    }
    return JsonObject()
}

//@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS
    //, NAO SEI SE POSSO ADICIONAR O ABAIXO, SE NÃO PUDER, VOU TER QUE MUDAR A LÓGICA JA QUE MNÃO POSSO FAZER
    // val testMap = mapOf<Any?, Any?>(
    //            @IgnoreProperty
    //            "Test" to testString,
//, AnnotationTarget.EXPRESSION
//)
//@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class IgnoreProperty

@Target(AnnotationTarget.PROPERTY)
annotation class ForceJsonString

fun hasIgnorePropertyAnnotation(obj: Any?) : Boolean {
    return when(obj){
        is KClass<*> -> obj.hasAnnotation<IgnoreProperty>()
        is KProperty<*> -> obj.hasAnnotation<IgnoreProperty>()
        is Map.Entry<*, *> -> obj.key is KProperty<*> && (obj.key as KProperty<*>).hasAnnotation<IgnoreProperty>()
        else -> false
    }
}

