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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dev.chrisbanes.accompanist.glide.GlideImage
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.rosariopfernandes.firebasecompose.R
import io.github.rosariopfernandes.firebasecompose.firestore.FirestoreDocument
import io.github.rosariopfernandes.firebasecompose.firestore.documentStateOf
import io.github.rosariopfernandes.firebasecompose.model.Snack
import io.github.rosariopfernandes.firebasecompose.ui.components.LoadingBar
import io.github.rosariopfernandes.firebasecompose.ui.components.OnlyText
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

        val snackId = intent.extras!!.getString("snackId")!!
        setContent {
            FirebaseComposeTheme {
                ProvideWindowInsets {
                    val docRef = Firebase.firestore.collection("snacks").document(snackId)
                    val (result) = remember { documentStateOf(docRef, this@DetailActivity) }
                    when (result) {
                        is FirestoreDocument.Loading -> {
                            LoadingBar()
                        }
                        is FirestoreDocument.Error -> {
                            val exception = result.exception
                            OnlyText(title = stringResource(id = R.string.title_error),
                                    message = exception.message ?: "")
                        }
                        is FirestoreDocument.Snapshot -> {
                            val snack = result.snapshot.toObject<Snack>()!!
                            SnackDetail(snack = snack, upPress = { finish() })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SnackDetail(
    snack: Snack,
    upPress: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        val scroll = rememberScrollState(0)
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
                .height(280.dp)
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
                .size(36.dp)
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
                    .height(MinTitleOffset)
        )
        Column(
            modifier = Modifier.verticalScroll(scroll)
        ) {
            Spacer(Modifier.height(GradientScroll))
            Surface(Modifier.fillMaxWidth()) {
                Column {
                    Spacer(Modifier.height(ImageOverlap))
                    Spacer(Modifier.height(TitleHeight))

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.detail_header),
                        style = MaterialTheme.typography.overline,
                        color = Color.DarkGray,
                        modifier = HzPadding
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.detail_placeholder),
                        style = MaterialTheme.typography.body1,
                        color = Color.Gray,
                        modifier = HzPadding
                    )

                    Spacer(Modifier.height(40.dp))
                    Text(
                        text = stringResource(R.string.ingredients),
                        style = MaterialTheme.typography.overline,
                        color = Color.DarkGray,
                        modifier = HzPadding
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.ingredients_list),
                        style = MaterialTheme.typography.body1,
                        color = Color.Gray,
                        modifier = HzPadding
                    )

                    Spacer(Modifier.height(16.dp))
                    Divider( color = Color.LightGray, thickness = 1.dp, startIndent = 0.dp )

                    Spacer(
                        modifier = Modifier
                                .padding(bottom = BottomBarHeight)
                                .navigationBarsPadding(left = false, right = false)
                                .height(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Title(snack: Snack, scroll: Int) {
    val maxOffset = with(LocalDensity.current) { MaxTitleOffset.toPx() }
    val minOffset = with(LocalDensity.current) { MinTitleOffset.toPx() }
    val offset = (maxOffset - scroll).coerceAtLeast(minOffset)
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
                .heightIn(min = TitleHeight)
                .statusBarsPadding()
                .graphicsLayer { translationY = offset }
                .background(color = Color.White)
    ) {
        Spacer(Modifier.height(16.dp))
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
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${snack.price} MZN",
            style = MaterialTheme.typography.h6,
            color = purple700,
            modifier = HzPadding
        )

        Spacer(Modifier.height(8.dp))
        Divider( color = Color.LightGray, thickness = 1.dp, startIndent = 0.dp )
    }
}

@Composable
private fun Image(
    imageUrl: String,
    scroll: Int
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
