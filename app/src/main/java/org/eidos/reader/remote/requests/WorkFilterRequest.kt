package org.eidos.reader.remote.requests

import java.net.URLEncoder

/* ATTENTION: NOTES ON WORK FILTER CAN BE FOUND AT THE BOTTOM */

/**
 * This class represents a query string that will have arguments appended as each method is called.
 * All methods should only be called once at maximum.
 * Multiple calls will default to the arguments of the last call if I am not wrong.
 *
 * Most likely will be called by viewmodel - viewmodel takes in the form from view and generates the request
 * before passing it to the controller/remote
 */

// TODO: Refactor the methods to use variables so that any accidental extra calls will not screw up the queryString.

class WorkFilterRequest(val tagName: String) {
    companion object {
        // Utility methods
        private fun encodeMainTag(tag: String) : String {
            return tag.replace("/", "*s*")
                    .replace("&", "*a*")
        }

        private fun encodeAdditionalTag(tag: String) : String {
            return URLEncoder.encode(tag, "UTF-8")
        }
    }

    // FIXME: encoding doesn't really work well when fetching synonyms - should convert to actual synonym tag
    var queryString = "/tags/${encodeMainTag(tagName)}/works?utf8=✓&commit=Sort+and+Filter"

    var includedTags : List<String> = emptyList()
    var excludedTags : List<String> = emptyList()

    var showRatingGeneral = true
    var showRatingTeen = true
    var showRatingMature = true
    var showRatingExplicit = true
    var showRatingNotRated = true

    var showWarningNone = true
    var showWarningViolence = true
    var showWarningCharacterDeath = true
    var showWarningUnderage = true
    var showWarningRape = true
    var showWarningChoseNoWarnings = true
    var mustContainAllWarnings = false

    var showCategoryGen = true
    var showCategoryFM = true
    var showCategoryFF = true
    var showCategoryMM = true
    var showCategoryMulti = true
    var showCategoryOther = true

    var showSingleChapterWorksOnly = false
    var showCrossovers = true
    var showNonCrossovers = true
    var showCompletedWorks = true
    var showIncompleteWorks = true

    var hitsRange : Pair<Int, Int> = Pair(0, 0)
    var kudosRange : Pair<Int, Int> = Pair(0, 0)
    var commentsRange : Pair<Int, Int> = Pair(0, 0)
    var bookmarksRange : Pair<Int, Int> = Pair(0, 0)
    var wordCountRange : Pair<Int, Int> = Pair(0, 0)
    var dateUpdatedRange : Pair<String, String> = Pair("", "")

    var searchTerm : String = ""
    var language : String = ""
    var sortOrder : String = ""
    var pageNumber : Int = 1

    fun updateQueryString() {
        queryString = StringBuilder("/tags/${encodeMainTag(tagName)}/works?utf8=✓&commit=Sort+and+Filter")
                .append(includedTagsQueryString)
                .append(excludedTagsQueryString)
                .append(showSingleChapterWorksOnlyQueryString)
                .append(ratingsQueryString)
                .append(warningsQueryString)
                .append(crossoversQueryString)
                .append(completionStatusQueryString)
                .append(hitsRangeQueryString)
                .append(kudosRangeQueryString)
                .append(commentsRangeQueryString)
                .append(bookmarksRangeQueryString)
                .append(wordCountRangeQueryString)
                .append(dateUpdatedRangeQueryString)
                .append(languageQueryString)
                .append(sortOrderQueryString)
                .append(pageNumberQueryString)
                .toString()
    }

    /**
     * Helper methods for converting variables to partial query strings
     * These are written as vals to make it more kotlinic
     */

    val includedTagsQueryString : String
        get() {
            val tagConcat = includedTags.joinToString(
                    separator = "%2C",
                    transform = {
                        encodeAdditionalTag(it)
                    }
            )

            return "&work_search[other_tag_names]=$tagConcat"
        }

    val excludedTagsQueryString : String
        get() {
            val tagConcat = excludedTags.joinToString(
                    separator = "%2C",
                    transform = {
                        encodeAdditionalTag(it)
                    }
            )

            return "&work_search[excluded_tag_names]=$tagConcat"
        }

    val showSingleChapterWorksOnlyQueryString : String
        get() {
            if (showSingleChapterWorksOnly) {
                return "&work_search[single_chapter]=1"
            } else {
                return ""
            }
        }

