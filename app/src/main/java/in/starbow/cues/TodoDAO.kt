package `in`.starbow.cues

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDAO {
    @Insert()
    suspend fun insertTask(todoModel: TodoModel):Long
    @Query("SELECT * FROM TodoModel where isFinished ==0")
    fun getTask():LiveData<List<TodoModel>>
    @Query("Update TodoModel set isFinished = 1 where id=:uid")
    fun finishTask(uid:Long)

    @Query("Delete from TodoModel where id=:uid")
    fun deleTask(uid:Long)


}