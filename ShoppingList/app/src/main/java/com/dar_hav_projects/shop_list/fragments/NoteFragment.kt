package com.dar_hav_projects.shop_list.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dar_hav_projects.shop_list.activities.MainApp
import com.dar_hav_projects.shop_list.activities.NewNoteActivity

import com.dar_hav_projects.shop_list.db.MainViewModel
import com.dar_hav_projects.shop_list.db.NoteAdapter
import com.dar_hav_projects.shop_list.entities.NoteItem
import com.dar_hav_projects.shop_list.entities.dialogs.DeleteDialog
import com.dar_hav_projects.shop_list.databinding.FragmentNoteBinding


class NoteFragment : BaseFragment(), NoteAdapter.Listener {
    private lateinit var binding: FragmentNoteBinding

    //потрібне для відкриття нового актівіті
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: NoteAdapter
    private lateinit var defPref: SharedPreferences

    //У нас MainApp на рівні всього застосунку тому ми його робим як контекст....? і витаскуєм базу данних
    // тут ми запускаємо ініціалізацію MainViewModel через MainViewModelFactory
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).dataBase)
    }

    //Запускаємо через лаунчер актівіті
    override fun onClickNew() {
        editLauncher.launch(Intent(activity, NewNoteActivity::class.java))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    //observer який буде слідкувати за змінами в баззі данних
    private fun observer() {
        mainViewModel.allNotes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.tvEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    //функція запускається коли всі View запущені
    // (тому що ми не можемо ініціалізцвати RecycleView поки view не ініціалізовані )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }

    //функція де ми будемо ініціалізувати RecyclerView i adapter
    private fun initRcView() = with(binding) {
        //layoutManager потрібен для того щоб правильно організуватм розміщення елементів на екрані
        defPref = PreferenceManager.getDefaultSharedPreferences(activity)
        rcViewNote.layoutManager = getLayoutManager()
        //ми ініціалізували адаптер NoteAdapter() це консруктор
        adapter = NoteAdapter(this@NoteFragment, defPref)
        // вказуємо адаптер який буде оновлювати наш rcViewNote
        rcViewNote.adapter = adapter
    }

    private fun getLayoutManager():RecyclerView.LayoutManager{
        return if(defPref.getString("note_style_format", "Linear") == "Linear"){
            LinearLayoutManager(activity)
        }else{
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }


    //кол бек для зчитування що пришло з NewNoteActivity
    private fun onEditResult() {
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val editState = it.data?.getStringExtra(EDIT_STATE_KEY)
                if (editState == "update") {
                    mainViewModel.updateNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem)
                } else {
                    mainViewModel.insertNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem)
                }

            }
        }
    }

    //потрібен для того щоб була тільки одна інстанція нашого об'єкта,
    // якщо ми намагаємось кілька разів її загрузити


    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener {
            override fun onClick() {
                mainViewModel.deleteNote(id)
            }
        })
    }

    override fun onClickItem(note: NoteItem) {
        val intent = Intent(activity, NewNoteActivity::class.java).apply {
            putExtra(NEW_NOTE_KEY, note)
        }
        editLauncher.launch(intent)
    }

    companion object {
        const val NEW_NOTE_KEY = "new_note_key"
        const val EDIT_STATE_KEY = "edit_state_key"

        @JvmStatic
        //якщо визиваємо newInstance то видає новий фрагмент,
        // але якщо я намагаюсь другий раз визвати, то буде видавати вже готову інстанцію
        fun newInstance() = NoteFragment()

    }


}