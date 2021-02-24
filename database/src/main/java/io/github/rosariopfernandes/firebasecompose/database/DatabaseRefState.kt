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
 * [androidx.compose.runtime.RecomposeScope] will be subscribed to changes of that value. When the
 * [value] property is changed in that Database Reference, a recomposition of any subscribed
 * [androidx.compose.runtime.RecomposeScope]s will be scheduled.
 *
 * @see [State]
 * @see [databaseRefStateOf]
 */
interface DatabaseRefState : State<RTDBData>, LifecycleObserver {
    override val value: RTDBData
    fun startListening()
    fun stopListening()
    operator fun component1(): RTDBData
}

/**
 * Return a new [DatabaseRefState] initialized with the passed [databaseRef]
 *
 * The DatabaseRefState class is a single value holder whose reads are observed by
 * Compose.
 *
 * @param databaseRef the database reference to be observed
 * @param lifecycleOwner the lifecycle owner that the state should react to
 *
 * @see State
 * @see DatabaseRefState
 */
fun databaseRefStateOf(
    databaseRef: DatabaseReference,
    lifecycleOwner: LifecycleOwner? = null
) = object : DatabaseRefState {
    private var listener: ValueEventListener? = null
    private var dataState: RTDBData by mutableStateOf(RTDBData.Loading)

    override val value: RTDBData
        get() = dataState

    init {
        lifecycleOwner?.lifecycle?.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun startListening() {
        if (listener == null) {
            listener = databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dataState = RTDBData.Snapshot(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    dataState = RTDBData.Error(error)
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
