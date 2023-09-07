// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import windows.TaskState
import windows.execute
import javax.swing.JFileChooser

@Composable
@Preview
fun App() {
    MaterialTheme { MainScreen() }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    //states
    val scrollState = rememberScrollState()

    var apkPathUri by remember { mutableStateOf("") }
    val checksumValue = remember { mutableStateOf("") }
    val taskState = remember { mutableStateOf("") }
    var isProgressShowing by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier.height(40.dp))

        WindowHeader()

        Spacer(modifier.height(40.dp))

        WindowItemBox(
            title = "Dpc Apk Information", description = "Required For Process", content = {
                InputDpcApkInfo(apkPath = apkPathUri, onPathClick = {
                    CoroutineScope(Dispatchers.Default).launch {
                        chooseFile()?.let { apkPathUri = it }
                    }
                })
            }, scrollState = scrollState, cardExpand = checksumValue.value.isEmpty()
        )

        WindowChecksumField(checksum = checksumValue.value)


        Spacer(modifier.height(25.dp))
        WindowCalculateButton(onClick = {
            CoroutineScope(Dispatchers.Default).launch {
                isProgressShowing = true
                execute(apkPathUri, taskState, checksumValue)
                isProgressShowing = false
            }

        })

    }

    Spacer(modifier.height(25.dp))

    WindowScrollbar(scrollState)

    if (isProgressShowing) {
        Box(modifier = modifier.fillMaxSize().background(color = Color.Black).alpha(0.9f), contentAlignment = Alignment.Center) {
            ProgressIndicator(taskState = taskState)
        }
    }
}

@Composable
fun ProgressIndicator(modifier: Modifier = Modifier, taskState: MutableState<String>) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1000, easing = FastOutLinearInEasing))
    )

    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painterResource("process.png"),
            contentDescription = null,
            modifier = modifier.height(90.dp).width(90.dp).rotate(rotation)
        )

        Spacer(modifier.height(10.dp))

        Text(
            modifier = modifier,
            text = taskState.value,
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun WindowHeader(modifier: Modifier = Modifier) {

    Image(
        painterResource("logo.png"), contentDescription = null, modifier = modifier.height(100.dp).width(100.dp)
    )

    Spacer(modifier.height(25.dp))

    Text(
        text = "Welcome To Checksum Calculator", fontWeight = FontWeight.Bold, fontSize = TextUnit(20f, TextUnitType.Sp)
    )
}

@Composable
private fun WindowScrollbar(scrollState: ScrollState) {
    VerticalScrollbar(
        modifier = Modifier.fillMaxHeight(), adapter = rememberScrollbarAdapter(
            scrollState
        ), style = ScrollbarStyle(
            minimalHeight = 16.dp,
            thickness = 10.dp,
            shape = RoundedCornerShape(6.dp),
            hoverDurationMillis = 300,
            unhoverColor = Color.Black.copy(alpha = 0.20f),
            hoverColor = Color.Black.copy(alpha = 0.50f)
        )
    )
}

@OptIn(ExperimentalUnitApi::class, ExperimentalMaterialApi::class)
@Composable
private fun WindowItemBox(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    content: @Composable () -> Unit,
    scrollState: ScrollState,
    cardExpand: Boolean = true
) {

    var isCardExpanded by remember { mutableStateOf(cardExpand) }


    val icon = if (isCardExpanded) Icons.Filled.KeyboardArrowUp
    else Icons.Filled.KeyboardArrowDown

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 40.dp),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(0.5.dp, Color.Gray),
        elevation = 2.dp,
        onClick = { isCardExpanded = !isCardExpanded }
    ) {
        Column(modifier = modifier.padding(18.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    modifier = modifier.fillMaxWidth().weight(8f),
                    text = title,
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Icon(
                    modifier = modifier.fillMaxWidth().weight(0.5f).padding(top = 8.dp),
                    imageVector = icon,
                    contentDescription = null
                )
            }
            Text(
                text = description,
                fontSize = TextUnit(12f, TextUnitType.Sp),
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )

            if (isCardExpanded) {

                Spacer(modifier.height(12.dp))

                content()

                LaunchedEffect(Unit) {
                    scrollState.animateScrollTo(scrollState.maxValue)
                }
            }
        }
    }
}

@Composable
private fun InputDpcApkInfo(
    modifier: Modifier = Modifier,
    apkPath: String,
    onPathClick: () -> Unit
) {
    OutlinedTextField(value = apkPath,
        enabled = false,
        onValueChange = {},
        readOnly = true,
        label = { Text("Dpc apk Location") },
        modifier = modifier.fillMaxWidth().clickable {
            onPathClick()
        })
}

@Composable
fun WindowChecksumField(
    modifier: Modifier = Modifier,
    checksum: String,
) {
    if (checksum.isNotEmpty()) {
        Spacer(modifier.height(25.dp))
        Row(modifier.fillMaxWidth().padding(horizontal = 40.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = checksum,
                onValueChange = {},
                modifier = modifier.fillMaxWidth(),
                label = { Text(text = "Checksum") }
            )

        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun WindowCalculateButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        border = BorderStroke(1.dp, color = Color.Black),
        shape = RoundedCornerShape(size = 10.dp),
        modifier = modifier.fillMaxWidth(0.3f).height(50.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        elevation = ButtonDefaults.elevation(0.dp)
    ) {
        Text(
            text = "Calculate",
            modifier.fillMaxSize().padding(top = 5.dp),
            fontSize = TextUnit(14f, TextUnitType.Sp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

fun chooseFile(): String? {
    val fileChooser = JFileChooser()
    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        val selectedFile = fileChooser.selectedFile
        return selectedFile.absolutePath
    }
    return null
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication, title = "Enterprise Checksum Calculator",
        icon = painterResource("logo.png")
    ) {
        App()
    }
}
