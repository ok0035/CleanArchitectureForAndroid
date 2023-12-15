package com.zerodeg.cleanarchitecture.views

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.zerodeg.feature_main.databinding.ActivityMainBinding
import com.zerodeg.feature_main.ui.image.BubbleViewPager
import com.zerodeg.feature_main.ui.image.PagerExample
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PagerExample(itemList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
//            DraggableImageExample()

        }

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val navView: BottomNavigationView = binding.navView
//
//        val navController = findNavController(id.nav_host_fragment_activity_main)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//            id.navigation_home,
//            id.navigation_dashboard,
//            id.navigation_notifications)
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
    }
}