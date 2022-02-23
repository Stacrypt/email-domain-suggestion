package io.stacrypt.emaildomainsuggestion

fun main() {
    val suggestionService = EmailDomainSuggestion()
    println(suggestionService.suggest("myName@gamil.com"))
    println(suggestionService.suggest("myName@yaboo.com"))
    println(suggestionService.suggest("myName@gmsil.com"))
}