package com.example.bookcollection

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookcollection.data.BookDao
import com.example.bookcollection.data.BookDatabase
import com.example.bookcollection.data.BookEntity
import com.example.bookcollection.ui.theme.BookCollectionTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookCollectionTheme {
                Column(
                    modifier = Modifier
//                        .verticalScroll(rememberScrollState())
                        .padding(15.dp)
                ) {
                    val context= LocalContext.current
                    BookApp(context)

                }
            }
        }
    }
}

@Composable
fun BookApp(context: Context){
    val navController = rememberNavController()
    val bookDao= remember{
        BookDatabase.getDatabase(context).bookdao()
    }
    NavHost(navController = navController, startDestination = "home" ){
        composable("home"){ BookPage(navController, bookDao)}
        composable("AddBook"){ AddBookPage(
            bookDao= bookDao,
            onBookSaved = {
                navController.navigate("home")
            }
        )}
    }
}

@Composable
fun BookPage(navController: NavController, bookDao: BookDao){
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(top = 30.dp)
        ) {
            UserBar()
            Spacer(modifier = Modifier.height(30.dp))
            BookList(bookDao = bookDao)
            Spacer(modifier = Modifier.weight(1f))
        }
        AddBook(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            navController

        )
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
fun BookList(bookDao: BookDao){
    val books by bookDao.getAllBooks().collectAsState(initial = emptyList())
    var showPopup by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }


    LazyRow {
        items(books){books ->
            Card(
                colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
                modifier = Modifier
                    .size(width = 110.dp, height = 130.dp)
                    .padding(end = 10.dp),
                onClick = {
                    selectedBook = books
                    showPopup = true
                }

            ) {
                Column(modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)){
                    Text(
                        text = books.title,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = books.author,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                    )

                }
            }
        }
    }
    if (showPopup && selectedBook != null) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            title = { Text(text = selectedBook?.title ?: "Unknown Title") },
            text = {
                Column {
                    Text(text = "Author: ${selectedBook?.author ?: "Unknown Author"}")
                    Text(text = "Genre: ${selectedBook?.genre ?: "Unknown Genre"}")
                }
            },
            confirmButton = {
                TextButton(onClick = { showPopup = false }) {
                    Text("OK")
                }
            },
        )
    }
}

@Composable
fun AddBook(
    modifier: Modifier= Modifier,
    navController: NavController
){
    FloatingActionButton(
        onClick = { navController.navigate("AddBook")},
        shape = CircleShape,
        modifier = modifier
    ) {
        Icon(Icons.Filled.Add, "Floating action button.")
    }
}

@Composable
fun AddBookPage(
    bookDao: BookDao,
    onBookSaved:()-> Unit){
    var authorName by remember { mutableStateOf("") }
    var bookTitle by remember { mutableStateOf("") }
    var genre by remember{ mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top) // Space items and center them vertically
    ){
        Spacer(modifier = Modifier.padding(top= 50.dp))
        OutlinedTextField(
            value = authorName,
            onValueChange = { authorName = it },
            label = { Text("Author") },
            modifier = Modifier
                .fillMaxWidth(0.9f) // Increase width to 90% of the parent width
        )

        OutlinedTextField(
            value = bookTitle,
            onValueChange = { bookTitle = it },
            label = { Text("Book Title") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        )

        OutlinedTextField(
            value = genre,
            onValueChange = { genre = it },
            label = { Text("Genre") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        )
        
        Spacer(modifier = Modifier)

        ExtendedFloatingActionButton(
            onClick = {
                val newBook = BookEntity(
                    author= authorName,
                    title = bookTitle,
                    genre = genre
                )
                CoroutineScope(Dispatchers.Main).launch {
                    bookDao.insertBook(newBook)
                    // Navigate back or perform an action after saving
                    onBookSaved()
                }
            },
            icon = { Icon(Icons.Filled.Edit, "Extended floating action button.") },
            text = { Text(text = "Save") },
        )
    }

}



