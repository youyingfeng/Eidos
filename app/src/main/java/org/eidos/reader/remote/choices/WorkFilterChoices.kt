package org.eidos.reader.remote.choices

data class WorkFilterChoices(
    var showRatingGeneral: Boolean = true,
    var showRatingTeen: Boolean = true,
    var showRatingMature: Boolean = true,
    var showRatingExplicit: Boolean = true,
    var showRatingNotRated: Boolean = true,
    // warnings,
    var showWarningNone: Boolean = true,
    var showWarningViolence: Boolean = true,
    var showWarningCharacterDeath: Boolean = true,
    var showWarningUnderage: Boolean = true,
    var showWarningRape: Boolean = true,
    var showWarningChoseNoWarnings: Boolean = true,
    var mustContainAllWarnings: Boolean = false,

    var showCategoryGen: Boolean = true,
    var showCategoryFM: Boolean = true,
    var showCategoryFF: Boolean = true,
    var showCategoryMM: Boolean = true,
    var showCategoryMulti: Boolean = true,
    var showCategoryOther: Boolean = true,

    var showSingleChapterWorksOnly: Boolean = false,
    var showCrossovers: Boolean = true,
    var showNonCrossovers: Boolean = true,
    var showCompletedWorks: Boolean = true,
    var showIncompleteWorks: Boolean = true,

    var hitsMin: Int = 0,
    var hitsMax: Int = 0,
    var kudosMin: Int = 0,
    var kudosMax: Int = 0,
    var commentsMin: Int = 0,
    var commentsMax: Int = 0,
    var bookmarksMin: Int = 0,
    var bookmarksMax: Int = 0,
    var wordCountMin: Int = 0,
    var wordCountMax: Int = 0,
    var dateUpdatedMin: String = "",
    var dateUpdatedMax: String = "",

    var includedTags: List<String> = emptyList(),
    var excludedTags: List<String> = emptyList(),

    var searchTerm: String = "",
    var language: String = "",
    var sortOrder : String = ""
) {
    // stringifying choices should be done in request constructor, not in choices
}