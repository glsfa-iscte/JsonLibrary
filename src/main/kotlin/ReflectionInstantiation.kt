import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

/**
 * Data class fields Returns the list of all properties of the primary constructor parameters of a data class.
 *
 * @return the list of all properties of the primary constructor parameters of a data class
 * @throws IllegalArgumentException if the instance is not a data class
 */
val KClass<*>.dataClassFields: List<KProperty<*>>
    get() {
        require(isData) { "instance must be data class" }
        return primaryConstructor!!.parameters.map { p ->
            declaredMemberProperties.find { it.name == p.name }!!
        }
    }

/**
 * Is enum Verifies if the instance is an enum class.
 *
 * @return true if the instance is an enum class, false otherwise
 */
val KClassifier?.isEnum: Boolean
    get() = this is KClass<*> && this.isSubclassOf(Enum::class)

/**
 * Enum constants Returns a list of all constants of an enum class.
 *
 * @return a list of all constants of an enum class
 * @throws IllegalArgumentException if the instance is not an enum class
 */
val KClassifier?.enumConstants: List<*>
    get() {
        require(isEnum) { "instance must be enum" }
        return asClass.java.enumConstants.toList()
    }

/**
 * As Class Returns the receiver instance as a KClass object.
 * Throws an IllegalArgumentException if the receiver is not an instance of KClass.
 * @return the receiver instance as a KClass object
 * @throws IllegalArgumentException if the receiver is not an instance of KClass
 */
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
        is Map<*, *> -> JsonObject(obj.entries.associate { it.key.toString() to instanciateJson(it.value) }).takeIf { it.properties!!.isNotEmpty() } ?: JsonObject()
        is Collection<*> -> JsonArray(obj.map { instanciateJson(it) }).takeIf { it.valueList!!.isNotEmpty()} ?: JsonArray()
        is Enum<*> ->  JsonString(obj.name)//JsonObject(mapOf("name" to JsonString(obj.name), "ordinal" to JsonNumber(obj.ordinal)))//instanciateJson(obj)
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