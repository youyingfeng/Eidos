package org.eidos.reader.ui.reader

import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eidos.reader.model.Work
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.requests.WorkRequest
import org.eidos.reader.repository.EidosRepository
import timber.log.Timber

class WorkReaderViewModel(private var workURL: String) : ViewModel() {
    // TODO: make this non-nullable after writing coroutine
    private lateinit var work : Work
    private var currentChapterIndex: Int = 0

    // Livedata variable for currentText
    private val _currentChapterBody = MutableLiveData<Spanned>()
    val currentChapterBody: LiveData<Spanned>
        get() = _currentChapterBody

    init {
        viewModelScope.launch {
            getWorkFromRemote()
        }
    }

    private suspend fun getWorkFromRemote() {
        withContext(Dispatchers.IO) {
            val workRequest = WorkRequest(workURL)
            work = EidosRepository.getWorkFromAO3(workRequest)
            _currentChapterBody.postValue(convertHtmlToSpanned(work.chapters[0].chapterBody))
            Timber.i("Coroutines: Work fetched from Remote")
        }
    }

    /* Chapter getters */
    // the below methods should provide a complete FSM
    fun getFirstChapter() : Spanned {
        currentChapterIndex = 0
        return convertHtmlToSpanned(work.chapters[currentChapterIndex].chapterBody)
    }

    fun getNextChapter() : Spanned {
        currentChapterIndex++
        if (currentChapterIndex >= work.chapterCount) {
            currentChapterIndex = work.chapterCount - 1
        }
        return convertHtmlToSpanned(work.chapters[currentChapterIndex].chapterBody)
    }

    fun getPreviousChapter() : Spanned {
        currentChapterIndex--
        if (currentChapterIndex < 0) {
            currentChapterIndex = 0
        }
        return convertHtmlToSpanned(work.chapters[currentChapterIndex].chapterBody)
    }

    fun getChapterAtIndex(index: Int) : Spanned {
        currentChapterIndex = index
        return convertHtmlToSpanned(work.chapters[currentChapterIndex].chapterBody)
    }

    /* Utility methods */
    private fun convertHtmlToSpanned(html: String) : Spanned {
        return HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}