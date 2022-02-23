package io.stacrypt.emaildomainsuggestion

/**
 * Check an email address and represent suggestion for email domain name in case of misspelled
 * source https://github.com/raghulj/MailCheckEditText/blob/master/src/com/raghulj/android/widgets/MailCheckEditText.java
 */
class EmailDomainSuggestion {
    /**
     * Suggests an email address based on the entered email address using a
     * fuzzy logic
     */
    fun suggest(email: String): String? {
        return suggestEmailId(email, defaultDomains, defaultTopLevelDomains)
    }

    private fun suggestEmailId(
        email: String,
        domains: Array<String>,
        topLevelDomains: Array<String>
    ): String? {
        val loweredEmail = email.toLowerCase()
        var emailAddress: String? = loweredEmail
        val emailParts = splitEmail(loweredEmail) ?: return null
        try {
            var closestDomain = findClosestDomain(emailParts[1], domains)
            if (closestDomain != null) {
                if (closestDomain !== emailParts[1]) {
                    // The email address closely matches one of the supplied
                    // domains; return a suggestion
                    emailAddress = emailParts[2].toString() + "@" + closestDomain
                    return emailAddress
                }
            } else {
                // The email address does not closely match one of the supplied
                // domains
                val closestTopLevelDomain = findClosestDomain(
                    emailParts[0],
                    topLevelDomains
                )
                if (emailParts[1] != null && closestTopLevelDomain != null && closestTopLevelDomain !== emailParts[0]) {
                    // The email address may have a misspelled top-level domain;
                    // return a suggestion
                    var domain = emailParts[1]
                    val domainName: Array<String?> = domain!!.split("\\.").toTypedArray()

                    // find the closest of the domain names
                    domainName[0] = findClosestDomain(
                        domainName[0],
                        defaultDomainNames
                    )
                    domain = stringJoin(domainName, ".")

                    // concatenate the domain name with the top level domain name
                    closestDomain = (domain.substring(
                        0,
                        domain.lastIndexOf(emailParts[0]!!)
                    ) + closestTopLevelDomain)
                    emailAddress = emailParts[2].toString() + "@" + closestDomain
                }
            }
        } catch (e: Exception) {
            emailAddress = null
        }
        /*
         * The email address exactly matches one of the supplied domains, does
         * not closely match any domain and does not appear to simply have a
         * misspelled top-level domain, or is an invalid email address; do not
         * return a suggestion.
         */return emailAddress
    }

    /**
     * Method compares two string and find the accuracy percentage from that.
     * source: https://github.com/gtri/string-metric/blob/master/src/main/java/edu/gatech/gtri/stringmetric/DamerauLevenshteinDistance.java
     * https://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance
     */
    private fun damerauLevenshteinDistance(a: String, b: String): Float {
        val m: Int = a.length
        val n: Int = b.length

        if (m == 0) {
            return n.toFloat()
        }
        if (n == 0) {
            return m.toFloat()
        }

        val inf: Int = maxOf(m, n)
        val h = Array(m + 2) {
            IntArray(
                n + 2
            )
        }
        h[0][0] = inf
        for (i in 0..m) {
            h[i + 1][1] = i
            h[i + 1][0] = inf
        }
        for (j in 0..n) {
            h[1][j + 1] = j
            h[0][j + 1] = inf
        }

        val da: MutableMap<Char, Int> = HashMap(128)
        for (i in 0 until m) {
            da[a[i]] = 0
        }
        for (j in 0 until n) {
            da[b[j]] = 0
        }

        for (i in 1..m) {
            var db = 0
            for (j in 1..n) {
                val i1 = da[b[j - 1]]!!
                val j1 = db
                val d = if (a[i - 1] == b[j - 1]) 0 else 1
                if (d == 0) db = j
                h[i + 1][j + 1] = minOf(
                    h[i][j] + d,
                    h[i + 1][j] + 1,
                    h[i][j + 1] + 1,
                    h[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1)
                )
            }
            da[a[i - 1]] = i
        }

        return h[m + 1][n + 1].toFloat()
    }

    /** Split the email id to username domain name and top level domain names  */
    private fun splitEmail(email: String): Array<String?>? {
        val parts = email.split("@").toTypedArray()
        if (parts.size < 2) {
            return null
        }
        for (i in parts.indices) {
            if (parts[i] === "") {
                return null
            }
        }
        val domain = parts[1]
        val domainParts = domain.split("\\.").toTypedArray()
        var tld = ""
        if (domainParts.isEmpty()) {
            // The address does not have a top-level domain
            return null
        } else if (domainParts.size == 1) {
            // The address has only a top-level domain (valid under RFC)
            tld = domainParts[0]
        } else {
            // The address has a domain and a top-level domain
            for (i in 1 until domainParts.size) {
                tld += domainParts[i] + '.'
            }
            if (domainParts.size >= 2) {
                tld = tld.substring(0, tld.length - 1)
            }
        }
        return arrayOf(tld, domain, parts[0])
    }

