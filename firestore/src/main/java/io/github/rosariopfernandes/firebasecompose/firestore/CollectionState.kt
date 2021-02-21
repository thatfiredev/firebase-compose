package io.github.rosariopfernandes.firebasecompose.firestore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

/**
 * A value holder where reads to the [value] property during the execution of a [Composable]
 * function, the current [RecomposeScope] will be subscribed to changes of that value. When the
 * [value] property is changed in that Firestore Collection, a recomposition of any subscribed
 * [RecomposeScope]s will be scheduled.
 *
 * @see [State]
 * @see [collectionStateOf]
 */
interface CollectionState : State<List<DocumentSnapshot>>, LifecycleObserver {
    override val value: List<DocumentSnapshot>
    val error: FirebaseFirestoreException?
    val loading: Boolean
    fun startListening()
    fun stopListening()
    operator fun component1(): List<DocumentSnapshot>
    operator fun component2(): FirebaseFirestoreException?
    operator fun component3(): Boolean
}

/**
 * Return a new [CollectionState] initialized with the passed [query]
 *
 * The CollectionState class is a single value holder whose reads are observed by
 * Compose.
 *
 * @param query the collection query to be observed
 * @param lifecycleOwner the lifecycle owner that the state should react to
 *
 * @see State
 * @see CollectionState
 */
fun collectionStateOf(
    query: Query,
    lifecycleOwner: LifecycleOwner? = null
) = object : CollectionState {
    private var listener: ListenerRegistration? = null
    private var documentsState: List<DocumentSnapshot> by mutableStateOf(listOf())
    private var errorState: FirebaseFirestoreException? by mutableStateOf(null)
    private var loadingState: Boolean by mutableStateOf(true)

    override val error: FirebaseFirestoreException?
        get() = errorState

    override val value: List<DocumentSnapshot>
        get() = documentsState

    override val loading: Boolean
        get() = loadingState

    init {
        lifecycleOwner?.lifecycle?.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun startListening() {
        if (listener == null) {
            listener = query.addSnapshotListener { value, error ->
                loadingState = false
                value?.let { documentsState = it.documents }
                error?.let { errorState = it }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun stopListening() {
        listener?.remove()
    }

    override fun component1() = value

    override fun component2() = error

    override fun component3() = loading
}

