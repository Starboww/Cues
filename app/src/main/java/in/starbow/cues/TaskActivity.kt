package `in`.starbow.cues

import `in`.starbow.cues.databinding.ActivityTaskBinding
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.*

const val DB_NAME = "todo.db"
class TaskActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var bind:ActivityTaskBinding
    lateinit var myCalander:Calendar
    lateinit var dateSetListner:DatePickerDialog.OnDateSetListener//anything inside the calender if clicked will be stored inside this variable
    lateinit var timeSetListner:TimePickerDialog.OnTimeSetListener
    private val lable = arrayListOf<String>("Personal","Shopping","Medicines","Fitness","Banking","Task","Study","Business")
    val db by lazy{
        AppDatabase.getDatabase(this)
    }

    var finalDate = 0L
    var finalTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind= ActivityTaskBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.dateEdt.setOnClickListener(this)
        bind.timeEdt.setOnClickListener(this)
        bind.saveBtn.setOnClickListener(this)
        setUpSpinner()


    }

    private fun setUpSpinner() {
      val adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,lable)
        lable.sort()
        bind.spinnerCategory.adapter= adapter
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.dateEdt->{
                setListner()
            }
            R.id.timeEdt->{
                setTimeListner()
            }
            R.id.saveBtn->{
                saveTodo()
            }
        }

    }

    private fun saveTodo() {
        val category = bind.spinnerCategory.selectedItem.toString()
        val title = bind.titleInpLay.editText?.text.toString();
        val description = bind.taskInpLay.editText?.text.toString()

        GlobalScope.launch (Dispatchers.Main){
            val id = withContext(Dispatchers.IO){
                return@withContext db.todoDao().insertTask(
                    TodoModel(
                        title,
                        description,
                        category,
                        finalDate,
                        finalTime
                    )
                )
            }
            finish()
        }
    }

    private fun setTimeListner() {
        myCalander = Calendar.getInstance()
        timeSetListner=TimePickerDialog.OnTimeSetListener() { _:TimePicker, hourOfDay:Int, min:Int ->
            myCalander.set(Calendar.HOUR_OF_DAY,hourOfDay)
            myCalander.set(Calendar.MINUTE,min)

            updateTime()
        }

        val timePickerDialog=TimePickerDialog(
            this,timeSetListner,myCalander.get(Calendar.HOUR_OF_DAY),myCalander.get(Calendar.MINUTE),false
        )

        timePickerDialog.show()
    }

    private fun updateTime() {
        val myformat = "h:mm a"
        val sdf= SimpleDateFormat(myformat)
        finalTime=myCalander.time.time
        bind.timeEdt.setText(sdf.format(myCalander.time))

    }
    private fun setListner() {
        myCalander = Calendar.getInstance()
        dateSetListner=DatePickerDialog.OnDateSetListener { datePicker:DatePicker, year:Int, month:Int, dayOfMonth:Int ->
            myCalander.set(Calendar.YEAR,year)
            myCalander.set(Calendar.MONTH,month)
            myCalander.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDate()
        }
        val datePickerDialog = DatePickerDialog(
            this,dateSetListner,myCalander.get(Calendar.YEAR),myCalander.get(Calendar.MONTH),myCalander.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate=System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
     //Fri, 28 May 2021
    val myformat = "EEE, d MMM yyyy"
        val sdf= SimpleDateFormat(myformat)
        finalDate=myCalander.time.time
        bind.dateEdt.setText(sdf.format(myCalander.time))
        bind.timeInptLay.visibility =View.VISIBLE
    }

}