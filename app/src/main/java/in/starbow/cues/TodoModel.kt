package `in`.starbow.cues

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
class TodoModel(var title:String,
                var description:String,
                var categgory:String,
                var date:Long,
                var time:Long,
                var isFinished:Int =0,
                @PrimaryKey(autoGenerate = true)
                var id:Long=0
                )
