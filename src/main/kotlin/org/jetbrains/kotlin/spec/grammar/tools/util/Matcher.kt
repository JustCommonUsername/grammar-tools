package org.jetbrains.kotlin.spec.grammar.tools.util

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch
import org.jetbrains.kotlin.spec.grammar.KotlinParser

data class TypesafeTreeMatch<T : ParserRuleContext>(
    val match: ParseTreeMatch,
) {

    fun tree(): T = match.tree as T

    fun subtreeByLabel(label: String): ParseTree = match.get(label)

    inline fun <reified O : ParserRuleContext> mapSubtrees(
        parser: KotlinParser,
        pattern: String
    ): List<TypesafeTreeMatch<O>> = tree().subtreeMatch<O>(parser, pattern)
}
