# JSON Library

This is a simple implementation of a JSON (JavaScript Object Notation) value model in Kotlin. It is a set of classes and interfaces that represent the different types of values that can be present in a JSON structure.

## How to use

The library defines the following classes:

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
