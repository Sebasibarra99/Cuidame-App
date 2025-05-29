package  com.sebastian.ecovibra1.Fragmentos


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sebastian.ecovibra1.R

class FragmentChats : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("FragmentChats", "FragmentChats cargado correctamente") // Log para verificar
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }
}
