package com.example.bookcollection

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat.Style
import com.example.bookcollection.ui.theme.BookCollectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookCollectionTheme {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 15.dp)
                ) {
                    BookPage()
                }
            }
        }
    }
}

@Composable
fun BookPage(){
    Column(modifier = Modifier.padding(top=50.dp)) {
        UserBar()
        Spacer(modifier = Modifier.height(30.dp))
        BookList()
    }
}

@Composable
fun UserBar(){
    val profilePic= painterResource(id = R.drawable.profile)
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,

        ) {
        Column {
            Text(
                text = "Hi Fela",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "What are you reading today?",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light
                ),
                modifier = Modifier.padding(top= 5.dp)
            )
        }

        Column {
            Image(
                painter = profilePic,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun BookList(){
    LazyRow {
        item{
            Card(
                colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
                modifier = Modifier
                    .size(width = 100.dp, height = 130.dp),
                onClick = { }

            ) {
                Column(modifier = Modifier.align(Alignment.CenterHorizontally).padding(5.dp)){
                    Text(
                        text = "Percy Jackson",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rick Riordan",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                    )

                }
            }
        }
        item{
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .size(width = 120.dp, height = 130.dp)
                    .padding(start = 20.dp),
                onClick = { }

            ) {
                Column(modifier = Modifier.align(Alignment.CenterHorizontally).padding(5.dp)){
                    Text(
                        text = "Michael Vey",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Richard Paul Evans",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                    )

                }
            }
        }
        item{
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .size(width = 120.dp, height = 130.dp)
                    .padding(start = 20.dp),
                onClick = { }

            ) {
                Column(modifier = Modifier.align(Alignment.CenterHorizontally).padding(5.dp)){
                    Text(
                        text = "Verity",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Colleen Hoover",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                    )

                }
            }
        }
        item{
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .size(width = 120.dp, height = 130.dp)
                    .padding(start = 20.dp),
                onClick = { }

            ) {
                Column(modifier = Modifier.align(Alignment.CenterHorizontally).padding(5.dp)){
                    Text(
                        text = "Fourth Wing",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rebecca Yarros",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                    )

                }
            }
        }
        item{
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .size(width = 120.dp, height = 130.dp)
                    .padding(start = 20.dp),
                onClick = { }
            ) {
                Column(modifier = Modifier.align(Alignment.CenterHorizontally).padding(5.dp)){
                    Text(
                        text = "Artemis Fowl",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Eoin Colfer",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                    )

                }
            }
        }
        item{
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .size(width = 120.dp, height = 130.dp)
                    .padding(start = 20.dp),
                onClick = { }
            ) {
                Column(modifier = Modifier.align(Alignment.CenterHorizontally).padding(5.dp)){
                    Text(
                        text = "Steelheart",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Brandon Sanderson",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                    )

                }
            }
        }
    }

}

