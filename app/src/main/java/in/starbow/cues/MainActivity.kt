package `in`.starbow.cues

import `in`.starbow.cues.databinding.ActivityMainBinding
import android.content.Intent
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Observer

class MainActivity : AppCompatActivity() {
    private lateinit var bind:ActivityMainBinding
    val list = arrayListOf<TodoModel>()
    var adapter= TodoAdapter(list)

    val db by lazy{
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind= ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setSupportActionBar(bind.toolbar)
        bind.addNewTodo.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
        }
        bind.todoRV.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter=this@MainActivity.adapter
        }
        initSwip()
        db.todoDao().getTask().observe(this, {
            if(!it.isNullOrEmpty()){
                list.clear()
            list.addAll(it)
            adapter.notifyDataSetChanged()
            }else{
                list.clear()
                adapter.notifyDataSetChanged()
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
            //search function code
        val item= menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView
        item.setOnActionExpandListener(object:MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                displayTodo()
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                displayTodo()
                return true
            }

        })
         searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
             override fun onQueryTextSubmit(query: String?): Boolean {
                 return false
             }

             override fun onQueryTextChange(newText: String?): Boolean {
                 if(!newText.isNullOrEmpty()){
                     displayTodo(newText)
                 }
                return true
             }


         })
        return super.onCreateOptionsMenu(menu)
    }

    private fun displayTodo(newText:String="") {
        db.todoDao().getTask().observe(this,androidx.lifecycle.Observer{
            if(it.isNotEmpty()){
                list.clear()
                list.addAll(
                    it.filter {todo ->
                        todo.title.contains(newText,true)
                    }
                )
                adapter.notifyDataSetChanged()
            }
        })
    }

    fun initSwip(){
        val simpleItemTouchCallBack=object: ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                //TODO: Implement Later change pos of todo
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
             val position =viewHolder.adapterPosition
                if(direction==ItemTouchHelper.LEFT){
                    GlobalScope.launch(Dispatchers.IO) {
                    db.todoDao().deleTask(adapter.getItemId(position))}
                }else if(direction==ItemTouchHelper.RIGHT)
                {    GlobalScope.launch(Dispatchers.IO) {
                    db.todoDao().finishTask(adapter.getItemId(position))
                }
                }
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val paint = Paint()
                    val icon: Bitmap

                    if (dX > 0)//swiping left to right
                    {
                        icon =
                            BitmapFactory.decodeResource(resources, R.drawable.ic_check_white_png)
                        paint.color = Color.parseColor("#388E3C")
                        //i have to draw a rectangle
                        canvas.drawRect(
                            itemView.left.toFloat(), itemView.top.toFloat(),
                            itemView.left.toFloat() + dX, itemView.bottom.toFloat(), paint
                        )
                        canvas.drawBitmap(
                            icon,
                            itemView.left.toFloat(),
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )
                    }else{
                        icon =
                            BitmapFactory.decodeResource(resources, R.drawable.ic_delete_white_png)
                        paint.color = Color.parseColor("#D32F2F")
                        //i have to draw a rectangle
                        canvas.drawRect(
                            itemView.right.toFloat()+dX, itemView.top.toFloat(),
                            itemView.right.toFloat() , itemView.bottom.toFloat(), paint
                        )
                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat()-icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )
                    }
                    viewHolder.itemView.translationX=dX

                } else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(bind.todoRV)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.history->{
                startActivity(Intent(this,HistoryActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

}