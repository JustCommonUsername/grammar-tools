package org.jetbrains.kotlin.spec.grammar.tools

import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ListTokenSource
import org.jetbrains.kotlin.spec.grammar.KotlinLexer
import org.jetbrains.kotlin.spec.grammar.KotlinParser
import org.jetbrains.kotlin.spec.grammar.tools.parsing.Parser

fun main() {
    /* val input = """
        import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

        plugins {
            kotlin("jvm") version "1.7.0"
            `maven-publish`
        }
        
        dependencies {
            implementation("test")
        }
        
        x {
            dependencies {
                add("10")
                add("10")
            }
        }
    """.trimIndent() */
    val input = "val x = add(10)"
    val tokens = tokenizeKotlinCode(input)
    val tokenTypeMap = KotlinLexer(null).tokenTypeMap
    val tokensList = ListTokenSource(tokens.map { Parser.getAntlrTokenByKotlinToken(it, tokenTypeMap) })
    val parser = KotlinParser(CommonTokenStream(tokensList)).apply {
        removeErrorListeners()
        addErrorListener(Parser.errorParserListener)
    }
    val tree = parser.kotlinFile()

    val kotlinTree = Parser.buildTree(
        parser,
        tokenTypeMap,
        tree,
        KotlinParseTree(
            KotlinParseTreeNodeType.RULE,
            parser.ruleNames[parser.ruleIndexMap["kotlinFile"]!!]
        )
    )

    println(kotlinTree)

    val lexerForMatching = KotlinLexer(
        Parser.getCharsStream(input)
    ).apply {
        removeErrorListeners()
        addErrorListener(Parser.errorLexerListener)
    }

    println(lexerForMatching.allTokens)

    val xPattern = parser.compileParseTreePattern("add(<valueArgument>)", KotlinParser.RULE_valueArgument, lexerForMatching)
    println(xPattern.match(tree).succeeded())
}