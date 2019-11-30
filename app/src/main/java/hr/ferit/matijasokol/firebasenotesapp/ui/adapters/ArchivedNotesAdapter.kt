package hr.ferit.matijasokol.firebasenotesapp.ui.adapters

import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import hr.ferit.matijasokol.firebasenotesapp.R
import hr.ferit.matijasokol.firebasenotesapp.common.gone
import hr.ferit.matijasokol.firebasenotesapp.common.loadImage
import hr.ferit.matijasokol.firebasenotesapp.common.visible
import hr.ferit.matijasokol.firebasenotesapp.model.Note
import kotlinx.android.synthetic.main.note_item.view.*

class ArchivedNotesAdapter(
    options: FirestoreRecyclerOptions<Note>,
    private val onCheckBoxCheckChanged: (DocumentSnapshot, Boolean) -> Unit
) : FirestoreRecyclerAdapter<Note, ArchivedNotesAdapter.NoteItemViewHolder>(options) {

    private val TAG = "[DEBUG] ArchivedNotesAd"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteItemViewHolder(view, onCheckBoxCheckChanged)
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int, model: Note) {
        holder.bind(model)
    }

    fun getItemSnapshot(position: Int): DocumentSnapshot {
        return snapshots.getSnapshot(position)
    }

    inner class NoteItemViewHolder(
        private val containerView: View,
        private val onCheckBoxCheckChanged: (DocumentSnapshot, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(containerView) {

        fun bind(item: Note) {
            with(containerView) {
                textViewTitle.text = item.title

                val date = DateFormat.format("EEEE, MMM d, yyyy kk:mm:ss", item.timeCreated.toDate())
                textViewTimeCreated.text = date.toString()

                textViewDescription.text = item.desciption
                textViewDescription.visibility = if (item.isExpanded) {
                    setImageVisibility(item)
                    View.VISIBLE
                } else {
                    setImageVisibility(item)
                    View.GONE
                }

                checkBoxIsCompleted.isChecked = item.isCompleted

                checkBoxIsCompleted.setOnCheckedChangeListener { _, isChecked ->
                    if (getItem(adapterPosition).isCompleted != isChecked) {
                        Log.d(TAG, "bind: $isChecked")
                        onCheckBoxCheckChanged(snapshots.getSnapshot(adapterPosition), isChecked)
                    }
                }

                item.imageUrl?.let { image.loadImage(it) }

                setOnClickListener { onItemClicked(item) }
            }
        }

        private fun setImageVisibility(item: Note) {
            if (item.imageUrl != null) {
                containerView.image.visible()
            } else {
                containerView.image.gone()
            }
        }

        private fun onItemClicked(item: Note) {
            item.isExpanded = !item.isExpanded
            notifyItemChanged(adapterPosition)
        }
    }
}