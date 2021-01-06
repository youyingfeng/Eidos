package org.eidos.reader.remote.requests

import java.net.URLEncoder
import java.time.LocalDate

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

class WorkFilterRequest(tagName: String) {
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
        get() {
            return StringBuilder(field)
                    .append(includedTagsQueryString)
                    .append(excludedTagsQueryString)
                    .append(showSingleChapterWorksQueryString)
                    .append(ratingsQueryString)
                    .append(showCrossoversQueryString)
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

    // TODO: convert to use variables instead of simply appending
    // Must be done

    /**
     * The variables below are all formatted to query strings.
     */
    var includedTagsQueryString = ""
    var excludedTagsQueryString = ""
    var showSingleChapterWorksQueryString = ""
    var ratingsQueryString = ""
    var showCrossoversQueryString = ""
    var completionStatusQueryString = ""    // FIXME: either rename this or rename the rest
    var hitsRangeQueryString = ""
    var kudosRangeQueryString = ""
    var commentsRangeQueryString = ""
    var bookmarksRangeQueryString = ""
    var wordCountRangeQueryString = ""
    var dateUpdatedRangeQueryString = ""
    var languageQueryString = ""
    var sortOrderQueryString = ""
    var pageNumberQueryString = ""


    /**
     * Includes the specified tags in the work filter request.
     * This method is meant to replace the id-based approach adopted in the default AO3 work filter.
     * This method is used for the following fields:
     *      Warnings, Categories, Fandoms, Characters, Relationships, Additional Tags (Freeform), other tags
     */
    fun includeTags(vararg tags: String) : WorkFilterRequest {
        val tagConcat = tags.joinToString(
            separator = "%2C",
            transform = {
                encodeAdditionalTag(it)
            }
        )

        includedTagsQueryString = "&work_search[other_tag_names]=$tagConcat"
        return this
    }

    /**
     * Includes the specified tags in the work filter request.
     * This method is meant to replace the id-based approach adopted in the default AO3 work filter.
     * This method is used for the following fields:
     *      Warnings, Categories, Fandoms, Characters, Relationships, Additional Tags (Freeform), other tags
     */
    fun excludeTags(vararg tags: String) : WorkFilterRequest {
        // TODO: perhaps joining all of them with ',' first might be better for performance  - reduces function calls
        val tagConcat = tags.joinToString(
            separator = "%2C",
            transform = {
                encodeAdditionalTag(it)
            }
        )

        excludedTagsQueryString = "&work_search[excluded_tag_names]=$tagConcat"
        return this
    }

    /**
     * Shows oneshots only. There is no option to toggle this off, so dont call this if you want to show everything.
     */
    fun showSingleChapterWorksOnly() : WorkFilterRequest {
        showSingleChapterWorksQueryString = "&work_search[single_chapter]=1"
        return this
    }

    /**
     * Controls the ratings of the works shown in the filter results.
     */
    // TODO: convert to exclude ratings
    fun showRatings(
            showNotRated: Boolean,
            showGeneral: Boolean,
            showTeen: Boolean,
            showMature: Boolean,
            showExplicit: Boolean
    ) : WorkFilterRequest {
        // This is functionally separate from the include and exclude tags because im trying to adhere to good
        // coding practices.
        // So that include and exclude tags will have mirrored behaviour.
        // TODO: this method will be implemented using IDs

        /*
        Not rated:  9
        General:    10
        Teen:       11
        Mature:     12
        Explicit:   13
         */

        // initially set to allow all works
        ratingsQueryString = ""

        if (!showNotRated) {
            ratingsQueryString = ratingsQueryString + "&exclude_work_search[rating_ids][]=9"
        }

        if (!showGeneral) {
            ratingsQueryString = ratingsQueryString + "&exclude_work_search[rating_ids][]=10"
        }

        if (!showTeen) {
            ratingsQueryString = ratingsQueryString + "&exclude_work_search[rating_ids][]=11"
        }

        if (!showMature) {
            ratingsQueryString = ratingsQueryString + "&exclude_work_search[rating_ids][]=12"
        }

        if (!showExplicit) {
            ratingsQueryString = ratingsQueryString + "&exclude_work_search[rating_ids][]=13"
        }

        return this
    }

    fun showCrossovers(option: ToggleVariable) : WorkFilterRequest {
        showCrossoversQueryString = "&work_search[crossover]=${option.code}"
        return this
    }

    fun setCompletionStatus(option: ToggleVariable) : WorkFilterRequest {
        completionStatusQueryString = "&work_search[complete]=${option.code}"
        return this
    }

    fun setHitsRange(min: Int = 0, max: Int = 0) : WorkFilterRequest {
        if (min == 0 && max == 0 || min < 0 || max <= 0) {
            // TODO: Note error handling here if there are bugs in the future
            hitsRangeQueryString = ""
        } else if (min == max) {
            hitsRangeQueryString = "&work_search[hits]=$min"
        } else if (min == 0) {
            hitsRangeQueryString = "&work_search[hits]=<$max"
        } else if (max == 0) {
            hitsRangeQueryString = "&work_search[hits]=>$min"
        } else {
            hitsRangeQueryString = "&work_search[hits]=$min-$max"
        }

        return this
    }

    fun setKudosRange(min: Int = 0, max: Int = 0) : WorkFilterRequest {
        if (min == 0 && max == 0) {
            kudosRangeQueryString = ""
        } else if (min == max) {
            kudosRangeQueryString = "&work_search[kudos_count]=$min"
        } else if (min == 0) {
            kudosRangeQueryString = "&work_search[kudos_count]=<$max"
        } else if (max == 0) {
            kudosRangeQueryString = "&work_search[kudos_count]=>$min"
        } else {
            kudosRangeQueryString = "&work_search[kudos_count]=$min-$max"
        }

        return this
    }

    fun setCommentsRange(min: Int = 0, max: Int = 0) : WorkFilterRequest {
        if (min == 0 && max == 0) {
            commentsRangeQueryString = ""
        } else if (min == max) {
            commentsRangeQueryString = "&work_search[comments_count]=$min"
        } else if (min == 0) {
            commentsRangeQueryString = "&work_search[comments_count]=<$max"
        } else if (max == 0) {
            commentsRangeQueryString = "&work_search[comments_count]=>$min"
        } else {
            commentsRangeQueryString = "&work_search[comments_count]=$min-$max"
        }

        return this
    }

    fun setBookmarksRange(min: Int = 0, max: Int = 0) : WorkFilterRequest {
        if (min == 0 && max == 0) {
            bookmarksRangeQueryString = ""
        } else if (min == max) {
            bookmarksRangeQueryString = "&work_search[bookmarks_count]=$min"
        } else if (min == 0) {
            bookmarksRangeQueryString = "&work_search[bookmarks_count]=<$max"
        } else if (max == 0) {
            bookmarksRangeQueryString = "&work_search[bookmarks_count]=>$min"
        } else {
            bookmarksRangeQueryString = "&work_search[bookmarks_count]=$min-$max"
        }

        return this
    }

    fun setWordCountRange(min: Int = 0, max: Int = 0) : WorkFilterRequest {
        // AO3 doesnt entertain 0 so it can be used as a default
        if (min <= 0 && max <= 0) {
            return this
        }

        wordCountRangeQueryString = "&work_search[words_from]=$min" + "&work_search[words_to]=$max"
        return this
    }

    fun setDateUpdatedRange(fromDate: String = "", toDate: String = "") : WorkFilterRequest {
        // YYYY-MM-DD
        // fromDate <= toDate - this is valid and is accepted. fromDate > toDate is invalid and is ignored.

        if (fromDate.isEmpty() || toDate.isEmpty()) {
            dateUpdatedRangeQueryString = ""
        } else if (toDate.isEmpty()) {
            dateUpdatedRangeQueryString = "&work_search[date_from]=$fromDate"
        } else if (fromDate.isEmpty()) {
            dateUpdatedRangeQueryString = "&work_search[date_to]=$toDate"
            // FIXME: find a new class to handle dates
        } else if (LocalDate.parse(fromDate).compareTo(LocalDate.parse(toDate)) <= 0) {
            dateUpdatedRangeQueryString = "&work_search[date_from]=$fromDate&work_search[date_to]=$toDate"
        }

        return this
    }

    fun setLanguage(language: String) : WorkFilterRequest {
        // TODO: convert everything to enum
        // FIXME: currently this defaults to all languages.
        languageQueryString = "&work_search[language_id]="
        return this
    }

    /**
     * Compulsory call
     */
    fun setSortOrder(order: SortOrder) : WorkFilterRequest {
        sortOrderQueryString =  "&work_search[sort_column]=${order.code}"
        return this
    }

    /**
     * Call to set page number, otherwise first page will be retrieved from AO3.
     */
    fun setPageNumber(pageNumber: Int) : WorkFilterRequest {
        pageNumberQueryString = "&page=$pageNumber"
        return this
    }

    /**
     * Enumerations
     */

    enum class SortOrder(val code: String) {
        DATE_UPDATED("revised_at")
    }

    enum class ToggleVariable(val code: String) {
        ALL(""),
        WITH_VAR_ONLY("T"),
        WITHOUT_VAR("F")
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
