package io.github.rosariopfernandes.firebasecompose.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dev.chrisbanes.accompanist.glide.GlideImage
import io.github.rosariopfernandes.firebasecompose.R
import io.github.rosariopfernandes.firebasecompose.firestore.FirestoreCollection
import io.github.rosariopfernandes.firebasecompose.firestore.collectionStateOf
import io.github.rosariopfernandes.firebasecompose.model.Snack
import io.github.rosariopfernandes.firebasecompose.ui.components.LoadingBar
import io.github.rosariopfernandes.firebasecompose.ui.components.OnlyText
import io.github.rosariopfernandes.firebasecompose.ui.theme.FirebaseComposeTheme

const val FIRESTORE_COLLECTION = "snacks"

class MainActivity : AppCompatActivity() {
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseComposeTheme {
                Scaffold(
                        topBar = { Toolbar(this@MainActivity) },
                        bodyContent = {
                            val query = Firebase.firestore.collection(FIRESTORE_COLLECTION)
                            val (result) = remember { collectionStateOf (query, this) }

                            when (result) {
                                is FirestoreCollection.Error -> {
                                    OnlyText(
                                            title = stringResource(R.string.title_error),
                                            message = result.exception.message ?: ""
                                    )
                                }
                                is FirestoreCollection.Loading -> {
                                    LoadingBar()
                                }
                                is FirestoreCollection.Snapshot -> {
                                    if (result.list.isEmpty()) {
                                        OnlyText(
                                                title = stringResource(id = R.string.title_empty),
                                                message = stringResource(id = R.string.message_empty)
                                        )
                                    } else {
                                        SnackList(context = this@MainActivity, items = result.list)
                                    }
                                }
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun Toolbar(context: Context) {
    TopAppBar(
            title = { Text(text = "FireSnacks") },
            actions = {
                Button(onClick = { addItems(context) }) {
                    Text(text = "Add Items")
                }
            }
    )
}

fun addItems(context: Context) {
    val firestore = Firebase.firestore
    val collection = firestore.collection(FIRESTORE_COLLECTION)
    firestore.runBatch { batch ->
        for (item in getItems()) {
            val docRef = collection.document()
            batch.set(docRef, item)
        }
    }.addOnSuccessListener {
        Toast.makeText(context, "Items added!", Toast.LENGTH_SHORT).show()
    }.addOnFailureListener { e ->
        Toast.makeText(context, "Error adding items: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

private fun getItems() = listOf(
    Snack(id = 1, name = "Cupcake", imageUrl = "https://upload.wikimedia.org/wikipedia/commons/5/56/Chocolate_cupcakes.jpg"),
    Snack(id = 2, name = "Donut", imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Glazed-Donut.jpg/640px-Glazed-Donut.jpg"),
    Snack(id = 3, name = "Eclair", imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ginger_Spice_Eclair_-_39843168741.jpg/640px-Ginger_Spice_Eclair_-_39843168741.jpg"),
    Snack(id = 4, name = "Froyo", imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/06/ICE_CREAM_%28FROYO%29_01.jpg/640px-ICE_CREAM_%28FROYO%29_01.jpg"),
    Snack(id = 5, name = "Gingerbread", imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/49/Gingerbread_men.jpg/401px-Gingerbread_men.jpg"),
    Snack(id = 6, name = "Honey", imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Bienenwabe_mit_Eiern_und_Brut_5.jpg/640px-Bienenwabe_mit_Eiern_und_Brut_5.jpg"),
    Snack(id = 7, name = "Ice Cream", imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/35/Bananas_Foster_Ice_Cream_at_Little_Giant_Ice_Cream.jpg/480px-Bananas_Foster_Ice_Cream_at_Little_Giant_Ice_Cream.jpg"),
    Snack(id = 8, name = "Jelly Beans", imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/JellyBellyBeans.jpg/616px-JellyBellyBeans.jpg")
)

@ExperimentalFoundationApi
@Composable
fun SnackList(
    context: Context,
    items: List<DocumentSnapshot>
) {
    LazyVerticalGrid(cells = GridCells.Fixed(2), content = {
        items(items) { snapshot ->
            val snack = snapshot.toObject<Snack>()!!
            SnackItem(snack = snack, onSnackClick = {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("snackId", snapshot.id)
                context.startActivity(intent)
            })
        }
    })
}

@Composable
fun SnackItem(
        snack: Snack,
        onSnackClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    Card(
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
    ) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                        .clickable(onClick = { onSnackClick() })
                        .padding(8.dp)
        ) {
            GlideImage(
                    data = snack.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.preferredSize(120.dp),
                    requestBuilder = {
                        val options = RequestOptions()
                        options.circleCrop()
                        apply(options)
                    }
            )
            Text(
                    text = snack.name,
                    style = MaterialTheme.typography.subtitle1,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}