import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

/**
 * Data class fields Returns a list of atributes in the order of the primary constructor
 */
val KClass<*>.dataClassFields: List<KProperty<*>>
    get() {
        require(isData) { "instance must be data class" }
        return primaryConstructor!!.parameters.map { p ->
            declaredMemberProperties.find { it.name == p.name }!!
        }
    }
//TODO MISSING COMMENTS
// saber se um KClassifier é um enumerado
val KClassifier?.isEnum: Boolean
    get() = this is KClass<*> && this.isSubclassOf(Enum::class)

// obter uma lista de constantes de um tipo enumerado
val KClassifier?.enumConstants: List<*>
    get() {
        require(isEnum) { "instance must be enum" }
        return asClass.java.enumConstants.toList()
    }

val KClassifier?.asClass: KClass<*>
    get() {
        require(this is KClass<*>) { "instance must be KClass"}
        return this
    }

/**
 * Instanciate json Given an object this function allows to instanciate automatically the model, through reflexion
 *
 * @param obj The object to be instanciated, this can be a data class, collection, map, primitive type, string and enumerate
 * @return The obejct's corresponding Json Value
 */
fun instanciateJson(obj : Any?):JsonValue {
    return when (obj) {
        is String -> JsonString(obj)
        is Number -> JsonNumber(obj)
        is Boolean -> JsonBoolean(obj)
        null -> JsonNull()
        is Map<*, *> -> //JsonObject(obj.entries.associate { it.key.toString() to instanciateJson(it.value) })
            //if the object was empty, it was creating it with something inside
            /*
            if (obj.isEmpty())
                JsonObject()
            else
                JsonObject(obj.entries.associate { it.key.toString() to instanciateJson(it.value) })

             */
            JsonObject(obj.entries.associate { it.key.toString() to instanciateJson(it.value) }).takeIf { it.properties!!.isNotEmpty() } ?: JsonObject()
        is Collection<*> -> //JsonArray(obj.map { instanciateJson(it) })
            JsonArray(obj.map { instanciateJson(it) }).takeIf { it.valueList!!.isNotEmpty()} ?: JsonArray()
        //MOST LIKELY WILL ALSO REQUIRE VERIFICATION IF THE OBJ IS EMPTY
        is Enum<*> -> instanciateJson(obj)
        else -> {
            if (obj::class.isData) {
                val propertyList = obj::class.dataClassFields
                return JsonObject(
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

/**
 * Ignore property Annotation is used to mark a property in a data class to be ignored during the serialization process.
 * Any property marked with this annotation will be skipped and not included in the generated Json Object.
 *
 */
@Target(AnnotationTarget.PROPERTY)
annotation class IgnoreProperty

/**
 * ForceJsonString annotation is used to mark a property in a data class to be serialized as a JSON string regardless of its original data type.
 *
 * @constructor Create empty Force json string
 */
@Target(AnnotationTarget.PROPERTY)
annotation class ForceJsonString

/**
 * Custom identifier Annotation used to indicate a custom identifier for a property when instantiating JSON.
 *
 * @property newType The new type to be used for the property when instantiating JSON.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class CustomIdentifier(val newType: String)

/**
 * Allowed return type Checks if the return type of property is allowed for conversion to JsonString,
 * only allows conversion for Int and Boolean return types
 *
 * @param p The property to be checked
 * @return True if the return type is allowed, false otherwise
 */
fun allowedReturnType(p: KProperty<*>):Boolean =
    when(p.returnType.classifier){
        Int::class ->  true
        Boolean::class ->  true
        else ->  false
    }

/**
 * Changes the return type of the input value to the specified new type
 *
 * @param value The instance to be converted.
 * @param newType A string representing the new type.
 * @return An instance of the new type.
 * @throws IllegalArgumentException if the specified new type is not recognized (i.e., not "string", "boolean", or "int").
 */
fun changeReturnType(value: Any?, newType: String):Any{
    return when(newType){
        "string" -> value.toString()
        "boolean" -> value.toString().toBoolean()
        "int" -> value.toString().toInt()
        else -> throw IllegalArgumentException("Unrecognized type, try: string, boolean, int")
    }
}