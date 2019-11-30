package hr.ferit.matijasokol.firebasenotesapp.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Note(
    var title: String = "",
    var desciption: String = "",
    var userId: String = "",
    var timeCreated: Timestamp = Timestamp(Date()),
    var isCompleted: Boolean = false,
    var imageUrl: String? = null,
    var isArchived: Boolean = false
) : Parcelable {

    @IgnoredOnParcel
    @get:Exclude
    var isExpanded: Boolean = false

    @IgnoredOnParcel
    @get:Exclude
    var imageUri: String? = null

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) {
            return false
        }

        other as Note

        if (title != other.title || desciption != other.desciption || isCompleted != other.isCompleted || timeCreated != other.timeCreated || userId != other.userId) {
            return false
        }

        return true
    }
}