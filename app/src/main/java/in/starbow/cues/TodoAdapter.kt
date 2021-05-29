package `in`.starbow.cues

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(val list:List<TodoModel>):RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_todo,
            parent,false))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.binder(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }
    class TodoViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun binder(todoModel: TodoModel) {
            with(itemView){
                val colors = resources.getIntArray(R.array.random_color);
                val randomColor = colors[Random().nextInt(colors.size)]
                val viewColorTag:View =findViewById(R.id.viewColorTag);
                viewColorTag.setBackgroundColor(randomColor)
                val txtShowTitle:TextView=findViewById(R.id.txtShowTitle)
                val txtShowCategory:TextView=findViewById(R.id.txtShowCategory)
                val txtShowTask:TextView=findViewById(R.id.txtShowTask)

                txtShowTitle.text=todoModel.title
                txtShowCategory.text=todoModel.categgory
                txtShowTask.text=todoModel.description
              updateTime(todoModel.time)
                updateDate(todoModel.date)

            }
        }
        fun updateTime(time:Long) {
          val txtShowTime:TextView= itemView.findViewById(R.id.txtShowTime)

            val myformat = "h:mm a"
            val sdf= SimpleDateFormat(myformat)
            txtShowTime.text=sdf.format(Date(time))


        }
        private fun updateDate(time:Long)
        {
            val txtShowDate:TextView= itemView.findViewById(R.id.txtShowDate)
            val myformat="EEE, d MMM yyyy"

            val sdf= SimpleDateFormat(myformat)
            txtShowDate.text= sdf.format(Date(time))

        }

    }


}