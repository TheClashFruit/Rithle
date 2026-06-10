package me.theclashfruit.rithle.enums

enum class Operation(val value: String) {
    Equal("="),
    Smaller("<"),
    Greater(">"),
    SmallerOrEqual("<="),
    GreaterOrEqual(">="),
    NotEqual("!=");
}