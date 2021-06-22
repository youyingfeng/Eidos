package org.eidos.reader.remote.requests

import android.os.Parcel
import android.os.Parcelable
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

data class WorkFilterRequest(
    val tagName: String,
    var workFilterChoices: WorkFilterChoices = WorkFilterChoices()
) : Parcelable {

    private val BASE_QUERY_STRING =
        "/tags/${encodeMainTag(tagName)}/works?utf8=✓&commit=Sort+and+Filter"
    var pageNumber: Int = 1

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

    private val includedTagsQueryString: String
        get() {
            val tagConcat = workFilterChoices.includedTags.joinToString(
                separator = "%2C",
                transform = {
                    encodeAdditionalTag(it)
                }
            )

            return "&work_search[other_tag_names]=$tagConcat"
        }

    private val excludedTagsQueryString: String
        get() {
            val tagConcat = workFilterChoices.excludedTags.joinToString(
                separator = "%2C",
                transform = {
                    encodeAdditionalTag(it)
                }
            )

            return "&work_search[excluded_tag_names]=$tagConcat"
        }

    private val showSingleChapterWorksOnlyQueryString: String
        get() {
            if (workFilterChoices.showSingleChapterWorksOnly) {
                return "&work_search[single_chapter]=1"
            } else {
                return ""
            }
        }

    private val ratingsQueryString: String
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

    private val warningsQueryString: String
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

    private val crossoversQueryString: String
        get() {
            if (workFilterChoices.showCrossovers xor workFilterChoices.showNonCrossovers) {
                return "&work_search[crossover]=${if (workFilterChoices.showCrossovers) "T" else "F"}"
            } else {
                return ""
            }
        }

    private val completionStatusQueryString: String
        get() {
            if (workFilterChoices.showCompletedWorks xor workFilterChoices.showIncompleteWorks) {
                return "&work_search[complete]=${if (workFilterChoices.showCompletedWorks) "T" else "F"}"
            } else {
                return ""
            }
        }

    private val hitsRangeQueryString: String
        get() {
            if (workFilterChoices.hitsMin == 0 && workFilterChoices.hitsMax == 0 ||
                workFilterChoices.hitsMin < 0 ||
                workFilterChoices.hitsMax <= 0
            ) {
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

    private val kudosRangeQueryString: String
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

    private val commentsRangeQueryString: String
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

    private val bookmarksRangeQueryString: String
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

    private val wordCountRangeQueryString: String
        get() {
            if (workFilterChoices.wordCountMin <= 0 && workFilterChoices.wordCountMax <= 0) {
                return ""
            }
            return "&work_search[words_from]=${workFilterChoices.wordCountMin}" +
                    "&work_search[words_to]=${workFilterChoices.wordCountMax}"
        }

    private val dateUpdatedRangeQueryString: String
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

    private val searchTermQueryString: String
        get() {
            if (workFilterChoices.searchTerm.isEmpty()) {
                return ""
            }

            return "&work_search[query]=${workFilterChoices.searchTerm}"
        }

    private val languageQueryString: String
        get() {
            return if (workFilterChoices.language.isBlank()) {
                "&work_search[language_id]="
            } else {
                "&work_search[language_id]=${LANGUAGES[workFilterChoices.language]}"
            }
        }

    private val sortOrderQueryString: String
        get() {
            return if (workFilterChoices.sortOrder.isBlank()) {
                "&work_search[sort_column]=revised_at"
            } else {
                "&work_search[sort_column]=${SORT_OPTIONS[workFilterChoices.sortOrder]}"
            }
        }

    private val pageNumberQueryString: String
        get() {
            return "&page=$pageNumber"
        }

    // Parcelable stuff

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readParcelable(WorkFilterChoices::class.java.classLoader) ?: WorkFilterChoices()
    ) {
        // these are for the public vars, but I dont need them
//        pageNumber = parcel.readInt()
//        queryStringWithoutPageArgs = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tagName)
        parcel.writeParcelable(workFilterChoices, flags)
//        parcel.writeInt(pageNumber)
//        parcel.writeString(queryStringWithoutPageArgs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<WorkFilterRequest> =
            object : Parcelable.Creator<WorkFilterRequest> {
                override fun createFromParcel(parcel: Parcel): WorkFilterRequest {
                    return WorkFilterRequest(parcel)
                }

                override fun newArray(size: Int): Array<WorkFilterRequest?> {
                    return arrayOfNulls(size)
                }
            }

        private fun encodeMainTag(tag: String): String {
            return tag.replace("/", "*s*")
                .replace("&", "*a*")
        }

        private fun encodeAdditionalTag(tag: String): String {
            return URLEncoder.encode(tag, "UTF-8")
        }
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
