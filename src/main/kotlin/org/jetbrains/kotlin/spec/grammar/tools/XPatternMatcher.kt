package org.jetbrains.kotlin.spec.grammar.tools

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.jetbrains.kotlin.spec.grammar.KotlinLexer
import org.jetbrains.kotlin.spec.grammar.KotlinParser
import org.jetbrains.kotlin.spec.grammar.KotlinParser.ExpressionContext
import org.jetbrains.kotlin.spec.grammar.KotlinParser.FunctionDeclarationContext
import org.jetbrains.kotlin.spec.grammar.KotlinParser.IdentifierContext
import org.jetbrains.kotlin.spec.grammar.tools.parsing.Parser
import org.jetbrains.kotlin.spec.grammar.tools.util.TypesafeTreeMatch
import org.jetbrains.kotlin.spec.grammar.tools.util.subtreeMatch
import org.jetbrains.kotlin.spec.grammar.tools.util.subtreeParser

fun main() {
    val input = """
        val x = add(10)
        
        fun test() {
            while(true) { doSomething() }
        }
    """.trimIndent()
    val parser = subtreeParser(input)
    val tree = parser.kotlinFile()

    println(tree.toStringTree(parser))

    val statement = "val x = doBeAddedInTheEnd()"
    val statementParser = subtreeParser(statement)
    val statementTree = statementParser.statement()

    val function = tree.subtreeMatch<FunctionDeclarationContext>(
        parser, "fun test() {\n <statements> }"
    )[0].tree()
    function.addChild(statementTree)

    println(tree.toStringTree(parser))
}
