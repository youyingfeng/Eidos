package org.eidos.reader.ui.read.reader

import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eidos.reader.model.Work
import org.eidos.reader.remote.requests.WorkRequest
import org.eidos.reader.repository.EidosRepository
import timber.log.Timber

class WorkReaderViewModel(private var workURL: String) : ViewModel() {
    // TODO: make this non-nullable after writing coroutine
    private lateinit var work : Work

    // this variable is 0-based and represents the chapter's index in the list of chapters
    private var _currentChapterIndex: Int = 0
    val currentChapterIndex : Int
        get() = _currentChapterIndex

    private val _currentChapterIndicatorString = MutableLiveData<String>("")
    val currentChapterIndicatorString : LiveData<String>
        get() = _currentChapterIndicatorString

    private val _currentChapterTitle = MutableLiveData<String>()
    val currentChapterTitle: LiveData<String>
        get() = _currentChapterTitle

    private val _currentChapterBody = MutableLiveData<Spanned>()
    val currentChapterBody: LiveData<Spanned>
        get() = _currentChapterBody

    private val _hasNextChapter = MutableLiveData<Boolean>()
    val hasNextChapter: LiveData<Boolean>
        get() = _hasNextChapter

    private val _hasPreviousChapter = MutableLiveData<Boolean>()
    val hasPreviousChapter: LiveData<Boolean>
        get() = _hasPreviousChapter

    private val _chapterTitles = MutableLiveData<List<String>>()
    val chapterTitles: LiveData<List<String>>
        get() = _chapterTitles

    init {
        viewModelScope.launch {
            getWorkFromRemote()
        }
    }

    private suspend fun getWorkFromRemote() {
        withContext(Dispatchers.IO) {
            val workRequest = WorkRequest(workURL)
            work = EidosRepository.getWorkFromAO3(workRequest)
            loadFirstChapter()
            _chapterTitles.postValue(work.chapters.let { chapterList ->
                val titles = mutableListOf<String>()
                for (i in chapterList.indices) {
                    titles.add("Chapter ${i + 1}: ${chapterList[i].title}")
                }
                return@let titles
            })
            Timber.i("Coroutines: Work fetched from Remote")
        }
    }

    /* Chapter loaders */
    // the below methods should provide a complete FSM
    fun loadFirstChapter() {
        _currentChapterIndex = 0
        updateLiveDataFields()
    }

    fun loadNextChapter() {
        _currentChapterIndex++
        if (_currentChapterIndex >= work.chapterCount) {
            _currentChapterIndex = work.chapterCount - 1
        }
        updateLiveDataFields()
    }

    fun loadPreviousChapter() {
        _currentChapterIndex--
        if (_currentChapterIndex < 0) {
            _currentChapterIndex = 0
        }
        updateLiveDataFields()
    }

    fun loadChapterAtIndex(index: Int) {
        require(0 <= index && index < work.chapterCount)
        _currentChapterIndex = index
        updateLiveDataFields()
    }

    /* Utility methods */
    private fun convertHtmlToSpanned(html: String) : Spanned {
        return HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun updateLiveDataFields() {
        _currentChapterBody.postValue(convertHtmlToSpanned(work
                .chapters[_currentChapterIndex]
                .chapterBody)
        )
        _hasPreviousChapter.postValue(currentChapterIndex != 0)
        _hasNextChapter.postValue(currentChapterIndex != work.chapterCount - 1)
        _currentChapterTitle.postValue(work.chapters[currentChapterIndex].title)
        _currentChapterIndicatorString.postValue("${currentChapterIndex + 1}/${work.chapterCount}"
        )
    }
}