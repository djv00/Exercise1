package com.example.exercise1 // don't forget to keep your own package name

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.exercise1.ui.theme.Exercise1Theme
import kotlinx.coroutines.launch

data class Shoe(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val imageRes: Int,
    val selectedSize: Int = 42
)

// dummy data for testing
val dummyShoes = listOf(
    Shoe(1, "Nike Air Max 90", 137.50, "New Release", R.drawable.shoe_1),
    Shoe(2, "Creter Impact", 99.50, "Men's Shoes", R.drawable.shoe_2),
    Shoe(3, "Air Max Pre-Day", 145.00, "Men's Shoes", R.drawable.shoe_3),
    Shoe(4, "ZoomX Invincible", 180.00, "Running", R.drawable.shoe_1)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Exercise1Theme {
                ShoeShopApp()
            }
        }
    }
}

@Composable
fun ShoeShopApp() {
    val navController = rememberNavController()
    val cartItems = remember { mutableStateListOf<Shoe>() }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                cartItems = cartItems,
                onAddToCart = { cartItems.add(it) },
                onNavigateToCart = { navController.navigate("cart") },
                onNavigateToDetail = { shoeId -> navController.navigate("detail/$shoeId") }
            )
        }
        composable("cart") {
            CartScreen(
                cartItems = cartItems,
                onBack = { navController.popBackStack() },
                onRemoveItem = { cartItems.remove(it) }
            )
        }
        composable("detail/{shoeId}") { backStackEntry ->
            val shoeId = backStackEntry.arguments?.getString("shoeId")?.toIntOrNull()
            val shoe = dummyShoes.find { it.id == shoeId }
            if (shoe != null) {
                ShoeDetailScreen(
                    shoe = shoe,
                    onBack = { navController.popBackStack() },
                    onAddToCart = {
                        cartItems.add(it)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    cartItems: List<Shoe>,
    onAddToCart: (Shoe) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("All", "New Release", "Men's Shoes", "Running")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    // filter logic
    val filteredShoes = dummyShoes.filter { shoe ->
        val matchCategory = selectedCategory == "All" || shoe.category == selectedCategory
        val matchSearch = searchQuery.isBlank() || shoe.name.contains(searchQuery, ignoreCase = true)
        matchCategory && matchSearch
    }

    val totalPrice = cartItems.sumOf { it.price }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(250.dp)) {
                Spacer(Modifier.height(32.dp))
                Text("Menu", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                Divider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    label = { Text("My Bag (${cartItems.size})") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToCart()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    containerColor = Color(0xFF1E1E1E),
                    contentColor = Color.White,
                    modifier = Modifier.clickable { onNavigateToCart() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${cartItems.size} Items")
                        }
                        Text(
                            text = "Total: $${String.format("%.2f", totalPrice)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                if (isSearchActive) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        placeholder = { Text("Search shoes...") },
                        trailingIcon = {
                            IconButton(onClick = {
                                isSearchActive = false
                                searchQuery = ""
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Close Search")
                            }
                        },
                        singleLine = true
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", modifier = Modifier.size(28.dp))
                        }
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(28.dp))
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("New Release", color = Color.White, fontSize = 12.sp)
                            Text("Nike Air Max 90", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onNavigateToDetail(1) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("Shop Now", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = (category == selectedCategory),
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFF5722),
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(50)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("New Men's", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(filteredShoes) { shoe ->
                        ShoeCard(
                            shoe = shoe,
                            onAddToCart = onAddToCart,
                            onCardClick = { onNavigateToDetail(shoe.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoeDetailScreen(
    shoe: Shoe,
    onBack: () -> Unit,
    onAddToCart: (Shoe) -> Unit
) {
    val availableSizes = listOf(40, 41, 42, 43, 44, 45)
    var selectedSize by remember { mutableStateOf(42) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(shoe.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color(0xFF1E1E1E), contentColor = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "$${shoe.price}", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Button(
                        onClick = { onAddToCart(shoe.copy(selectedSize = selectedSize)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
                    ) {
                        Text("Add to Bag", fontSize = 16.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color.White)) {
            Image(
                painter = painterResource(id = shoe.imageRes),
                contentDescription = shoe.name,
                modifier = Modifier.fillMaxWidth().height(250.dp).padding(16.dp)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = shoe.category, color = Color.Red)
                Text(text = shoe.name, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Select Size (EU)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availableSizes) { size ->
                        FilterChip(
                            selected = (size == selectedSize),
                            onClick = { selectedSize = size },
                            label = { Text(size.toString()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFF5722),
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(cartItems: List<Shoe>, onBack: () -> Unit, onRemoveItem: (Shoe) -> Unit) {
    val totalPrice = cartItems.sumOf { it.price }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bag", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total: $${String.format("%.2f", totalPrice)}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))) {
                        Text("Checkout")
                    }
                }
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Your bag is empty.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(cartItems) { shoe ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(id = shoe.imageRes), contentDescription = null, modifier = Modifier.size(60.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(shoe.name, fontWeight = FontWeight.Bold)
                                Text("Size: ${shoe.selectedSize}", fontSize = 12.sp, color = Color.Gray)
                                Text("$${shoe.price}", color = Color.Gray)
                            }
                            IconButton(onClick = { onRemoveItem(shoe) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShoeCard(shoe: Shoe, onAddToCart: (Shoe) -> Unit, onCardClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = painterResource(id = shoe.imageRes),
                contentDescription = shoe.name,
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = shoe.category, color = Color.Red, fontSize = 12.sp)
            Text(text = shoe.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$${shoe.price}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                IconButton(
                    onClick = { onAddToCart(shoe) },
                    modifier = Modifier.size(32.dp).background(Color.Black, shape = RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                }
            }
        }
    }
}