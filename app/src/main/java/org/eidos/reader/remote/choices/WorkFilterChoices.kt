package org.eidos.reader.remote.choices

import android.os.Parcel
import android.os.Parcelable

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

    var includedTags: MutableList<String> = mutableListOf<String>(),
    var excludedTags: MutableList<String> = mutableListOf<String>(),

    var searchTerm: String = "",
    var language: String = "",
    var sortOrder : String = ""
) : Parcelable
{
    // stringifying choices should be done in request constructor, not in choices
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList()?.toMutableList() ?: mutableListOf<String>(),
        parcel.createStringArrayList()?.toMutableList() ?: mutableListOf<String>(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (showRatingGeneral) 1 else 0)
        parcel.writeByte(if (showRatingTeen) 1 else 0)
        parcel.writeByte(if (showRatingMature) 1 else 0)
        parcel.writeByte(if (showRatingExplicit) 1 else 0)
        parcel.writeByte(if (showRatingNotRated) 1 else 0)
        parcel.writeByte(if (showWarningNone) 1 else 0)
        parcel.writeByte(if (showWarningViolence) 1 else 0)
        parcel.writeByte(if (showWarningCharacterDeath) 1 else 0)
        parcel.writeByte(if (showWarningUnderage) 1 else 0)
        parcel.writeByte(if (showWarningRape) 1 else 0)
        parcel.writeByte(if (showWarningChoseNoWarnings) 1 else 0)
        parcel.writeByte(if (mustContainAllWarnings) 1 else 0)
        parcel.writeByte(if (showCategoryGen) 1 else 0)
        parcel.writeByte(if (showCategoryFM) 1 else 0)
        parcel.writeByte(if (showCategoryFF) 1 else 0)
        parcel.writeByte(if (showCategoryMM) 1 else 0)
        parcel.writeByte(if (showCategoryMulti) 1 else 0)
        parcel.writeByte(if (showCategoryOther) 1 else 0)
        parcel.writeByte(if (showSingleChapterWorksOnly) 1 else 0)
        parcel.writeByte(if (showCrossovers) 1 else 0)
        parcel.writeByte(if (showNonCrossovers) 1 else 0)
        parcel.writeByte(if (showCompletedWorks) 1 else 0)
        parcel.writeByte(if (showIncompleteWorks) 1 else 0)
        parcel.writeInt(hitsMin)
        parcel.writeInt(hitsMax)
        parcel.writeInt(kudosMin)
        parcel.writeInt(kudosMax)
        parcel.writeInt(commentsMin)
        parcel.writeInt(commentsMax)
        parcel.writeInt(bookmarksMin)
        parcel.writeInt(bookmarksMax)
        parcel.writeInt(wordCountMin)
        parcel.writeInt(wordCountMax)
        parcel.writeString(dateUpdatedMin)
        parcel.writeString(dateUpdatedMax)
        parcel.writeStringList(includedTags)
        parcel.writeStringList(excludedTags)
        parcel.writeString(searchTerm)
        parcel.writeString(language)
        parcel.writeString(sortOrder)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WorkFilterChoices> {
        override fun createFromParcel(parcel: Parcel): WorkFilterChoices {
            return WorkFilterChoices(parcel)
        }

        override fun newArray(size: Int): Array<WorkFilterChoices?> {
            return arrayOfNulls(size)
        }
    }
}
