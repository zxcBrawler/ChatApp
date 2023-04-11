package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import model.Message

class MessageAdapter(private val message: ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.MyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.message_card, parent, false)
        return MyHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.messageBody.text = message[position].body
        holder.timestamp.text = message[position].timestamp
    }

    override fun getItemCount(): Int {
       return message.size
    }
    class MyHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val messageBody : TextView = itemView.findViewById(R.id.message)
        val timestamp : TextView = itemView.findViewById(R.id.timestamp)

    }
}