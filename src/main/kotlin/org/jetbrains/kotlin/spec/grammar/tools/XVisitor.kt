package org.jetbrains.kotlin.spec.grammar.tools

import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ListTokenSource
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern
import org.jetbrains.kotlin.spec.grammar.KotlinLexer
import org.jetbrains.kotlin.spec.grammar.KotlinParser
import org.jetbrains.kotlin.spec.grammar.KotlinParser.InfixFunctionCallContext
import org.jetbrains.kotlin.spec.grammar.KotlinParser.InfixOperationContext
import org.jetbrains.kotlin.spec.grammar.KotlinParser.KotlinFileContext
import org.jetbrains.kotlin.spec.grammar.KotlinParser.SimpleIdentifierContext
import org.jetbrains.kotlin.spec.grammar.KotlinParserBaseVisitor
import org.jetbrains.kotlin.spec.grammar.tools.parsing.Parser

object XVisitor : KotlinParserBaseVisitor<KotlinParser.FunctionDeclarationContext>() {

    override fun visitFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext): KotlinParser.FunctionDeclarationContext {
        if (ctx.simpleIdentifier().Identifier().symbol.text == "x") return ctx
        // visit children
        return super.visitFunctionDeclaration(ctx)
    }

    override fun shouldVisitNextChild(
        node: RuleNode?,
        currentResult: KotlinParser.FunctionDeclarationContext?
    ): Boolean {
        return currentResult == null
    }
}

fun main() {
    val tokens = tokenizeKotlinCode("""
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
    """.trimIndent())
    val (parseTree: KotlinParseTree, fileContext: KotlinFileContext) = parseKotlinCode(tokens)
    // or just `val parseTree = parseKotlinCode("val x = foo() + 10;")`

    println(parseTree)

    val xNode = XVisitor.visit(fileContext)
    println(xNode.simpleIdentifier())
}