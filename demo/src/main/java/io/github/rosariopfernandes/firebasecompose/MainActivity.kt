package io.github.rosariopfernandes.firebasecompose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.request.RequestOptions
import dev.chrisbanes.accompanist.glide.GlideImage
import io.github.rosariopfernandes.firebasecompose.model.Snack
import io.github.rosariopfernandes.firebasecompose.ui.theme.FirebaseComposeTheme

class MainActivity : AppCompatActivity() {
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseComposeTheme {
                Scaffold(
                        topBar = { Toolbar() },
                        bodyContent = { SnackList() }
                )
            }
        }
    }
}

@Composable
fun Toolbar() {
    TopAppBar(
            title = { Text(text = "FireSnacks") },
            actions = {
                Button(onClick = { /*TODO: add items to Firestore*/ }) {
                    Text(text = "Add Items")
                }
            }
    )
}

@ExperimentalFoundationApi
@Composable
fun SnackList(

) {
    LazyVerticalGrid(cells = GridCells.Fixed(2), content = {
        items(getItems()) { snack ->
            SnackItem(snack = snack, onSnackClick = {
                // TODO
            })
        }
    })
}

@Composable
fun SnackItem(
        snack: Snack,
        onSnackClick: (Long) -> Unit,
        modifier: Modifier = Modifier
) {
    Card(
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.padding(
                    start = 4.dp,
                    end = 4.dp,
                    bottom = 8.dp
            )
    ) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                        .clickable(onClick = { onSnackClick(snack.id) })
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FirebaseComposeTheme {
        SnackItem(snack = Snack(), onSnackClick = { /*TODO*/ })
    }
}