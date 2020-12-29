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

class WorkFilterRequest(tagname: String) {
    var queryString = "/works?utf8=✓&commit=Sort+and+Filter&tag_id=$tagname"

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
                URLEncoder.encode(it, "UTF-8")
            }
        )

        queryString = queryString + "&work_search[other_tag_names]=" + tagConcat
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
                URLEncoder.encode(it, "UTF-8")
            }
        )

        queryString = queryString + "&work_search[excluded_tag_names]=" + tagConcat
        return this
    }

    /**
     * Shows oneshots only. There is no option to toggle this off, so dont call this if you want to show everything.
     */
    fun showSingleChapterWorksOnly() : WorkFilterRequest {
        queryString = queryString + "&work_search[single_chapter]=1"
        return this
    }

    /**
     * Controls the ratings of the works shown in the filter results.
     */
    // TODO: convert to excluderatings
    fun showRatings(general: Boolean, teen: Boolean, mature: Boolean, explicit: Boolean, notRated: Boolean) : WorkFilterRequest {
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

        when {
            !general    -> queryString = queryString + "&exclude_work_search[rating_ids][]=10"
            !teen       -> queryString = queryString + "&exclude_work_search[rating_ids][]=11"
            !mature     -> queryString = queryString + "&exclude_work_search[rating_ids][]=12"
            !explicit   -> queryString = queryString + "&exclude_work_search[rating_ids][]=13"
            !notRated   -> queryString = queryString + "&exclude_work_search[rating_ids][]=9"
        }

        return this
    }

    fun showCrossovers(option: ToggleVariable) : WorkFilterRequest {
        queryString = queryString + "&work_search[crossover]=${option.code}"
        return this
    }

    fun setCompletionStatus(option: ToggleVariable) : WorkFilterRequest {
        queryString = queryString + "&work_search[complete]=${option.code}"
        return this
    }

    fun setHitsRange(min: Int = 0, max: Int = 0) : WorkFilterRequest {
        if (min == 0 && max == 0) {
            return this
        }

        if (min == max) {
            queryString = queryString + "&work_search[hits]=$min"
        } else if (min == 0) {
            queryString = queryString + "&work_search[hits]=<$max"
        } else if (max == 0) {
            queryString = queryString + "&work_search[hits]=>$min"
        } else {
            queryString = queryString + "&work_search[hits]=$min-$max"
        }

        return this
    }

    fun setKudosRange(min: Int = 0, max: Int = 0) : WorkFilterRequest {
        if (min == 0 && max == 0) {
            return this
        }

        if (min == max) {
            queryString = queryString + "&work_search[kudos_count]=$min"
        } else if (min == 0) {
            queryString = queryString + "&work_search[kudos_count]=<$max"
        } else if (max == 0) {
            queryString = queryString + "&work_search[kudos_count]=>$min"
        } else {
            queryString = queryString + "&work_search[kudos_count]=$min-$max"
        }

        return this
    }

    fun setCommentsRange(min: Int = 0, max: Int = 0) : WorkFilterRequest {
        if (min == 0 && max == 0) {
            return this
        }

        if (min == max) {
            queryString = queryString + "&work_search[comments_count]=$min"
        } else if (min == 0) {
            queryString = queryString + "&work_search[comments_count]=<$max"
        } else if (max == 0) {
            queryString = queryString + "&work_search[comments_count]=>$min"
        } else {
            queryString = queryString + "&work_search[comments_count]=$min-$max"
        }

        return this
    }

    fun setBookmarksRange(min: Int = 0, max: Int = 0) : WorkFilterRequest {
        if (min == 0 && max == 0) {
            return this
        }

        if (min == max) {
            queryString = queryString + "&work_search[bookmarks_count]=$min"
        } else if (min == 0) {
            queryString = queryString + "&work_search[bookmarks_count]=<$max"
        } else if (max == 0) {
            queryString = queryString + "&work_search[bookmarks_count]=>$min"
        } else {
            queryString = queryString + "&work_search[bookmarks_count]=$min-$max"
        }

        return this
    }

    fun setWordCountRange(min: Int = 0, max: Int = 0) : WorkFilterRequest {
        // AO3 doesnt entertain 0 so it can be used as a default
        if (min <= 0 && max <= 0) {
            return this
        }

        queryString = queryString + "&work_search[words_from]=$min" + "&work_search[words_to]=$max"
        return this
    }

    fun setDateUpdatedRange(fromDate: String = "", toDate: String = "") : WorkFilterRequest {
        // YYYY-MM-DD
        // fromDate <= toDate - this is valid and is accepted. fromDate > toDate is invalid and is ignored.

        if (fromDate.length == 0 || toDate.length == 0) {
            return this
        }

        if (toDate.isEmpty()) {
            queryString = queryString + "&work_search[date_from]=$fromDate"
        } else if (fromDate.isEmpty()) {
            queryString = queryString + "&work_search[date_to]=$toDate"
        } else if (LocalDate.parse(fromDate).compareTo(LocalDate.parse(toDate)) <= 0) {
            queryString = queryString + "&work_search[date_from]=$fromDate" + "&work_search[date_to]=$toDate"
        }

        return this
    }

    fun setLanguage(language: String) : WorkFilterRequest {
        // TODO: convert everything to enum
        // FIXME: currently this defaults to all languages.
        queryString = queryString + "&work_search[language_id]="
        return this
    }

    /**
     * Compulsory call
     */
    fun setSortOrder(order: SortOrder) : WorkFilterRequest {
        queryString = queryString + "&work_search[sort_column]=" + order.code
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
