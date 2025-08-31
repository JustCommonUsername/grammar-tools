package org.jetbrains.kotlin.spec.grammar.tools.util

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch
import org.jetbrains.kotlin.spec.grammar.KotlinLexer
import org.jetbrains.kotlin.spec.grammar.KotlinParser

fun subtreeParser(input: String): KotlinParser {
    val lexer = KotlinLexer(CharStreams.fromString(input))
    val parser = KotlinParser(CommonTokenStream(lexer))
    return parser
}

inline fun <reified T : ParserRuleContext> ParseTree.subtreeMatch(
    parser: KotlinParser, pattern: String
): List<TypesafeTreeMatch<T>> {
    val rule = ruleFromType<T>()
    val compiledPattern = parser.compileParseTreePattern(pattern, parser.getRuleIndex(rule))
    println(compiledPattern.patternTree.toStringTree(parser))
    // Using XPath finder mechanism here, see
    // https://github.com/antlr/antlr4/blob/master/doc/tree-matching.md#using-xpath-to-identify-parse-tree-node-sets
    val expressions: List<ParseTreeMatch> = compiledPattern.findAll(this, "//$rule")
    return expressions.map(::TypesafeTreeMatch)
}

inline fun <reified T: ParserRuleContext> KotlinParser.getRuleIndex() =
    getRuleIndex(ruleFromType<T>())

inline fun <reified T: ParserRuleContext> ruleFromType(): String {
    val rule = T::class.simpleName!!
    assert(rule.endsWith("Context")) { "Rule type should end with 'Context'." }
    return rule.removeSuffix("Context").replaceFirstChar { it.lowercase() }
}