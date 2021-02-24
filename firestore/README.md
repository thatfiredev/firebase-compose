# Firebase Compose for Cloud Firestore

Firebase Compose makes it simple to bind data from Cloud Firestore to your app's UI.

Before using this library, you should be familiar with the following topics:

* [Structuring and querying data in Cloud Firestore][firestore-docs].
* [Compose's LazyColumn][lazycolumn].

## Data model

Suppose you have an app that displays a list of snacks and each snack is a document
 in the `snacks` collection of your database. In your app, you may represent a snack like this:


```kotlin
data class Snack(
    val name: String = ",
    val price: Long = 0L,
    // Default values are needed for Firestore
)
```

For a model class with default values like the `Snack` class above, Firestore can perform automatic
serialization in `DocumentReference#set()` and automatic deserialization in
`DocumentSnapshot#toObject()`. For more information on data mapping in Firestore, see the
documentation on [custom objects][firestore-custom-objects].

## Querying

On the main screen of your app, you may want to show the cheapest 20 snacks.
In Firestore, you would use the following query:

```kotlin
val query = Firebase.firestore.collection("snacks")
        .orderBy("price")
        .limit(20)
```

To retrieve this data without FirebaseUI, you might use `addSnapshotListener` to listen for
live query updates:

```kotlin
query.addSnapshotListener { snapshot, e ->
    e?.let {
        // Handle error
        return@addSnapshotListener
    }

    val chats = snapshot.toObjects<Snack>()

    // Update UI
    // ...
}
```

## Using Firebase Compose to display a list of Documents from a Firestore Collection

If you're displaying a list of data, you likely want to bind the `Snack` objects to one of the
`Lazy` Composables, such as `LazyColumn` or `LazyRow`.

Firebase Compose can help you do this (almost) automatically!


### Using the `CollectionState`

The `CollectionState` binds a `Query` to a `FirestoreCollection` sealed class, which has 3 finite states:
- `FirestoreCollection.Snapshot` - contains the list of documents (as `DocumentSnapshot`) returned from that query.
- `FirestoreCollection.Error` - contains the error returned from the query.
- `FirestoreCollection.Loading` - indicates that no data or error was emitted from the query yet.

When documents are added, removed, or change these updates are automatically applied to your UI
 in real time.

First, create the state using the destructuring declaration provided by the library and the
 `remember { }` function to handle recompositions:

```kotlin
val lifecycleOwner = this@MainActivity // You can also pass a fragment to it
val (result) = remember { collectionStateOf(query, lifecycleOwner) }
```

Now you can get the list of snacks by checking the `result` state and passing the `result.list` property
 to the `items()` extension function of a `LazyColumn` or `LazyRow` composable:
```kotlin
if (result is FirestoreCollection.Snapshot) {
    LazyColumn {
        items(result.items) { documentSnapshot ->
            // parse the DocumentSnapshot to your custom class
            val snack = documentSnapshot.toObject<Snack>()!!

            // Pass the snack object to your Item Composable
            // ...
        }
    }
}
```

And that's it! It's that easy!

#### `CollectionState` lifecycle

##### Start/stop listening

The `CollectionState` uses a snapshot listener to monitor changes to the Firestore query.
To begin listening for data, call the `startListening()` method. You may want to call this
in your `onStart()` method. Make sure you have finished any authentication necessary to read the
data before calling `startListening()` or your query will fail.

```kotlin
val state = collectionStateOf(query)

override fun onStart() {
    super.onStart()
    state.startListening()
}
```

Similarly, the `stopListening()` call removes the snapshot listener.
Call this method when the containing Activity or Fragment stops:

```kotlin
override fun onStop() {
    super.onStop()
    state.stopListening()
}
```

##### Automatic listening

If you don't want to manually start/stop listening you can use
[Android Architecture Components][arch-components] to automatically manage the lifecycle of the
`CollectionState`. Pass a `LifecycleOwner` to it and Firebase Compose will automatically
start and stop listening in `onStart()` and `onStop()`.

#### Handling Error and Loading States

If you would like to handle all the possible states, you can use a `when` expression:

```kotlin
val (result) = remember { collectionStateOf(query, lifecycleOwner) }

when (result) {
    is FirestoreCollection.Snapshot -> {
        val items = result.items
        // call a Composable with this value
    }
    is FirestoreCollection.Error -> {
        val exception = result.exception
        // call a Composable to tell the user an error occurred
    }
    is FirestoreCollection.Loading -> {
        // call a Composable that shows a loading state
    }
}
```


### Using Pagination

Coming Soon.

## Using Firebase Compose to display a single Firestore Document

Now if you need to display a single `Snack` in your app, you can use `DocumentState`.

`DocumentState` binds a `DocumentReference` to a `FirestoreDocument` sealed class, which has 3 finite states:
- `FirestoreDocument.Snapshot` - contains `DocumentSnapshot` returned from that document reference.
- `FirestoreDocument.Error` - contains any exception thrown when reading the document reference.
- `FirestoreDocument.Loading` - indicates that no data or error was emitted from the document reference yet.

Its usage is similar to `CollectionState`. The main difference is that `DocumentState`
 returns a single `DocumentSnapshot` instead of `List<DocumentSnapshot>`:

```kotlin
// getting a reference to the snack with key/id 'snack1'
val documentRef = Firebase.firestore.collection("snacks").document("snack1")

val (result) = remember { documentStateOf(documentRef, lifecycleOwner) }

when (result) {
    is FirestoreDocument.Snapshot -> {
        val items = result.snapshot
        // call a Composable with this value
    }
    is FirestoreDocument.Error -> {
        val exception = result.exception
        // call a Composable to tell the user an error occurred
    }
    is FirestoreDocument.Loading -> {
        // call a Composable that shows a loading state
    }
}
```

-----

This README file was inspired by [FirebaseUI](https://github.com/firebase/FirebaseUI-Android/).

[firestore-docs]: https://firebase.google.com/docs/firestore/
[firestore-custom-objects]: https://firebase.google.com/docs/firestore/manage-data/add-data#custom_objects
[lazycolumn]: https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#lazycolumn
[arch-components]: https://developer.android.com/topic/libraries/architecture/index.html
[paging-support]: https://developer.android.com/topic/libraries/architecture/paging.html
