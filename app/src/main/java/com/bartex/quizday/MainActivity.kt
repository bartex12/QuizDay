package com.bartex.quizday

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.network.NoInternetDialogFragment
import com.bartex.quizday.network.OnlineLiveData
import com.bartex.quizday.network.isInternetAvailable
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController:NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var audioManager: AudioManager

    private var isNetworkAvailable: Boolean = true //Доступна ли сеть

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //при первом включении проверяем наличие интернета вручную - без LiveData
        if (savedInstanceState == null) {
            isNetworkAvailable = isInternetAvailable(this)
            if (!isNetworkAvailable) {
                showNoInternetConnectionDialog()
            }
        }

        //следим за сетью через LiveData
        OnlineLiveData(this).observe(
            this@MainActivity,
            Observer<Boolean> {
                isNetworkAvailable = it
                if (!isNetworkAvailable) {
                    showNoInternetConnectionDialog()
                }
            })

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Задание значений по умолчанию для SharedPreferences
        PreferenceManager.setDefaultValues(this, R.xml.pref_setting, false)

        audioManager=  getSystemService(Context.AUDIO_SERVICE) as AudioManager

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        //читаем из графа конфигурацию - так работает правильно!!!
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph)
            //Отображать кнопку навигации как гамбургер , когда она не отображается как кнопка вверх
            .setOpenableLayout(drawerLayout)
            .build()
        //работа гамбургера и стрелки вверх в toolbar
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setNavigationItemSelectedListener(this)
    }

    //щелчки по стрелке вверх - как appBarConfiguration и по гамбургеру - как super.onSupportNavigateUp()
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showNoInternetConnectionDialog() {
        showAlertDialog(
                getString(R.string.dialog_title_device_is_offline),
                getString(R.string.dialog_message_device_is_offline)
        )
    }

    private fun showAlertDialog(title: String?, message: String?) {
        NoInternetDialogFragment.newInstance(title, message)
                .show(supportFragmentManager, Constants.DIALOG_FRAGMENT)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    //todo  изменять при добавлении фрагментов
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        //нашел способ установить видимость иконок в тулбаре без перебора всех вариантов
        val id = navController.currentDestination?.id
        //видимость иконок в тулбаре
        id?. let {
            menu?.findItem(R.id.action_settings)?.isVisible = it != R.id.settingsFragment
            menu?.findItem(R.id.action_help)?.isVisible = it!= R.id.helpFragment
            menu?.findItem(R.id.search)?.isVisible = it== R.id.regionFragment

            //заголовки тулбара в зависимости от фрагмента
            toolbar.title = when(it){
                R.id.homeFragment -> getString(R.string.app_name)
                R.id.textquizFragment -> getString(R.string.text_quiz)
                R.id.imagequizFragment -> getString(R.string.image_quiz)
                R.id.settingsFragment -> getString(R.string.action_settings)
                R.id.helpFragment -> getString(R.string.action_help)
                R.id.flagsFragment -> getString(R.string.flags)
                R.id.tabsFragment -> getString(R.string.flags)
                R.id.regionFragment -> getString(R.string.states)
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
            R.id.homeFragment -> {
                when (item.itemId) {
                    R.id.action_settings -> navController.navigate(R.id.action_homeFragment_to_settingsFragment)
                    R.id.action_help -> navController.navigate(R.id.action_homeFragment_to_helpFragment)
                }
            }

            R.id.textquizFragment -> {
                when (item.itemId) {
                    R.id.action_settings -> navController.navigate(R.id.action_textquizFragment_to_settingsFragment)
                    R.id.action_help -> navController.navigate(R.id.action_textquizFragment_to_helpFragment)
                }
            }

            R.id.imagequizFragment -> {
                when (item.itemId) {
                    R.id.action_help -> navController.navigate(R.id.action_imagequizFragment_to_helpFragment)
                    R.id.action_settings -> navController.navigate(R.id.action_imagequizFragment_to_settingsFragment)
                }
            }

            R.id.tabsFragment -> {
                when (item.itemId) {
                    R.id.action_help -> navController.navigate(R.id.action_tabsFragment_to_helpFragment)
                    R.id.action_settings -> navController.navigate(R.id.action_tabsFragment_to_settingsFragment)
                }
            }

            R.id.settingsFragment -> {
                when (item.itemId) {
                    R.id.action_help -> navController.navigate(R.id.action_settingsFragment_to_helpFragment)
                }
            }
            R.id.helpFragment -> {
                when (item.itemId) {
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
        item.isChecked = true
        drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    override fun onStop() {
        super.onStop()
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false)
    }

    fun getNetworkAvailable(): Boolean = isNetworkAvailable


}