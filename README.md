# JSON Library 

# Model tuturial

This is a simple implementation of a JSON (JavaScript Object Notation) value model in Kotlin. It is a set of classes and interfaces that represent the different types of values that can be present in a JSON structure.

## How to use it:

This library defines the following classes:

- `JsonString`: represents a JSON string value.
- `JsonNumber`: represents a JSON numeric value.
- `JsonBoolean`: represents a JSON boolean value.
- `JsonNull`: represents a JSON null value.
- `JsonObject`: represents a JSON object, which is a collection of key-value pairs where the keys are strings and the values can be any JSON value.
- `JsonArray`: represents a JSON array, which is an ordered collection of values, where each value can be any JSON value.

To use this model, you can create instances of the different classes and build your JSON structure as needed.

For example, to create a simple JSON object with a string and a number value, you could do the following:

```kotlin
val myJson = JsonObject(mapOf(
    "name" to JsonString("John Doe"),
    "age" to JsonNumber(30)
))
```

This would create a JSON object with two key-value pairs: `"name": "John Doe"` and `"age": 30`.

To convert a JSON value to a JSON string, you can call the `toJsonString` property on any instance of `JsonValue`.

For example:

```kotlin
val myJson = JsonObject(mapOf(
    "name" to JsonString("John Doe"),
    "age" to JsonNumber(30)
))
val jsonString = myJson.toJsonString
```

This would generate the following JSON string:

```json
{
    "name": "John Doe",
    "age": 30
}
```

Note that the generated JSON string is properly indented and formatted for readability.

# Visitor Pattern Tutorial

This tutorial will guide you on how to use the Visitor pattern in Kotlin. We'll use the provided code as an example, which demonstrates how to perform various operations on a JSON structure using the Visitor pattern. The operations include:

1. Get all values stored in properties with a specific identifier.
2. Get all objects that have properties with specific names.
3. Verify that the model complies with certain structure requirements.

## Visitor Interface

```kotlin
interface Visitor {
    fun visit(jsonElement: JsonObject)
    fun visit(jsonElement: JsonArray)
}
```

The `Visitor` interface defines two methods: `visit` for `JsonObject` and `JsonArray`. This interface allows adding new operations without modifying the existing classes.

## GetValuesByPropertyNameVisitor

```kotlin
class GetValuesByPropertyNameVisitor(val name: String) : Visitor {
    val lst = mutableListOf<String>()

    override fun visit(jsonElement: JsonObject) {
        jsonElement.properties?.forEach {
            if (it.key == name)
                lst.add(it.value.toJsonString)
            if (it.value is JsonStructure) (it.value as JsonStructure).accept(this)
        }
    }

    override fun visit(jsonElement: JsonArray) {
        jsonElement.valueList?.forEach {
            if (it is JsonStructure) it.accept(this)
        }
    }
}
```

The `GetValuesByPropertyNameVisitor` class implements the `Visitor` interface. It searches for a property with a given name in a `JsonObject` and collects its values in the `lst` mutable list. If the property value is a `JsonStructure`, it recursively calls the corresponding `accept` method.

## getValuesByPropertyName Function

```kotlin
fun getValuesByPropertyName(jsonElement: JsonStructure, name: String): MutableList<String> {
    val visitor = GetValuesByPropertyNameVisitor(name)
    jsonElement.accept(visitor)
    return visitor.lst
}
```

The `getValuesByPropertyName` function accepts a `JsonStructure` and a property name as input. It creates an instance of the `GetValuesByPropertyNameVisitor` and calls `accept` on the `jsonElement` to start the visiting process. Finally, it returns the collected values in the `lst` list.

## GetObjectByPropertiesNamesVisitor

```kotlin
class GetObjectByPropertiesNamesVisitor(val names: List<String>) : Visitor {
    val lst = mutableListOf<JsonObject>()

    override fun visit(jsonElement: JsonObject) {
        if (jsonElement.properties?.keys?.containsAll(names) == true)
            lst.add(jsonElement)
        jsonElement.properties?.forEach {
            if (it.value is JsonStructure) (it.value as JsonStructure).accept(this)
        }
    }

    override fun visit(jsonElement: JsonArray) {
        jsonElement.valueList?.forEach {
            if (it is JsonStructure) it.accept(this)
        }
    }
}
```

