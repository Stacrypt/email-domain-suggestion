package io.stacrypt.emaildomainsuggestion

import org.junit.Assert
import org.junit.jupiter.api.Test

class MailDomainSuggestionTest {
    private val misspelledDomains = arrayOf(
        "gmail.comy",
        "gmaul.com",
        "gamli.com",
        "gmsil.com",
        "00gmail.com",
        "yahoo.cmo",
        "gmail.com",
        "gmaiil.com",
        "gxmail.com",
        "gmail.comn",
        "gmail.cam",
        "gmaio.com",
        "gmeil.com",
        "gmial.com",
        "hatmail.com",
        "yahho.com",
        "gmail.c",
        "gmail.cin",
        "gmail.comh",
        "gmail.co.com",
        "ahoo.com",
        "gmal.com",
        "gmail.comp",
        "gmail.come",
        "ghail.com",
        "gmail..com",
        "gmall.c0m",
        "gmail.coom",
        "gmaol.com",
        "uahoo.com",
        "gmila.com",
        "gmael.com",
        "gamail.com",
        "glail.com",
        "gmall.com",
        "gami.com",
        "gmail.com2",
        "gmaip.com",
        "gmeal.com",
        "gmmail.com",
        "gmail.coms",
        "gmqail.com",
        "qmail.com",
        "gmail.co",
        "amil.com",
        "gmeli.com",
        "ggmail.com",
        "gmaill.com",
        "email.com",
        "gmil.cim",
        "gmile.com",
        "gnail.com",
        "0gmail.com",
        "gmail.cmo",
        "giml.com",
        "gamill.com",
        "gmail.con",
        "jmail.com",
        "gmail.comoi",
        "gmail.com1",
        "gmail.com0"
    )

    @Test
    fun `Check list of misspelled domains, domain suggester should suggest correct domains`() {
        val userName = "somebody@"
        val correctDomainNames = listOf("gmail.com", "yahoo.com", "hotmail.com", "mail.com", "email.com")
        val mailChecker = EmailDomainSuggestion()
        misspelledDomains.forEach { misspelledDomain ->
            val suggestion = mailChecker.suggest("$userName$misspelledDomain")
            Assert.assertTrue(correctDomainNames.map { "$userName$it" }.contains(suggestion))
        }
    }
}
