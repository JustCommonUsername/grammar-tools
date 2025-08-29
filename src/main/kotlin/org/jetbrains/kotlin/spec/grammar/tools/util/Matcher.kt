package org.jetbrains.kotlin.spec.grammar.tools.util

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch
import org.jetbrains.kotlin.spec.grammar.KotlinParser


inline fun <reified T : ParserRuleContext> KotlinParser.getRuleByPattern(
    tree: ParseTree, pattern: String, index: Int
): T = getRulesByPattern<T>(tree, pattern)[index]

inline fun <reified T : ParserRuleContext> KotlinParser.getRulesByPattern(
    tree: ParseTree, pattern: String
): List<T> {
    val rule = T::class.simpleName?.removeSuffix("Context")?.replaceFirstChar { it.lowercase() } ?:
        throw IllegalArgumentException("No simple name for the Kotlin Parser rule type!")
    val compiledPattern = compileParseTreePattern(pattern, getRuleIndex(rule))
    // Using XPath finder mechanism here, see
    // https://github.com/antlr/antlr4/blob/master/doc/tree-matching.md#using-xpath-to-identify-parse-tree-node-sets
    val expressions: List<ParseTreeMatch> = compiledPattern.findAll(tree, "//$rule")

    if (expressions.isEmpty())
        throw RuleNoteFoundException("Rule $rule not found in tree\n ${tree.toStringTree(this)}")

    // Parse tree _should_ be convertible because we find the subtrees by the rule
    return expressions.map { it.tree as T }
}

class RuleNoteFoundException(msg: String) : Exception(msg)
