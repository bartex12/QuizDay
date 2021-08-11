package com.bartex.quizday

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.NavController

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController:NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.visibility = View.GONE //скроем пока
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        navController = findNavController(R.id.nav_host_fragment)
        //читаем из графа конфигурацию - так работает правильно!!!
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph)
            //Отображать кнопку навигации как гамбургер , когда она не отображается как кнопка вверх
            .setOpenableLayout(drawerLayout)
            .build()
        // если делать так, то стрелка вверх не отобржается, кроме того в back stak скапливаются
        // все фрагменты, к которым обращались !!!
        //        appBarConfiguration =   AppBarConfiguration(setOf(
        //                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        //работа гамбургера и стрелки вверх в toolbar
        setupActionBarWithNavController(navController, appBarConfiguration)
        //работа меню шторки без внешних слушателей - если все пункты меню могут через navigate
       // navView.setupWithNavController(navController)
        //слушатель меню шторки -для обработки пунктов шторки - если нужно как-то хитро делать переходы
        //то есть если бы все пункты были через navigate, то было бы не нужно, справлялся бы NavController
         navView.setNavigationItemSelectedListener(this)
    }

    //щелчки по стрелке вверх - как appBarConfiguration и по гамбургеру - как super.onSupportNavigateUp()
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        //нашел способ установить видимость иконок в тулбаре без перебора всех вариантов
        val id = navController.currentDestination?.id
        //видимость иконок в тулбаре
        id?. let {
            menu?.findItem(R.id.action_settings)?.isVisible = it != R.id.settingsFragment
            menu?.findItem(R.id.action_help)?.isVisible = it!= R.id.helpFragment

            //заголовки тулбара в зависимости от фрагмента
            toolbar.title = when(it){
                R.id.homeFragment -> getString(R.string.app_name)
                R.id.textquizFragment -> getString(R.string.text_quiz)
                R.id.imagequizFragment -> getString(R.string.image_quiz)
                R.id.settingsFragment -> getString(R.string.action_settings)
                R.id.helpFragment -> getString(R.string.action_help)
                else -> getString(R.string.app_name)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    //так  как справка - настройки могут бесконечно вызываться друг из друга
    //чтобы это предотвратить, в их action было задействовано popUpTo и popUpToInclusive
    //поэтому здесь использовано navigate to action в соответствии с mobile_navigation
    //кроме того пришлось делать перебор всех вариантов (может лучше  убрать меню во фрагменты)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (navController.currentDestination?.id ){
            R.id.homeFragment ->{
                when(item.itemId){
                    R.id.action_settings -> navController.navigate(R.id.action_homeFragment_to_settingsFragment)
                    R.id.action_help -> navController.navigate(R.id.action_homeFragment_to_helpFragment)
                }
            }

            R.id.textquizFragment ->{
                when(item.itemId){
                    R.id.action_settings -> navController.navigate(R.id.action_textquizFragment_to_settingsFragment)
                    R.id.action_help -> navController.navigate(R.id.action_textquizFragment_to_helpFragment)
                }
            }

            R.id.imagequizFragment ->{
                when(item.itemId){
                    R.id.action_help -> navController.navigate(R.id.action_imagequizFragment_to_helpFragment)
                    R.id.action_settings -> navController.navigate(R.id.action_imagequizFragment_to_settingsFragment)
                }
            }
           R.id.settingsFragment ->{
               when(item.itemId){
                   R.id.action_help -> navController.navigate(R.id.action_settingsFragment_to_helpFragment)
               }
            }
            R.id.helpFragment ->{
                when(item.itemId){
                    R.id.action_settings -> navController.navigate(R.id.action_helpFragment_to_settingsFragment)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //меню шторки
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.textquizFragment -> {
                navController.navigate(R.id.textquizFragment)
            }
            R.id.imagequizFragment -> {
                navController.navigate(R.id.imagequizFragment)
            }
            R.id.nav_settings -> {
                navController.navigate(R.id.settingsFragment)
            }
            R.id.nav_help -> {
                navController.navigate(R.id.helpFragment)
            }
            R.id.nav_share -> {
                //поделиться - передаём ссылку на приложение в маркете
                //shareApp()
                Toast.makeText(this, "share", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_rate -> {
                //оценить приложение - попадаем на страницу приложения в маркете
                //rateApp()
                Toast.makeText(this, "rate", Toast.LENGTH_SHORT).show()
            }
        }
        // Выделяем выбранный пункт меню в шторке
        //item.isChecked = true
        drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }
}