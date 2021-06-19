package org.eidos.reader.model

import android.os.Parcel
import android.os.Parcelable

data class WorkBlurb
    constructor(
        val title: String,
        val authors: List<String>,
        val giftees: List<String>,
        val lastUpdatedDate: String,
        val fandoms: List<String>,
        val rating: String,
        val warnings: List<String>,
        val categories: List<String>,
        val completionStatus: Boolean,
        val characters: List<String>,
        val relationships: List<String>,
        val freeforms: List<String>,
        val summary: String,
        val language: String,
        val wordCount: Int,
        val chapterCount: Int,
        val maxChapters: Int,
        val commentsCount: Int,
        val kudosCount: Int,
        val bookmarksCount: Int,
        val hitCount: Int,
        val workURL: String
    )
    : Parcelable
{
    constructor(parcel: Parcel) : this(
        title = parcel.readString()!!,
        authors = parcel.createStringArrayList()!!,
        giftees = parcel.createStringArrayList()!!,
        lastUpdatedDate = parcel.readString()!!,
        fandoms = parcel.createStringArrayList()!!,
        rating = parcel.readString()!!,
        warnings = parcel.createStringArrayList()!!,
        categories = parcel.createStringArrayList()!!,
        completionStatus = parcel.readInt() != 0,
        characters = parcel.createStringArrayList()!!,
        relationships = parcel.createStringArrayList()!!,
        freeforms = parcel.createStringArrayList()!!,
        summary = parcel.readString()!!,
        language = parcel.readString()!!,
        wordCount = parcel.readInt(),
        chapterCount = parcel.readInt(),
        maxChapters = parcel.readInt(),
        commentsCount = parcel.readInt(),
        kudosCount = parcel.readInt(),
        bookmarksCount = parcel.readInt(),
        hitCount = parcel.readInt(),
        workURL = parcel.readString()!!
    ) {
    }

    fun isSimilarTo(other: WorkBlurb): Boolean {
        return title == other.title
                && authors == other.authors
                && giftees == other.giftees
                && lastUpdatedDate == other.lastUpdatedDate
                && fandoms == other.fandoms
                && rating == other.rating
                && warnings == other.warnings
                && categories == other.categories
                && completionStatus == other.completionStatus
                && characters == other.characters
                && relationships == other.relationships
                && freeforms == other.freeforms
                && chapterCount == other.chapterCount
                && wordCount == other.wordCount
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeStringList(authors)
        dest.writeStringList(giftees)
        dest.writeString(lastUpdatedDate)
        dest.writeStringList(fandoms)
        dest.writeString(rating)
        dest.writeStringList(warnings)
        dest.writeStringList(categories)
        dest.writeInt(if (completionStatus) 1 else 0)
        dest.writeStringList(characters)
        dest.writeStringList(relationships)
        dest.writeStringList(freeforms)
        dest.writeString(summary)
        dest.writeString(language)
        dest.writeInt(wordCount)
        dest.writeInt(chapterCount)
        dest.writeInt(maxChapters)
        dest.writeInt(commentsCount)
        dest.writeInt(kudosCount)
        dest.writeInt(bookmarksCount)
        dest.writeInt(hitCount)
        dest.writeString(workURL)
    }

    companion object CREATOR : Parcelable.Creator<WorkBlurb> {
        override fun createFromParcel(parcel: Parcel): WorkBlurb {
            return WorkBlurb(parcel)
        }

        override fun newArray(size: Int): Array<WorkBlurb?> {
            return arrayOfNulls(size)
        }
    }
}
