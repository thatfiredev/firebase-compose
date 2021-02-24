package io.github.rosariopfernandes.firebasecompose.database

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

/**
 * A value holder where reads to the [value] property during the execution of a
 * [androidx.compose.runtime.Composable] function, the current
 * [androidx.compose.runtime.RecomposeScope] will be subscribed to changes
 * of that value. When the [value] property is changed in that Database Reference,
 * a recomposition of any subscribed [androidx.compose.runtime.RecomposeScope]s
 * will be scheduled.
 *
 * @see [State]
 * @see [databaseListRefStateOf]
 */
interface DatabaseListRefState : State<RTDBDataList>, LifecycleObserver {
    override val value: RTDBDataList
    fun startListening()
    fun stopListening()
    operator fun component1(): RTDBDataList
}

/**
 * Return a new [DatabaseListRefState] initialized with the passed [databaseRef]
 *
 * The DatabaseListRefState class is a single value holder whose reads are observed by
 * Compose.
 *
 * @param databaseRef the database reference whose children will be observed
 * @param lifecycleOwner the lifecycle owner that the state should react to
 *
 * @see State
 * @see DatabaseRefState
 */
fun databaseListRefStateOf(
    databaseRef: DatabaseReference,
    lifecycleOwner: LifecycleOwner? = null
) = object : DatabaseListRefState {
    private var listener: ValueEventListener? = null
    private var dataState: RTDBDataList by mutableStateOf(RTDBDataList.Loading)

    override val value: RTDBDataList
        get() = dataState

    init {
        lifecycleOwner?.lifecycle?.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun startListening() {
        if (listener == null) {
            listener = databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.toList()
                    dataState = RTDBDataList.SnapshotList(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    dataState = RTDBDataList.Error(error)
                }
            })
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun stopListening() {
        listener?.let { databaseRef.removeEventListener(it) }
    }

    override fun component1() = value
}