    val ratingsQueryString : String
        get() {
            var tempRatingsQueryString = ""

            if (!showRatingNotRated) {
                tempRatingsQueryString += "&exclude_work_search[rating_ids][]=9"
            }

            if (!showRatingGeneral) {
                tempRatingsQueryString += "&exclude_work_search[rating_ids][]=10"
            }

            if (!showRatingTeen) {
                tempRatingsQueryString += "&exclude_work_search[rating_ids][]=11"
            }

            if (!showRatingMature) {
                tempRatingsQueryString += "&exclude_work_search[rating_ids][]=12"
            }

            if (!showRatingExplicit) {
                tempRatingsQueryString += "&exclude_work_search[rating_ids][]=13"
            }

            return tempRatingsQueryString
        }

    val warningsQueryString : String
        get() {
            /**
             * Chose No Warnings    14
             * None                 16
             * Violence             17
             * Character Death      18
             * Rape                 19
             * Underage             20
             */
            var tempWarningsQueryString = ""

            if (mustContainAllWarnings) {
                if (showWarningNone) {
                    tempWarningsQueryString += "&include_work_search[archive_warning_ids][]=16"
                }

                if (showWarningViolence) {
                    tempWarningsQueryString += "&include_work_search[archive_warning_ids][]=17"
                }

                if (showWarningCharacterDeath) {
                    tempWarningsQueryString += "&include_work_search[archive_warning_ids][]=18"
                }

                if (showWarningRape) {
                    tempWarningsQueryString += "&include_work_search[archive_warning_ids][]=19"
                }

                if (showWarningUnderage) {
                    tempWarningsQueryString += "&include_work_search[archive_warning_ids][]=20"
                }

                if (showWarningChoseNoWarnings) {
                    tempWarningsQueryString += "&include_work_search[archive_warning_ids][]=14"
                }

            } else {
                if (!showWarningNone) {
                    tempWarningsQueryString += "&exclude_work_search[archive_warning_ids][]=16"
                }

                if (!showWarningViolence) {
                    tempWarningsQueryString += "&exclude_work_search[archive_warning_ids][]=17"
                }

                if (!showWarningCharacterDeath) {
                    tempWarningsQueryString += "&exclude_work_search[archive_warning_ids][]=18"
                }

                if (!showWarningRape) {
                    tempWarningsQueryString += "&exclude_work_search[archive_warning_ids][]=19"
                }

                if (!showWarningUnderage) {
                    tempWarningsQueryString += "&exclude_work_search[archive_warning_ids][]=20"
                }

                if (!showWarningChoseNoWarnings) {
                    tempWarningsQueryString += "&exclude_work_search[archive_warning_ids][]=14"
                }
            }

            return tempWarningsQueryString
        }

    val crossoversQueryString : String
        get() {
            if (showCrossovers xor showNonCrossovers) {
                return "&work_search[crossover]=${if (showCrossovers) "T" else "F"}"
            } else {
                return ""
            }
        }

    val completionStatusQueryString : String
        get() {
            if (showCompletedWorks xor showIncompleteWorks) {
                return "&work_search[complete]=${if (showCompletedWorks) "T" else "F"}"
            } else {
                return ""
            }
        }

    val hitsRangeQueryString : String
        get() {
            if (hitsRange.first == 0 && hitsRange.second == 0 ||
                    hitsRange.first < 0 ||
                    hitsRange.second <= 0) {
                return ""
            } else if (hitsRange.first == hitsRange.second) {
                return "&work_search[hits]=$hitsRange.first"
            } else if (hitsRange.first == 0) {
                return "&work_search[hits]=<$hitsRange.second"
            } else if (hitsRange.second == 0) {
                return "&work_search[hits]=>$hitsRange.first"
            } else {
                return "&work_search[hits]=$hitsRange.first-$hitsRange.second"
            }
        }

    val kudosRangeQueryString : String
        get() {
            if (kudosRange.first == 0 && kudosRange.second == 0) {
                return ""
            } else if (kudosRange.first == kudosRange.second) {
                return "&work_search[kudos_count]=$kudosRange.first"
            } else if (kudosRange.first == 0) {
                return "&work_search[kudos_count]=<$kudosRange.second"
            } else if (kudosRange.second == 0) {
                return "&work_search[kudos_count]=>$kudosRange.first"
            } else {
                return "&work_search[kudos_count]=$kudosRange.first-$kudosRange.second"
            }
        }

