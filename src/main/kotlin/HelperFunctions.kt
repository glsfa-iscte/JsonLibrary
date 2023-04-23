/**
 * Get json property types
 *
 * This function returns a collection of pairs representing the name and type of each property
 * that matches the given pattern in the JSON string.
 *
 * @param json The JSON string to parse
 * @param pattern The regular expression pattern to match against property names and types
 * @return A collection of pairs representing the name and type of each matching property
 */
fun getJsonPropertyTypes(json: String, pattern: String): Collection<Pair<String, String>> {
    val regex = pattern.toRegex()
    val properties = regex.findAll(json)
        .map { match -> match.groupValues[1] to match.groupValues[2] }
        .toList()
    return properties
}