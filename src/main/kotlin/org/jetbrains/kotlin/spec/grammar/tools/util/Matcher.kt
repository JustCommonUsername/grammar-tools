package org.jetbrains.kotlin.spec.grammar.tools.util

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch
import org.jetbrains.kotlin.spec.grammar.KotlinParser


inline fun <reified T : ParserRuleContext> KotlinParser.getRuleByPattern(
    tree: ParseTree, pattern: String, rule: String, index: Int
): T = getRulesByPattern<T>(tree, pattern, rule)[index]

inline fun <reified T : ParserRuleContext> KotlinParser.getRulesByPattern(
    tree: ParseTree, pattern: String, rule: String
): List<T> {
    val compiledPattern = compileParseTreePattern(pattern, getRuleIndex(rule))
    // Using XPath finder mechanism here, see
    // https://github.com/antlr/antlr4/blob/master/doc/tree-matching.md#using-xpath-to-identify-parse-tree-node-sets
    val expressions: List<ParseTreeMatch> = compiledPattern.findAll(tree, "//$rule")

    if (expressions.isEmpty())
        throw RuleNoteFoundException("Rule $rule not found in tree\n ${tree.toStringTree(this)}")

    return expressions.map { it.tree as T }
}

class RuleNoteFoundException(msg: String) : Exception(msg)
