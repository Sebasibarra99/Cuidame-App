package com.sebastian.ecovibra1

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import android.content.pm.PackageManager
import com.google.firebase.auth.FirebaseAuth
import com.sebastian.ecovibra1.Fragmentos.FragmentBienvenidaCuidador
import com.sebastian.ecovibra1.Fragmentos.FragmentPerfil

class MainActivityCuidador : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_cuidador)

        // Permiso para vibración en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.VIBRATE), 1001)
            }
        }

        // Enlazar vistas
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)

        // Configurar Toolbar como ActionBar
        setSupportActionBar(toolbar)

        // Configurar toggle para abrir/cerrar el Drawer
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Asignar listener de ítems del menú
        navigationView.setNavigationItemSelectedListener(this)

        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragmento, FragmentBienvenidaCuidador())
            .commit()

        // Iniciar listener de alertas
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            AlertaListenerManager.iniciarListener(applicationContext, uid)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_agregar_medicamento -> {
                startActivity(Intent(this, AgregarMedicamentoActivity::class.java))
            }
            R.id.nav_reportes -> {
                startActivity(Intent(this, ReportesCuidadorActivity::class.java))
            }
            R.id.nav_ubicacion_usuario -> {
                startActivity(Intent(this, VerUbicacionActivity::class.java))
            }
            R.id.nav_alertas -> {
                startActivity(Intent(this, VerAlertasEmergenciaActivity::class.java))
            }
            R.id.nav_salir -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, OpcionesLoginActivity::class.java))
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
