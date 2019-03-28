package uk.co.victoriajanedavis.tindercardstack.cardView.data

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uk.co.victoriajanedavis.tindercardstack.R


class CardStackAdapter: ListAdapter<User, CardStackAdapter.ViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        Log.d("CardStackAdapter", "onCreateViewHolder")
        return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Log.d("CardStackViewHolder", "onBind($position)")
        holder.bind(getItem(position))

    }

    override fun onViewRecycled(holder: ViewHolder) {
        Log.d("CardStackAdapter", "onViewRecycled(id=${holder.user.id}, name=${holder.user.name})")
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        val city: TextView = view.findViewById(R.id.item_city)
        val image: ImageView = view.findViewById(R.id.item_image)

        lateinit var user: User

        fun bind(user: User) {
            this.user = user

            name.text = "${user.id}. ${user.name}"
            itemView.setOnClickListener { v ->
                Toast.makeText(v.context, user.name, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        class ChatDiffCallback : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}
