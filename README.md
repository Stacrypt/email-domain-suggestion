# email-domain-suggestion

A Kotlin library to suggest misspelled email domain name.

[![](https://jitpack.io/v/stacrypt/email-domain-suggestion.svg)](https://jitpack.io/#stacrypt/email-domain-suggestion)

### Dependency

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.stacrypt:email-domain-suggestion:0.0.1'
	}

### How to use

	val suggestionService = EmailDomainSuggestion()
   	suggestionService.suggest("myName@gamil.com") // return myName@gmail.com
   	suggestionService.suggest("myName@yaboo.com") // return myName@yahoo.com
   	suggestionService.suggest("myName@gmsil.com") // return myName@gmail.com
