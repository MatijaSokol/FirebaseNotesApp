package hr.ferit.matijasokol.firebasenotesapp.ui.adapters

import android.graphics.Canvas
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import hr.ferit.matijasokol.firebasenotesapp.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class RecyclerMoveSwipeCallback(private val onItemSwiped: (Int, Int) -> Unit) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onItemSwiped(viewHolder.adapterPosition, direction)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            .addSwipeLeftBackgroundColor(ContextCompat.getColor(recyclerView.context, R.color.colorAccent))
            .addSwipeLeftActionIcon(R.drawable.ic_delete)
            .addSwipeRightBackgroundColor(ContextCompat.getColor(recyclerView.context, R.color.colorPrimary))
            .addSwipeRightActionIcon(R.drawable.ic_archive)
            .create()
            .decorate()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}