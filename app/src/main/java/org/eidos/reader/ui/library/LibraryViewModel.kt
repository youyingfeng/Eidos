package org.eidos.reader.ui.library

import androidx.lifecycle.ViewModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.sqldelight.ColumnAdapter
import org.eidos.reader.model.Chapter

class LibraryViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    companion object {
        // moshi setup
        private val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        private val listChapterType = Types.newParameterizedType(List::class.java, Chapter::class.java)
        private val jsonAdapter: JsonAdapter<List<Chapter>> = moshi.adapter(listChapterType)

        // Adapters
        val listOfStringsAdapter = object : ColumnAdapter<List<String>, String> {
            override fun decode(databaseValue: String) =
                if (databaseValue.isEmpty()) {
                    listOf()
                } else {
                    databaseValue.split(",")
                }

            override fun encode(value: List<String>) = value.joinToString(separator = ",")
        }

        val chaptersAdapter = object : ColumnAdapter<List<Chapter>, String> {
            override fun decode(databaseValue: String) = jsonAdapter.fromJson(databaseValue)!!

            override fun encode(value: List<Chapter>) = jsonAdapter.toJson(value)

        }

    }
}