    val commentsRangeQueryString : String
        get() {
            if (commentsRange.first == 0 && commentsRange.second == 0) {
                return ""
            } else if (commentsRange.first == commentsRange.second) {
                return "&work_search[comments_count]=$commentsRange.first"
            } else if (commentsRange.first == 0) {
                return "&work_search[comments_count]=<$commentsRange.second"
            } else if (commentsRange.second == 0) {
                return "&work_search[comments_count]=>$commentsRange.first"
            } else {
                return "&work_search[comments_count]=$commentsRange.first-$commentsRange.second"
            }
        }

    val bookmarksRangeQueryString : String
        get() {
            if (bookmarksRange.first == 0 && bookmarksRange.second == 0) {
                return ""
            } else if (bookmarksRange.first == bookmarksRange.second) {
                return "&work_search[bookmarks_count]=$bookmarksRange.first"
            } else if (bookmarksRange.first == 0) {
                return "&work_search[bookmarks_count]=<$bookmarksRange.second"
            } else if (bookmarksRange.second == 0) {
                return "&work_search[bookmarks_count]=>$bookmarksRange.first"
            } else {
                return "&work_search[bookmarks_count]=$bookmarksRange.first-$bookmarksRange.second"
            }
        }

    val wordCountRangeQueryString : String
        get() {
            if (wordCountRange.first <= 0 && wordCountRange.second <= 0) {
                return ""
            } else {
                return "&work_search[words_from]=$wordCountRange.first" +
                        "&work_search[words_to]=$wordCountRange.second"
            }
        }

    val dateUpdatedRangeQueryString : String
        get() {
            if (dateUpdatedRange.first.isEmpty() || dateUpdatedRange.second.isEmpty()) {
                return ""
            } else if (dateUpdatedRange.second.isEmpty()) {
                return "&work_search[date_from]=$dateUpdatedRange.first"
            } else if (dateUpdatedRange.first.isEmpty()) {
                return "&work_search[date_to]=$dateUpdatedRange.second"
                // FIXME: find a new class to handle dates
            } else {
                return "&work_search[date_from]=$dateUpdatedRange.first&work_search[date_to]=$dateUpdatedRange.second"
            }
        }

    val languageQueryString : String
        get() {
            return "&work_search[language_id]="
        }

    val sortOrderQueryString : String
        get() {
            return "&work_search[sort_column]=revised_at"
        }

    val pageNumberQueryString : String
        get() {
            return "&page=$pageNumber"
        }

    /**
     * Enumerations
     */

    enum class SortOrder(val code: String) {
        DATE_UPDATED("revised_at")
    }

