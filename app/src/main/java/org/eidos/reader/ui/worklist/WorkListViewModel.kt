package org.eidos.reader.ui.worklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.repository.EidosRepository
import timber.log.Timber

class WorkListViewModel(val workFilterRequest: WorkFilterRequest) : ViewModel() {

    /* BEGIN Variables for WorkListFragment */
    private var _workBlurbs = MutableLiveData<List<WorkBlurb>>(emptyList())
    val workBlurbs: LiveData<List<WorkBlurb>>
        get() = _workBlurbs

    private var largestPageNumber = 1

    // possibly hacky fix, see if it is possible to improve.
    private var isFetchingWorks = false
    /* END Variables for WorkListFragment */

    /* BEGIN Variables for FilterDialogFragment */
    // TODO: add arrays to store shit
    // Note: autocomplete stuff should be shoved in another ViewModel
    /* END Variables for FilterDialogFragment */

    init {
        initialiseWorkBlurbs()
    }

    private fun initialiseWorkBlurbs() {
        // clear the data
        _workBlurbs.value = emptyList()

        Timber.i("Fetching Work Blurbs")
        viewModelScope.launch(Dispatchers.IO) {
            _workBlurbs.postValue(getWorkBlurbs(workFilterRequest))
            Timber.i("WorkBlurbs successfully fetched")
        }
    }

    private suspend fun getWorkBlurbs(request: WorkFilterRequest) : List<WorkBlurb> {
        return EidosRepository.getWorkBlurbsFromAO3(request)
    }

    fun getNextPage() {
        if (!isFetchingWorks) {
            // set the bool to stop repeated fetching
            isFetchingWorks = true
            largestPageNumber++
            Timber.i("getNextPage() called")

            workFilterRequest.pageNumber = largestPageNumber
            workFilterRequest.updateQueryString()

            val currentList : MutableList<WorkBlurb> = workBlurbs.value!!.toMutableList()

            viewModelScope.launch(Dispatchers.IO) {
                currentList.addAll(getWorkBlurbs(workFilterRequest))
                _workBlurbs.postValue(currentList)
                isFetchingWorks = false
                Timber.i("More WorkBlurbs successfully fetched")
            }
        } else {
            Timber.i("Currently fetching works, extra requests blocked")
        }
    }

    fun resetPages() {
        _workBlurbs.value = emptyList()
    }

    // oh dear the param list is like 30 bloody lines
    /**
     * Sets the WorkFilterRequest params to the params supplied below.
     * This method expects the input to be the full state of the form, not the diff.
     */
    // FIXME: This is tightly coupled to the workFilterRequest
    fun updateWorkFilter(
            includedTags: List<String> = emptyList(),
            excludedTags: List<String> = emptyList(),

            showRatingGeneral: Boolean = true,
            showRatingTeen: Boolean = true,
            showRatingMature: Boolean = true,
            showRatingExplicit: Boolean = true,
            showRatingNotRated: Boolean = true,

            showWarningNone: Boolean = true,
            showWarningViolence: Boolean = true,
            showWarningCharacterDeath: Boolean = true,
            showWarningUnderage: Boolean = true,
            showWarningRape: Boolean = true,
            showWarningChoseNoWarnings: Boolean = true,
            mustContainAllWarnings: Boolean = false,

            showCategoryGen: Boolean = true,
            showCategoryFM: Boolean = true,
            showCategoryFF: Boolean = true,
            showCategoryMM: Boolean = true,
            showCategoryMulti: Boolean = true,
            showCategoryOther: Boolean = true,

            showSingleChapterWorksOnly: Boolean = false,
            showCrossovers: Boolean = true,
            showNonCrossovers: Boolean = true,
            showCompletedWorks: Boolean = true,
            showIncompleteWorks: Boolean = true,

            hitsRange: Pair<Int, Int> = Pair(0, 0),
            kudosRange: Pair<Int, Int> = Pair(0, 0),
            commentsRange: Pair<Int, Int> = Pair(0, 0),
            bookmarksRange: Pair<Int, Int> = Pair(0, 0),
            wordCountRange: Pair<Int, Int> = Pair(0, 0),
            dateUpdatedRange: Pair<String, String> = Pair("", ""),

            searchTerm: String = "",
            language: String = "",
            sortOrder: String = "",
            pageNumber: Int = 1
    ) {
        workFilterRequest.includedTags = includedTags
        workFilterRequest.excludedTags = excludedTags

        workFilterRequest.showRatingGeneral = showRatingGeneral
        workFilterRequest.showRatingTeen = showRatingTeen
        workFilterRequest.showRatingMature = showRatingMature
        workFilterRequest.showRatingExplicit = showRatingExplicit
        workFilterRequest.showRatingNotRated = showRatingNotRated

        workFilterRequest.showWarningCharacterDeath = showWarningCharacterDeath
        workFilterRequest.showWarningChoseNoWarnings = showWarningChoseNoWarnings
        workFilterRequest.showWarningNone = showWarningNone
        workFilterRequest.showWarningRape = showWarningRape
        workFilterRequest.showWarningUnderage = showWarningUnderage
        workFilterRequest.showWarningViolence = showWarningViolence
        workFilterRequest.mustContainAllWarnings = mustContainAllWarnings

        workFilterRequest.showCategoryGen = showCategoryGen
        workFilterRequest.showCategoryFF = showCategoryFF
        workFilterRequest.showCategoryFM = showCategoryFM
        workFilterRequest.showCategoryMM = showCategoryMM
        workFilterRequest.showCategoryMulti = showCategoryMulti
        workFilterRequest.showCategoryOther = showCategoryOther

        workFilterRequest.showSingleChapterWorksOnly = showSingleChapterWorksOnly
        workFilterRequest.showCompletedWorks = showCompletedWorks
        workFilterRequest.showIncompleteWorks = showIncompleteWorks
        workFilterRequest.showCrossovers = showCrossovers
        workFilterRequest.showNonCrossovers = showNonCrossovers

        workFilterRequest.hitsRange = hitsRange
        workFilterRequest.kudosRange = kudosRange
        workFilterRequest.commentsRange = commentsRange
        workFilterRequest.bookmarksRange = bookmarksRange
        workFilterRequest.wordCountRange = wordCountRange
        workFilterRequest.dateUpdatedRange = dateUpdatedRange

        workFilterRequest.searchTerm = searchTerm
        workFilterRequest.language = language
        workFilterRequest.sortOrder = sortOrder
        workFilterRequest.pageNumber = pageNumber

        // update the string
        workFilterRequest.updateQueryString()

        // resubmit request
        initialiseWorkBlurbs()
    }
}