package org.eidos.reader.remote.requests

import org.eidos.reader.remote.choices.WorkFilterChoices
import org.eidos.reader.ui.misc.values.LANGUAGES
import org.eidos.reader.ui.misc.values.SORT_OPTIONS
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

class WorkFilterRequest(
    val tagName: String,
    var workFilterChoices: WorkFilterChoices = WorkFilterChoices()
) {
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

    private val BASE_QUERY_STRING = "/tags/${encodeMainTag(tagName)}/works?utf8=✓&commit=Sort+and+Filter"
    var pageNumber : Int = 1

    // FIXME: encoding doesn't really work well when fetching synonyms - should convert to actual synonym tag
    var queryStringWithoutPageArgs = StringBuilder(BASE_QUERY_STRING)
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
        .toString()

    val queryString: String
        get() = queryStringWithoutPageArgs + pageNumberQueryString

    fun updateChoices(workFilterChoices: WorkFilterChoices) {
        this.workFilterChoices = workFilterChoices
        queryStringWithoutPageArgs = StringBuilder(BASE_QUERY_STRING)
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
            .toString()
        pageNumber = 1
    }

    /**
     * Helper methods for converting variables to partial query strings
     * These are written as vals to make it more kotlinic
     */

    private val includedTagsQueryString : String
        get() {
            val tagConcat = workFilterChoices.includedTags.joinToString(
                    separator = "%2C",
                    transform = {
                        encodeAdditionalTag(it)
                    }
            )

            return "&work_search[other_tag_names]=$tagConcat"
        }

    private val excludedTagsQueryString : String
        get() {
            val tagConcat = workFilterChoices.excludedTags.joinToString(
                    separator = "%2C",
                    transform = {
                        encodeAdditionalTag(it)
                    }
            )

            return "&work_search[excluded_tag_names]=$tagConcat"
        }

    private val showSingleChapterWorksOnlyQueryString : String
        get() {
            if (workFilterChoices.showSingleChapterWorksOnly) {
                return "&work_search[single_chapter]=1"
            } else {
                return ""
            }
        }

    private val ratingsQueryString : String
        get() {
            var tempRatingsQueryString = ""

            if (!workFilterChoices.showRatingNotRated) {
                tempRatingsQueryString += "&exclude_work_search[rating_ids][]=9"
            }

            if (!workFilterChoices.showRatingGeneral) {
                tempRatingsQueryString += "&exclude_work_search[rating_ids][]=10"
            }

            if (!workFilterChoices.showRatingTeen) {
                tempRatingsQueryString += "&exclude_work_search[rating_ids][]=11"
            }

            if (!workFilterChoices.showRatingMature) {
                tempRatingsQueryString += "&exclude_work_search[rating_ids][]=12"
            }

            if (!workFilterChoices.showRatingExplicit) {
                tempRatingsQueryString += "&exclude_work_search[rating_ids][]=13"
            }

            return tempRatingsQueryString
        }

    private val warningsQueryString : String
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

            if (workFilterChoices.mustContainAllWarnings) {
                if (workFilterChoices.showWarningNone) {
                    tempWarningsQueryString += "&include_work_search[archive_warning_ids][]=16"
                }

                if (workFilterChoices.showWarningViolence) {
                    tempWarningsQueryString += "&include_work_search[archive_warning_ids][]=17"
                }

                if (workFilterChoices.showWarningCharacterDeath) {
                    tempWarningsQueryString += "&include_work_search[archive_warning_ids][]=18"
                }

                if (workFilterChoices.showWarningRape) {
                    tempWarningsQueryString += "&include_work_search[archive_warning_ids][]=19"
                }

                if (workFilterChoices.showWarningUnderage) {
                    tempWarningsQueryString += "&include_work_search[archive_warning_ids][]=20"
                }

                if (workFilterChoices.showWarningChoseNoWarnings) {
                    tempWarningsQueryString += "&include_work_search[archive_warning_ids][]=14"
                }

            } else {
                if (!workFilterChoices.showWarningNone) {
                    tempWarningsQueryString += "&exclude_work_search[archive_warning_ids][]=16"
                }

                if (!workFilterChoices.showWarningViolence) {
                    tempWarningsQueryString += "&exclude_work_search[archive_warning_ids][]=17"
                }

                if (!workFilterChoices.showWarningCharacterDeath) {
                    tempWarningsQueryString += "&exclude_work_search[archive_warning_ids][]=18"
                }

                if (!workFilterChoices.showWarningRape) {
                    tempWarningsQueryString += "&exclude_work_search[archive_warning_ids][]=19"
                }

                if (!workFilterChoices.showWarningUnderage) {
                    tempWarningsQueryString += "&exclude_work_search[archive_warning_ids][]=20"
                }

                if (!workFilterChoices.showWarningChoseNoWarnings) {
                    tempWarningsQueryString += "&exclude_work_search[archive_warning_ids][]=14"
                }
            }

            return tempWarningsQueryString
        }

    private val crossoversQueryString : String
        get() {
            if (workFilterChoices.showCrossovers xor workFilterChoices.showNonCrossovers) {
                return "&work_search[crossover]=${if (workFilterChoices.showCrossovers) "T" else "F"}"
            } else {
                return ""
            }
        }

    private val completionStatusQueryString : String
        get() {
            if (workFilterChoices.showCompletedWorks xor workFilterChoices.showIncompleteWorks) {
                return "&work_search[complete]=${if (workFilterChoices.showCompletedWorks) "T" else "F"}"
            } else {
                return ""
            }
        }

    private val hitsRangeQueryString : String
        get() {
            if (workFilterChoices.hitsMin == 0 && workFilterChoices.hitsMax == 0 ||
                    workFilterChoices.hitsMin < 0 ||
                    workFilterChoices.hitsMax <= 0) {
                return ""
            } else if (workFilterChoices.hitsMin == workFilterChoices.hitsMax) {
                return "&work_search[hits]=${workFilterChoices.hitsMin}"
            } else if (workFilterChoices.hitsMin == 0) {
                return "&work_search[hits]=<${workFilterChoices.hitsMax}"
            } else if (workFilterChoices.hitsMax == 0) {
                return "&work_search[hits]=>${workFilterChoices.hitsMin}"
            }

            return "&work_search[hits]=${workFilterChoices.hitsMin}-${workFilterChoices.hitsMax}"

        }

    private val kudosRangeQueryString : String
        get() {
            if (workFilterChoices.kudosMin == 0 && workFilterChoices.kudosMax == 0) {
                return ""
            } else if (workFilterChoices.kudosMin == workFilterChoices.kudosMax) {
                return "&work_search[kudos_count]=${workFilterChoices.kudosMin}"
            } else if (workFilterChoices.kudosMin == 0) {
                return "&work_search[kudos_count]=<${workFilterChoices.kudosMax}"
            } else if (workFilterChoices.kudosMax == 0) {
                return "&work_search[kudos_count]=>${workFilterChoices.kudosMin}"
            }

            return "&work_search[kudos_count]=${workFilterChoices.kudosMin}-${workFilterChoices.kudosMax}"

        }

    private val commentsRangeQueryString : String
        // TODO: should I add happy path to the if conditions explicitly?
        get() {
            if (workFilterChoices.commentsMin == 0 && workFilterChoices.commentsMax == 0) {
                return ""
            } else if (workFilterChoices.commentsMin == workFilterChoices.commentsMax) {
                return "&work_search[comments_count]=${workFilterChoices.commentsMin}"
            } else if (workFilterChoices.commentsMin == 0) {
                return "&work_search[comments_count]=<${workFilterChoices.commentsMax}"
            } else if (workFilterChoices.commentsMax == 0) {
                return "&work_search[comments_count]=>${workFilterChoices.commentsMin}"
            }

            return "&work_search[comments_count]=${workFilterChoices.commentsMin}-${workFilterChoices.commentsMax}"

        }

    private val bookmarksRangeQueryString : String
        get() {
            if (workFilterChoices.bookmarksMin == 0 && workFilterChoices.bookmarksMax == 0) {
                return ""
            } else if (workFilterChoices.bookmarksMin == workFilterChoices.bookmarksMax) {
                return "&work_search[bookmarks_count]=${workFilterChoices.bookmarksMin}"
            } else if (workFilterChoices.bookmarksMin == 0) {
                return "&work_search[bookmarks_count]=<${workFilterChoices.bookmarksMax}"
            } else if (workFilterChoices.bookmarksMax == 0) {
                return "&work_search[bookmarks_count]=>${workFilterChoices.bookmarksMin}"
            }

            return "&work_search[bookmarks_count]=${workFilterChoices.bookmarksMin}-${workFilterChoices.bookmarksMax}"
        }

    private val wordCountRangeQueryString : String
        get() {
            if (workFilterChoices.wordCountMin <= 0 && workFilterChoices.wordCountMax <= 0) {
                return ""
            }
            return "&work_search[words_from]=${workFilterChoices.wordCountMin}" +
                    "&work_search[words_to]=${workFilterChoices.wordCountMax}"
        }

    private val dateUpdatedRangeQueryString : String
        get() {
            if (workFilterChoices.dateUpdatedMin.isEmpty() || workFilterChoices.dateUpdatedMax.isEmpty()) {
                return ""
            } else if (workFilterChoices.dateUpdatedMax.isEmpty()) {
                return "&work_search[date_from]=${workFilterChoices.dateUpdatedMin}"
            } else if (workFilterChoices.dateUpdatedMin.isEmpty()) {
                return "&work_search[date_to]=${workFilterChoices.dateUpdatedMax}"
                // FIXME: find a new class to handle dates
            }

            return "&work_search[date_from]=${workFilterChoices.dateUpdatedMin}&work_search[date_to]=${workFilterChoices.dateUpdatedMax}"
        }

    private val searchTermQueryString : String
        get() {
            if (workFilterChoices.searchTerm.isEmpty()) {
                return ""
            }

            return "&work_search[query]=${workFilterChoices.searchTerm}"
        }

    private val languageQueryString : String
        get() {
            return "&work_search[language_id]=${LANGUAGES[workFilterChoices.language]}"
        }

    private val sortOrderQueryString : String
        get() {
            return if (workFilterChoices.sortOrder.isBlank()) {
                "&work_search[sort_column]=revised_at"
            } else {
                "&work_search[sort_column]=${SORT_OPTIONS[workFilterChoices.sortOrder]}"
            }
        }

    private val pageNumberQueryString : String
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