    enum class Language(val code: String, val displayText: String) {
        ENGLISH("en", "English")
//        AFRIKAANS("afr", "Afrikaans"),
//        ARABIC("ar", "العربية"),
//        ("arc", "ܐܪܡܝܐ | ארמיא"),
//        ("hy", "հայերեն"),
//        BELARUSSIAN("be", "беларуская"),
//        ("bg", "Български"),
//        ("bn", "বাংলা"),
//        ("bos", "Bosanski"),
//        ("br", "brezhoneg"),
//        CATALAN("ca", "Català"),
//        CHINUK_WAWA("chn", "Chinuk Wawa"),
//        ("cs", "Čeština"),
//        ("cy", "Cymraeg"),
//        DANISH("da", "Dansk"),
//        GERMAN("de", "Deutsch"),
//        ("el", "Ελληνικά"),
//        ENGLISH("en", "English"),
//        ESPERANTO("eo", "Esperanto"),
//        SPANISH("es", "Español"),
//        ESTONIAN("et", "eesti keel"),
//        ("eu", "Euskara"),
//        FARSI("fa", "فارسی"),
//        FINNISH("fi", "Suomi"),
//        TAGALOG("fil", "Filipino"),
//        FRENCH("fr", "Français"),
//        FURLAN("fur", "Furlan"),
//        IRISH("ga", "Gaeilge"),
//        GAELIC_SCOTTISH("gd", "Gàidhlig"),
//        ("gem", "Sprēkō Þiudiskō"),
//        ("gl", "Galego"),
//        ("got", "𐌲𐌿𐍄𐌹𐍃𐌺𐌰"),
//        CHINESE_HAKKA("hak", "中文-客家话"),
//        ("hau", "Hausa | هَرْشَن هَوْسَ"),
//        HEBREW("he", "עברית"),
//        HINDI("hi", "हिन्दी"),
//        CROATIAN("hr", "Hrvatski"),
//        HUNGARIAN("hu", "Magyar"),
//        INTERLINGUA("ia", "Interlingua"),
//        BAHASA_INDONESIA("id", "Bahasa Indonesia"),
//        ICELANDIC("is", "Íslenska"),
//        ZULU("zu", "isiZulu"),
//        ITALIAN("it", "Italiano"),
//        JAPANESE("ja", "日本語"),
//        BASA_JAWA("jv", "Basa Jawa"),
//        ("kat", "ქართული"),
//        KOREAN("ko", "한국어"),
//        LATIN("la", "Lingua latina"),
//        QUEBECOIS("fcs", "Langue des signes québécoise"),
//        ("lb", "Lëtzebuergesch"),
//        ("lt", "Lietuvių kalba"),
//        ("lv", "Latviešu valoda"),
//        ("ml", "മലയാളം"),
//        ("mnc", "ᠮᠠᠨᠵᡠ ᡤᡳᠰᡠᠨ"),
//        ("mk", "македонски"),
//        ("mon", "ᠮᠣᠩᠭᠣᠯ ᠪᠢᠴᠢᠭ᠌ | Монгол Кирилл үсэг"),
//        ("mr", "मराठी"),
//        BAHASA_MALAYSIA("ms", "Bahasa Malaysia"),
//        ("nan", "中文-闽南话 臺語"),
//        ("nds", "Plattdüütsch"),
//        DUTCH("nl", "Nederlands"),
//        NORWEGIAN("no", "Norsk"),
//        ("pa", "ਪੰਜਾਬੀ"),
//        POLISH("pl", "Polski"),
//        PORTUGUESE_BRAZILIAN("ptBR", "Português brasileiro"),
//        PORTUGUESE("ptPT", "Português europeu"),
//        ("qkz", "Khuzdul"),
//        ("qya", "Quenya"),
//        ROMANIAN("ro", "Română"),
//        RUSSIAN("ru", "Русский"),
//        SCOTTISH("sco", "Scots"),
//        ("si", "සිංහල"),
//        SINDARIN("sjn", "Sindarin"),
//        ("sk", "Slovenčina"),
//        SLOVENIAN("slv", "Slovenščina"),
//        SOMALIAN("so", "af Soomaali"),
//        ("sq", "Shqip"),
//        ("sr", "српски"),
//        ("sv", "Svenska"),
//        ("sw", "Kiswahili"),
//        ("ta", "தமிழ்"),
//        ("th", "ไทย"),
//        ("bod", "བོད་སྐད་"),
//        KLINGON("tlh", "tlhIngan-Hol"),
//        TOKI_PONA("qtp", "Toki Pona"),
//        THERMIAN("tqx", "Thermian"),
//        TURKISH("tr", "Türkçe"),
//        UKRAINIAN("uk", "Українська"),
//        UIGHUR("uig", "ئۇيغۇر تىلى"),
//        VIETNAMESE("vi", "Tiếng Việt"),
//        CHINESE_WU("wuu", "中文-吴语"),
//        CHINESE_CANTONESE("yue", "中文-广东话 粵語"),
//        CHINESE_STANDARD("zh", "中文-普通话 國語")
        
    }
}

/*
    parameters that only accept one variable at a time (no repeats allowed). use a custom setter to ensure singularity
        include work search
            ratings (ids)
        work search (mostly strings except minwords)
            crossovers
            completion
            min words
            max words
            min date
            max date
            language
            search within results

    parameters that accept multiple variables, but have to be repeated
        basically all of these are id-based

        include work search
            archive warnings (ids)
            categories (ids)
            fandoms (ids)
            characters (ids)
            relationships (ids)
            additional tags (aka freeform) (ids)
        exclude work search
            ratings (ids)
            archive warnings (ids)
            categories (ids)
            fandoms (ids)
            characters (ids)
            relationships (ids)
            additional tags (aka freeform) (ids)

    parameters that can accept multiple variables without having to repeat the argument - custom setter to concat strs
        these are separated by some delimiter in the arguments

        work search
            other tags to include (technically part of include section but under work search in http arguments)
            other tags to exclude (same as above)

     */
