package io.github.rosariopfernandes.firebasecompose.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.request.RequestOptions
import dev.chrisbanes.accompanist.glide.GlideImage
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.rosariopfernandes.firebasecompose.R
import io.github.rosariopfernandes.firebasecompose.model.Snack
import io.github.rosariopfernandes.firebasecompose.ui.theme.FirebaseComposeTheme
import io.github.rosariopfernandes.firebasecompose.ui.theme.purple200
import io.github.rosariopfernandes.firebasecompose.ui.theme.purple500
import io.github.rosariopfernandes.firebasecompose.ui.theme.purple700
import kotlin.math.max
import kotlin.math.min

private val BottomBarHeight = 56.dp
private val TitleHeight = 128.dp
private val GradientScroll = 180.dp
private val ImageOverlap = 115.dp
private val MinTitleOffset = 56.dp
private val MinImageOffset = 12.dp
private val MaxTitleOffset = ImageOverlap + MinTitleOffset + GradientScroll
private val ExpandedImageSize = 300.dp
private val CollapsedImageSize = 150.dp
private val HzPadding = Modifier.padding(horizontal = 24.dp)

class DetailActivity : AppCompatActivity() {
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseComposeTheme {
                ProvideWindowInsets {
                    SnackDetail(snackId = 1, upPress = { finish() })
                }
            }
        }
    }
}

@Composable
fun SnackDetail(
    snackId: Long,
    upPress: () -> Unit
) {
    val snack = remember(snackId) { getItems()[0] }

    Box(Modifier.fillMaxSize()) {
        val scroll = rememberScrollState(0f)
        Header()
        Body(scroll)
        Title(snack, scroll.value)
        Image(snack.imageUrl, scroll.value)
        Up(upPress)
    }
}

@Composable
private fun Header() {
    Spacer(
        modifier = Modifier
            .preferredHeight(280.dp)
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(purple200, purple500)))
    )
}

@Composable
private fun Up(upPress: () -> Unit) {
    IconButton(
        onClick = upPress,
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .preferredSize(36.dp)
            .background(
                color = purple200,
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            tint = Color.White,
            contentDescription = stringResource(R.string.label_back)
        )
    }
}

@Composable
private fun Body(
    scroll: ScrollState
) {
    Column {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .preferredHeight(MinTitleOffset)
        )
        Column(
            modifier = Modifier.verticalScroll(scroll)
        ) {
            Spacer(Modifier.preferredHeight(GradientScroll))
            Surface(Modifier.fillMaxWidth()) {
                Column {
                    Spacer(Modifier.preferredHeight(ImageOverlap))
                    Spacer(Modifier.preferredHeight(TitleHeight))

                    Spacer(Modifier.preferredHeight(16.dp))
                    Text(
                        text = stringResource(R.string.detail_header),
                        style = MaterialTheme.typography.overline,
                        color = Color.DarkGray,
                        modifier = HzPadding
                    )
                    Spacer(Modifier.preferredHeight(4.dp))
                    Text(
                        text = stringResource(R.string.detail_placeholder),
                        style = MaterialTheme.typography.body1,
                        color = Color.Gray,
                        modifier = HzPadding
                    )

                    Spacer(Modifier.preferredHeight(40.dp))
                    Text(
                        text = stringResource(R.string.ingredients),
                        style = MaterialTheme.typography.overline,
                        color = Color.DarkGray,
                        modifier = HzPadding
                    )
                    Spacer(Modifier.preferredHeight(4.dp))
                    Text(
                        text = stringResource(R.string.ingredients_list),
                        style = MaterialTheme.typography.body1,
                        color = Color.Gray,
                        modifier = HzPadding
                    )

                    Spacer(Modifier.preferredHeight(16.dp))
                    Divider( color = Color.LightGray, thickness = 1.dp, startIndent = 0.dp )

                    Spacer(
                        modifier = Modifier
                            .padding(bottom = BottomBarHeight)
                            .navigationBarsPadding(left = false, right = false)
                            .preferredHeight(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Title(snack: Snack, scroll: Float) {
    val maxOffset = with(LocalDensity.current) { MaxTitleOffset.toPx() }
    val minOffset = with(LocalDensity.current) { MinTitleOffset.toPx() }
    val offset = (maxOffset - scroll).coerceAtLeast(minOffset)
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .preferredHeightIn(min = TitleHeight)
            .statusBarsPadding()
            .graphicsLayer { translationY = offset }
            .background(color = Color.White)
    ) {
        Spacer(Modifier.preferredHeight(16.dp))
        Text(
            text = snack.name,
            style = MaterialTheme.typography.h4,
            color = purple500,
            modifier = HzPadding
        )
        Text(
            text = snack.tagline,
            style = MaterialTheme.typography.subtitle2,
            fontSize = 20.sp,
            color = Color.DarkGray,
            modifier = HzPadding
        )
        Spacer(Modifier.preferredHeight(4.dp))
        Text(
            text = "${snack.price} MZN",
            style = MaterialTheme.typography.h6,
            color = purple700,
            modifier = HzPadding
        )

        Spacer(Modifier.preferredHeight(8.dp))
        Divider( color = Color.LightGray, thickness = 1.dp, startIndent = 0.dp )
    }
}

@Composable
private fun Image(
    imageUrl: String,
    scroll: Float
) {
    val collapseRange = with(LocalDensity.current) { (MaxTitleOffset - MinTitleOffset).toPx() }
    val collapseFraction = (scroll / collapseRange).coerceIn(0f, 1f)

    CollapsingImageLayout(
        collapseFraction = collapseFraction,
        modifier = HzPadding.then(Modifier.statusBarsPadding())
    ) {
        GlideImage(
            data = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            requestBuilder = {
                val options = RequestOptions()
                options.circleCrop()
                apply(options)
            }
        )
    }
}

@Composable
private fun CollapsingImageLayout(
    collapseFraction: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        check(measurables.size == 1)

        val imageMaxSize = min(ExpandedImageSize.roundToPx(), constraints.maxWidth)
        val imageMinSize = max(CollapsedImageSize.roundToPx(), constraints.minWidth)
        val imageWidth = lerp(imageMaxSize, imageMinSize, collapseFraction)
        val imagePlaceable = measurables[0].measure(Constraints.fixed(imageWidth, imageWidth))

        val imageY = lerp(MinTitleOffset, MinImageOffset, collapseFraction).roundToPx()
        val imageX = lerp(
            (constraints.maxWidth - imageWidth) / 2, // centered when expanded
            constraints.maxWidth - imageWidth, // right aligned when collapsed
            collapseFraction
        )
        layout(
            width = constraints.maxWidth,
            height = imageY + imageWidth
        ) {
            imagePlaceable.place(imageX, imageY)
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