    /**
     * Find the closest domain match for the given domain name in the email with
     * the set of domain names
     */
    private fun findClosestDomain(domain: String?, domains: Array<String>?): String? {
        var dist: Float
        var minDist = 99
        val threshold = 3
        var closestDomain: String? = null
        if (domain == null || domains == null) {
            return null
        }
        for (i in domains.indices) {
            if (domain === domains[i]) {
                return domain
            }
            dist = damerauLevenshteinDistance(domain, domains[i])
            if (dist < minDist) {
                minDist = dist.toInt()
                closestDomain = domains[i]
            }
        }
        return if (minDist <= threshold && closestDomain != null) {
            closestDomain
        } else {
            null
        }
    }

    // Util methods
    private fun isNull(str: String?): Boolean {
        return str == null
    }

    private fun isNullOrBlank(param: String): Boolean {
        return isNull(param) || param.trim { it <= ' ' }.isEmpty()
    }

    private fun stringJoin(inputArray: Array<String?>, glueString: String): String {
        /** Output variable  */
        var output = ""
        if (inputArray.isNotEmpty()) {
            val sb = StringBuilder()
            sb.append(inputArray[0])
            for (i in 1 until inputArray.size) {
                sb.append(glueString)
                sb.append(inputArray[i])
            }
            output = sb.toString()
        }
        return output
    }

    // Default domain names to be checked
    private val defaultDomains = arrayOf(
        /* Default domains included */
        "aol.com", "att.net", "comcast.net", "facebook.com", "gmail.com", "gmx.com", "googlemail.com",
        "google.com", "hotmail.com", "hotmail.co.uk", "mac.com", "me.com", "mail.com", "msn.com",
        "live.com", "sbcglobal.net", "verizon.net", "yahoo.com", "yahoo.co.uk",

        /* Other global domains */
        "email.com", "fastmail.fm", "games.com" /* AOL */, "gmx.net", "hush.com", "hushmail.com", "icloud.com",
        "iname.com", "inbox.com", "lavabit.com", "love.com" /* AOL */, "outlook.com", "pobox.com", "protonmail.ch", "protonmail.com", "tutanota.de", "tutanota.com", "tutamail.com", "tuta.io",
        "keemail.me", "rocketmail.com" /* Yahoo */, "safe-mail.net", "wow.com" /* AOL */, "ygm.com" /* AOL */,
        "ymail.com" /* Yahoo */, "zoho.com", "yandex.com",

        /* United States ISP domains */
        "bellsouth.net", "charter.net", "cox.net", "earthlink.net", "juno.com",

        /* British ISP domains */
        "btinternet.com", "virginmedia.com", "blueyonder.co.uk", "live.co.uk",
        "ntlworld.com", "orange.net", "sky.com", "talktalk.co.uk", "tiscali.co.uk",
        "virgin.net", "bt.com",

        /* Domains used in Asia */
        "sina.com", "sina.cn", "qq.com", "naver.com", "hanmail.net", "daum.net", "nate.com", "yahoo.co.jp", "yahoo.co.kr", "yahoo.co.id", "yahoo.co.in", "yahoo.com.sg", "yahoo.com.ph", "163.com", "yeah.net", "126.com", "21cn.com", "aliyun.com", "foxmail.com",

        /* French ISP domains */
        "hotmail.fr", "live.fr", "laposte.net", "yahoo.fr", "wanadoo.fr", "orange.fr", "gmx.fr", "sfr.fr", "neuf.fr", "free.fr",

        /* German ISP domains */
        "gmx.de", "hotmail.de", "live.de", "online.de", "t-online.de" /* T-Mobile */, "web.de", "yahoo.de",

        /* Italian ISP domains */
        "libero.it", "virgilio.it", "hotmail.it", "aol.it", "tiscali.it", "alice.it", "live.it", "yahoo.it", "email.it", "tin.it", "poste.it", "teletu.it",

        /* Russian ISP domains */
        "bk.ru", "inbox.ru", "list.ru", "mail.ru", "rambler.ru", "yandex.by", "yandex.com", "yandex.kz", "yandex.ru", "yandex.ua", "ya.ru",

        /* Belgian ISP domains */
        "hotmail.be", "live.be", "skynet.be", "voo.be", "tvcablenet.be", "telenet.be",

        /* Argentinian ISP domains */
        "hotmail.com.ar", "live.com.ar", "yahoo.com.ar", "fibertel.com.ar", "speedy.com.ar", "arnet.com.ar",

        /* Domains used in Mexico */
        "yahoo.com.mx", "live.com.mx", "hotmail.es", "hotmail.com.mx", "prodigy.net.mx",

        /* Domains used in Canada */
        "yahoo.ca", "hotmail.ca", "bell.net", "shaw.ca", "sympatico.ca", "rogers.com",

        /* Domains used in Brazil */
        "yahoo.com.br", "hotmail.com.br", "outlook.com.br", "uol.com.br", "bol.com.br", "terra.com.br", "ig.com.br", "r7.com", "zipmail.com.br", "globo.com", "globomail.com", "oi.com.br"
    )
    private val defaultTopLevelDomains = arrayOf(
        "co.uk", "com", "net",
        "org", "info", "edu", "gov", "mil"
    )
    private val defaultDomainNames = arrayOf(
        "yahoo", "google", "hotmail",
        "gmail", "me", "aol", "mac", "live", "comcast", "googlemail",
        "msn", "hotmail", "yahoo", "facebook", "verizon", "sbcglobal",
        "att", "gmx", "mail"
    )
}
