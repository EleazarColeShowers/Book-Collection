package com.example.bookcollection

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
//import androidx.compose.ui.node.CanFocusChecker.end
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
        composable("EditBook/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull()
            if (bookId != null) {
                EditBookPage(
                    bookDao = bookDao,
                    bookId = bookId,
                    onBookSaved = { navController.navigate("home") }
                )
            }
        }
    }
}



@Composable
fun BookPage(navController: NavController, bookDao: BookDao){
    val searchQuery = remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(top = 30.dp)
        ) {
            SearchBar(
                hint = "Search books...",
                onTextChange = { query ->
                    searchQuery.value = query
                },
                onSearchClicked = {
                    // Perform search action
                    println("Search for: ${searchQuery.value}")
                }
                )
            UserBar()
            Spacer(modifier = Modifier.height(30.dp))
            BookList(bookDao = bookDao, navController, searchQuery.value)
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
fun BookList(
    bookDao: BookDao,
    navController: NavController,
    searchQuery: String

){
    val books by bookDao.getAllBooks().collectAsState(initial = emptyList())
    var showPopup by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }

    val filteredBooks = if (searchQuery.isNotBlank()) {
        books.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.author.contains(searchQuery, ignoreCase = true)
        }
    } else {
        books
    }


    LazyRow {
        items(filteredBooks){book ->
            Card(
                colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .size(width = 110.dp, height = 130.dp)
                    .padding(end = 10.dp),
                onClick = {
                    selectedBook = book
                    showPopup = true
                }

            ) {
                Column(modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)){
                    Text(
                        text = book.title,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.author,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        ),

                    )

                }
            }
        }
    }
    if (showPopup && selectedBook != null) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            title = { Text(text = "Manage Book") },
            text = {
                Column {
                    Text(text = "Would you like to edit or  delete ${selectedBook?.title} by ${selectedBook?.author}")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPopup = false
                        CoroutineScope(Dispatchers.Main).launch{
                            bookDao.deleteBook(selectedBook!!)
                        }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPopup = false
                        navController.navigate("EditBook/${selectedBook?.id}")
                    }
                ) {
                    Text("Edit")
                }
            }

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
    bookId: Long? = null,
    onBookSaved:()-> Unit){
    val context= LocalContext.current
    var authorName by remember { mutableStateOf("") }
    var bookTitle by remember { mutableStateOf("") }
    var genre by remember{ mutableStateOf("") }

    if (bookId != null) {
        // Fetch the book by its ID and prefill the fields
        val bookToEdit = bookDao.getBookById(bookId).collectAsState(initial = null).value
        LaunchedEffect(bookToEdit) {
            if (bookToEdit != null) {
                authorName = bookToEdit.author
                bookTitle = bookToEdit.title
                genre = bookToEdit.genre
            }
        }
    }


    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top) // Space items and center them vertically
    ){
        Spacer(modifier = Modifier.padding(top= 50.dp))
        OutlinedTextField(
            value = bookTitle,
            onValueChange = { bookTitle = it },
            label = { Text("Book Title") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        )

        OutlinedTextField(
            value = authorName,
            onValueChange = { authorName = it },
            label = { Text("Author") },
            modifier = Modifier
                .fillMaxWidth(0.9f) // Increase width to 90% of the parent width
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

@Composable
fun EditBookPage(
    bookDao: BookDao,
    bookId: Long,
    onBookSaved: () -> Unit
) {
    val context = LocalContext.current
    var authorName by remember { mutableStateOf("") }
    var bookTitle by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }

    // Load the book details when the page is first composed
    LaunchedEffect(bookId) {
        bookDao.getBookById(bookId).collect { bookToEdit ->
            authorName = bookToEdit.author
            bookTitle = bookToEdit.title
            genre = bookToEdit.genre
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top)
    ) {
        Spacer(modifier = Modifier.padding(top = 50.dp))

        OutlinedTextField(
            value = bookTitle,
            onValueChange = { bookTitle = it },
            label = { Text("Book Title") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        )


        OutlinedTextField(
            value = authorName,
            onValueChange = { authorName = it },
            label = { Text("Author") },
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
                CoroutineScope(Dispatchers.Main).launch {
                    val updatedBook = BookEntity(
                        id = bookId.toInt(),
                        author = authorName,
                        title = bookTitle,
                        genre = genre
                    )
                    bookDao.updateBook(updatedBook)
                    onBookSaved()
                }
            },
            icon = { Icon(Icons.Filled.Edit, "Extended floating action button.") },
            text = { Text(text = "Save") },
        )
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "Search...",
    onTextChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    textState: MutableState<String> = remember { mutableStateOf("") }
) {
    val text = textState.value

    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = text,
            onValueChange = {
                textState.value = it
                onTextChange(it)
            },
            modifier = Modifier
                .weight(1f)
                .padding(end= 8.dp),
        singleLine = true,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        ),
        decorationBox = { innerTextField ->
            if (text.isEmpty()) {
                Text(
                    text = hint,
                    color = Color.Gray.copy(alpha = 0.5f),
                    fontSize = 16.sp
                )
            }
            innerTextField()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchClicked()
            }
        )
        )

        IconButton(onClick = { onSearchClicked() }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


