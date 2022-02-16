package org.rsmod.game.model.enum.type

import org.rsmod.game.cache.type.ConfigType

data class EnumType(
    override val id: Int,

    val size: Int,
    val keys: List<Int>,

    val keyType: EnumVarType,
    val valueType: EnumVarType,

    val defaultString: String,
    val defaultInt: Int,

    val stringValues: List<String>,
    val intValues: List<Int>,
) : ConfigType

enum class EnumVarType(val keyChar: Char, val fullName: String) {
    INTEGER('i', "integer"),
    BOOLEAN('1', "boolean"),
    SEQ('A', "seq"),
    COLOUR('C', "colour"),
    /**
     * Also known as {@code Widget}.
     */
    COMPONENT('I', "component"),
    ID_KIT('K', "id-kit"),
    MIDI('M', "midi"),
    SYNTH('P', "synth"),
    STAT('S', "stat"),
    COORDINATE_GRID('c', "coordinate-grid"),
    GRAPHIC('d', "graphic"),
    FONT_METRICS('f', "font-metrics"),
    ENUM('g', "enum"),
    JINGLE('j', "jingle"),
    /**
     * Also known as {@code ObjectType}.
     */
    LOC('l', "loc"),
    MODEL('m', "model"),
    NPC('n', "npc"),
    /**
     * Also known as {@code ItemType}.
     */
    OBJ('o', "obj"),
    /**
     * Another version of {@code OBJ}, but means that on Jagex's side they used the internal name for an item.
     */
    NAMED_OBJ('O', "named-obj"),
    STRING('s', "string"),
    SPOT_ANIM('t', "spot-anim"),
    INV('v', "inv"),
    TEXTURE('x', "texture"),
    CHAR('z', "char"),
    MAP_SCENE_ICON('£', "map-scene-icon"),
    MAP_ELEMENT('µ', "map-element"),
    MAP_AREA('`', "map-area"),
    HIT_MARK('×', "hit-mark"),
    STRUCT('J', "struct"),
    AREA('R', "area"),
    CATEGORY('y', "category"),
}

fun Char.toEnumVarType(): EnumVarType
    = EnumVarType.values().single { it.keyChar == this }
