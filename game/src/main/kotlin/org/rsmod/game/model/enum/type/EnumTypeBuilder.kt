package org.rsmod.game.model.enum.type

private const val DEFAULT_ID = -1
private const val DEFAULT_INT_VALUE = -1
private const val DEFAULT_STRING_VALUE = "null"

private val DEFAULT_KEY_TYPE = EnumVarType.STRING
private val DEFAULT_INT_ARRAY = IntArray(0)
private val DEFAULT_STRING_ARRAY = arrayOf<String>()

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class EnumTypeBuilder(
    var id: Int = DEFAULT_ID,

    var size: Int = DEFAULT_INT_VALUE,
    var keys: IntArray = DEFAULT_INT_ARRAY,

    var keyType: EnumVarType = DEFAULT_KEY_TYPE,
    var valueType: EnumVarType = DEFAULT_KEY_TYPE,

    var defaultString: String = DEFAULT_STRING_VALUE,
    var defaultInt: Int = DEFAULT_INT_VALUE,

    var stringValues: Array<String> = DEFAULT_STRING_ARRAY,
    var intValues: IntArray = DEFAULT_INT_ARRAY,
) {

    fun build(): EnumType {
        return EnumType(
            id = id,
            size = size,
            keys = keys.asList(),
            keyType = keyType,
            valueType = valueType,
            defaultString = defaultString,
            defaultInt = defaultInt,
            stringValues = stringValues.asList(),
            intValues = intValues.asList()
        )
    }
}
