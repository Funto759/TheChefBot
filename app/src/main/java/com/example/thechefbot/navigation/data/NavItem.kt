package com.example.thechefbot.navigation.data

import android.media.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable


data class NavItem(
    val name : String,
    val image : ImageVector,
    val imageSelected: ImageVector,
    val route : String
)

sealed class NavigationGuide(){

    @Serializable
    data object LoginScreen : NavigationGuide()
    @Serializable
    data object HomeScreen : NavigationGuide()
    @Serializable
    data object SignUpScreen : NavigationGuide()
    @Serializable
    data object BooksScreen : NavigationGuide()
    @Serializable
    data object ProfileScreen : NavigationGuide()
}





fun provideNavItems()= listOf(
        NavItem(
            name = "Home",
            image = Icons.Outlined.Home,
            imageSelected = Icons.Filled.Home,
            route = "home"
        ),
        NavItem(
            name = "Books",
            image = Icons.Outlined.Book,
            imageSelected = Icons.Filled.Book,
            route = "books"
        ),
        NavItem(
            name = "Profile",
            image = Icons.Outlined.Person,
            imageSelected = Icons.Filled.Person,
            route = "profile"
        )
    )

