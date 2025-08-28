package org.jetbrains.kotlin.spec.grammar.tools

import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ListTokenSource
import org.antlr.v4.runtime.tree.RuleNode
import org.jetbrains.kotlin.spec.grammar.KotlinLexer
import org.jetbrains.kotlin.spec.grammar.KotlinParser
import org.jetbrains.kotlin.spec.grammar.KotlinParser.KotlinFileContext
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
    val tokens = tokenizeKotlinCode("fun x() = 10")
    val (parseTree: KotlinParseTree, fileContext: KotlinFileContext) = parseKotlinCode(tokens)
    // or just `val parseTree = parseKotlinCode("val x = foo() + 10;")`

    println(parseTree)

    val xNode = XVisitor.visit(fileContext)
    println(xNode.simpleIdentifier().Identifier().symbol)
}