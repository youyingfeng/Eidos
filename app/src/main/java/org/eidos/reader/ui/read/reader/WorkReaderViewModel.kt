package org.eidos.reader.ui.read.reader

import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map
import org.eidos.reader.model.domain.Comment
import org.eidos.reader.model.domain.Work
import org.eidos.reader.remote.requests.CommentsRequest
import org.eidos.reader.remote.requests.WorkRequest
import org.eidos.reader.repository.EidosRepository
import timber.log.Timber

class WorkReaderViewModel
    constructor(
        private val workURL: String,
        private val fetchFromDatabase: Boolean,
        private val repository: EidosRepository,
        private val htmlImageGetter: Html.ImageGetter
    )
    : ViewModel()
{
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

    val textSize = repository.uiPreferencesFlow
        .map { it.readerTextSize }
        .asLiveData()

    private val _workTitle = MutableLiveData<String>()
    val workTitle: LiveData<String>
        get() = _workTitle

    private val _workAuthors = MutableLiveData<List<String>>()
    val workAuthors: LiveData<List<String>>
        get() = _workAuthors

    val uiPreferences = repository.uiPreferencesFlow.asLiveData()

    init {
        viewModelScope.launch {
            if (fetchFromDatabase) {
                getWorkFromDatabase()
            } else {
                getWorkFromRemote()
            }

        }
    }

    private suspend fun getWorkFromRemote() {
        withContext(Dispatchers.IO) {
            val workRequest = WorkRequest(workURL)
            work = repository.getWorkFromAO3(workRequest)
            _workTitle.postValue(work.title)
            _workAuthors.postValue(work.authors)
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

    private suspend fun getWorkFromDatabase() {
        withContext(Dispatchers.IO) {
            work = repository.getWorkFromDatabase(workURL)
            _workTitle.postValue(work.title)
            _workAuthors.postValue(work.authors)

            // TODO: refactor out below code as this is duplicate
            loadFirstChapter()
            _chapterTitles.postValue(work.chapters.let { chapterList ->
                val titles = mutableListOf<String>()
                for (i in chapterList.indices) {
                    titles.add("Chapter ${i + 1}: ${chapterList[i].title}")
                }
                return@let titles
            })
            Timber.i("Coroutines: Work fetched from DB")
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
        return HtmlCompat.fromHtml(
            html,
            HtmlCompat.FROM_HTML_MODE_LEGACY,
            htmlImageGetter,
            null
        )
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

    /* COMMENTS */
    private var _comments = MutableLiveData<List<Comment>>(emptyList())
    val comments: LiveData<List<Comment>>
        get() = _comments

    private val commentsPage = 1

    fun getNextCommentsPage() {
        Timber.i("getNextCommentsPage called!")
        viewModelScope.launch(Dispatchers.IO) {
            val commentsRequest = CommentsRequest(
                work.chapters[currentChapterIndex].chapterID,
                commentsPage
            )

            val newComments = repository.getCommentsFromAO3(commentsRequest)

            var currentList = comments.value!!.toMutableList()
            currentList.addAll(newComments)
            _comments.postValue(currentList)
            Timber.i("currentlist size = ${currentList.size}")
            Timber.i("getNextCommentsPage posted!")
        }
    }

    fun saveWorkToDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertWorkIntoDatabase(work)
            Timber.i("Work saved!")
        }
    }

    /* PREFERENCES */
    fun updateTextSize(newTextSize: Float) {
        viewModelScope.launch {
            repository.updateTextSize(newTextSize)
        }
    }

    fun setNightMode(useNightMode: Boolean) {
        viewModelScope.launch {
            repository.setNightMode(useNightMode)
        }
    }
}