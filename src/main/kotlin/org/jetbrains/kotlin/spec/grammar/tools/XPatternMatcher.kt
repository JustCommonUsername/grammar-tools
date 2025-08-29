package org.jetbrains.kotlin.spec.grammar.tools

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.jetbrains.kotlin.spec.grammar.KotlinLexer
import org.jetbrains.kotlin.spec.grammar.KotlinParser
import org.jetbrains.kotlin.spec.grammar.KotlinParser.ExpressionContext
import org.jetbrains.kotlin.spec.grammar.KotlinParser.IdentifierContext
import org.jetbrains.kotlin.spec.grammar.tools.util.getRulesByPattern

fun main() {
    val input = """
        val x = add(10)
        
        fun test() {
            while(true) { doSomething() }
        }
    """.trimIndent()
    val lexer = KotlinLexer(CharStreams.fromString(input))
    val parser = KotlinParser(CommonTokenStream(lexer))
    val tree = parser.kotlinFile()

    println(tree.toStringTree(parser))

    val expressions = parser.getRulesByPattern<ExpressionContext>(
        tree, "add ( <expression> )"
    )

    expressions.forEach { it.addChild(IdentifierContext(it, it.invokingState)) }

    println(expressions.map { it.toStringTree(parser) })
}