The `GetObjectByPropertiesNamesVisitor` class implements the `Visitor` interface. It searches for objects (`JsonObject`) that have properties with specific names. If an object has all the specified property names, it adds itself to the `lst` list. It also recursively visits any nested `JsonStructure` elements.

## getObjectsWithSpecificNameValue Function

```kotlin
fun getObjectsWithSpecificNameValue(jsonElement: JsonStructure, properties: List<String>): MutableList<JsonObject> {


    val visitor = GetObjectByPropertiesNamesVisitor(properties)
    jsonElement.accept(visitor)
    return visitor.lst
}
```

The `getObjectsWithSpecificNameValue` function accepts a `JsonStructure` and a list of property names as input. It creates an instance of `GetObjectByPropertiesNamesVisitor` and calls `accept` on the `jsonElement` to initiate the visiting process. It returns the collected objects that have the specified property names in the `lst` list.

## JsonSearchVisitor

```kotlin
class JsonSearchVisitor(private val searchPredicate: (JsonObject) -> Boolean) : Visitor {
    val matchingObjects = mutableListOf<JsonObject>()

    override fun visit(jsonElement: JsonObject) {
        if (searchPredicate(jsonElement))
            matchingObjects.add(jsonElement)
        jsonElement.properties?.values?.forEach {
            if (it is JsonStructure) it.accept(this)
        }
    }

    override fun visit(jsonElement: JsonArray) {
        jsonElement.valueList?.forEach {
            if (it is JsonStructure) it.accept(this)
        }
    }
}
```

The `JsonSearchVisitor` class implements the `Visitor` interface. It performs a search operation based on a given search predicate, which is a lambda function that takes a `JsonObject` and returns a Boolean indicating whether the object matches the desired search criteria. If a match is found, the object is added to the `matchingObjects` list. It recursively visits any nested `JsonStructure` elements.

## searchJson Function

```kotlin
fun searchJson(jsonElement: JsonStructure, searchPredicate: (JsonObject) -> Boolean): List<JsonObject> {
    val visitor = JsonSearchVisitor(searchPredicate)
    jsonElement.accept(visitor)
    return visitor.matchingObjects
}
```

The `searchJson` function accepts a `JsonStructure` and a search predicate as input. It creates an instance of `JsonSearchVisitor` with the provided search predicate and calls `accept` on the `jsonElement` to initiate the visiting process. It returns a list of `JsonObject` instances that match the search criteria.

## Example Usage

Now that we have the necessary classes and functions, let's see how we can use them to perform the desired operations.

### Getting Values by Property Name

```kotlin
val jsonString = // JSON string
val jsonElement = parseJson(jsonString) // Parse the JSON string into a JsonStructure

val values = getValuesByPropertyName(jsonElement, "number")
println(values) // Prints all values stored in properties with the identifier "number"
```

The `getValuesByPropertyName` function is used to retrieve all values stored in properties with the identifier "number" from a JSON structure.

### Getting Objects with Specific Property Names

```kotlin
val jsonString = // JSON string
val jsonElement = parseJson(jsonString) // Parse the JSON string into a JsonStructure

val properties = listOf("number", "name")
val objects = getObjectsWithSpecificNameValue(jsonElement, properties)
println(objects) // Prints all objects that have properties "number" and "name"
```

The `getObjectsWithSpecificNameValue` function is used to obtain all objects that have properties with the names "number" and "name" from a JSON structure.

### Searching JSON with Custom Criteria

```kotlin
val jsonString = // JSON string
val jsonElement = parseJson(jsonString) // Parse the JSON string into a JsonStructure

val matchingObjects = searchJson(jsonElement) { jsonObject ->
    jsonObject.properties?.containsKey("number") && jsonObject.properties.containsKey("name")
}
println(matchingObjects) // Prints all objects that have properties "number" and "name"
```

The `searchJson` function is used to perform a custom search operation on a

 JSON structure. In this example, it searches for objects that have properties "number" and "name" using a custom search predicate.
