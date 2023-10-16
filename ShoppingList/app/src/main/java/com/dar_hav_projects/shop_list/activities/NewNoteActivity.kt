package com.dar_hav_projects.shop_list.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.dar_hav_projects.shop_list.entities.NoteItem
import com.dar_hav_projects.shop_list.fragments.NoteFragment
import com.dar_hav_projects.shop_list.utils.HtmlManager
import com.dar_hav_projects.shop_list.utils.MyTouchListener
import com.dar_hav_projects.shop_list.utils.TimeManager
import com.dar_hav_projects.shop_list.R
import com.dar_hav_projects.shop_list.databinding.ActivityNewNoteBinding
import java.util.*

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewNoteBinding
    private var note: NoteItem? = null
    private var pref: SharedPreferences? = null
    lateinit private var defPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.new_note)
        actionBarSettings()
        getNote()
        init()
        setTextSize()
        OnClickColorPicker()
        actionBarCallBack()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.ColorPicker.setOnTouchListener(MyTouchListener())
        pref = PreferenceManager.getDefaultSharedPreferences(this)
    }

    //ініціалізуємо прослуховувач на ColorPicker
    private fun OnClickColorPicker() = with(binding) {
        imBlack.setOnClickListener {
            setColorForSelectedText(R.color.picker_black)
        }
        imBlue.setOnClickListener {
            setColorForSelectedText(R.color.picker_blue)
        }
        imGreen.setOnClickListener {
            setColorForSelectedText(R.color.picker_green)
        }
        imRed.setOnClickListener {
            setColorForSelectedText(R.color.picker_red)
        }
        imOrange.setOnClickListener {
            setColorForSelectedText(R.color.picker_orange)
        }
        imYellow.setOnClickListener {
            setColorForSelectedText(R.color.picker_yellow)
        }
    }

    private fun getNote() {
        val sNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
        if (sNote != null) {
            note = sNote as NoteItem
            fillNote()
        }
    }

    private fun fillNote() = with(binding) {

        editTitle.setText(note?.title)
        edDescription.setText(HtmlManager.getFromHtml(note?.content!!).trim())

    }

    // при створенні меню ми заміняємо його на наше
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //функція setOnClickListener для меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.id_save) {
            setMainResult()
        } else if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.id_bold) {
            setBoldForSelectedText()
        } else if (item.itemId == R.id.id_color) {
            if (binding.ColorPicker.isShown) {
                closeColorPicker()
            } else {
                openColorPicker()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //встановлюємо вибраний колір
    private fun setColorForSelectedText(colorId: Int) = with(binding) {
        //визначаємо посаток і кінець виділеного тексту
        val startPosition = edDescription.selectionStart
        val endPosition = edDescription.selectionEnd

        //в перемінну  styles записуються стилі які використані в даному відрізку тексту
        val styles =
            edDescription.text.getSpans(startPosition, endPosition, ForegroundColorSpan::class.java)

        //якщо стиль є то ми його забираємо
        if (styles.isNotEmpty()) {
            edDescription.text.removeSpan(styles[0])
        }

        edDescription.text.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this@NewNoteActivity, colorId)),
            startPosition,
            endPosition,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //через те що в розмітці html у нас додадуться пробіли
        // і для того щоб їх видалити ми використовуємо функцію  .trim()
        edDescription.text.trim()
        edDescription.setSelection(startPosition)
    }

    private fun setBoldForSelectedText() = with(binding) {
        //визначаємо посаток і кінець виділеного тексту
        val startPosition = edDescription.selectionStart
        val endPosition = edDescription.selectionEnd

        //в перемінну  styles записуються стилі які використані в даному відрізку тексту
        val styles = edDescription.text.getSpans(startPosition, endPosition, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        //якщо стиль є то ми його забираємо
        if (styles.isNotEmpty()) {
            edDescription.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.ITALIC)
        }

        edDescription.text.setSpan(
            boldStyle,
            startPosition,
            endPosition,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //через те що в розмітці html у нас додадуться пробіли
        // і для того щоб їх видалити ми використовуємо функцію  .trim()
        edDescription.text.trim()
        edDescription.setSelection(startPosition)
    }

    private fun setMainResult() {
        var editState = "new"
        val tempNote: NoteItem? = if (note == null) {
            createNewNote()
        } else {
            editState = "update"
            updateNote()
        }
        val i = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, i)
        finish()

    }



    private fun updateNote(): NoteItem? = with(binding) {
        return note?.copy(
            title = editTitle.text.toString(),
            content = HtmlManager.toHTML(edDescription.text)
        )
    }

    //функція яка буде заповняти наш клас для передачі його
    private fun createNewNote(): NoteItem {
        return NoteItem(
            null,
            binding.editTitle.text.toString(),
            HtmlManager.toHTML(binding.edDescription.text),
            TimeManager.getCurrentTime(),
            ""
        )
    }



    //ми додаемо стрілку назад в actionBar
    private fun actionBarSettings() {
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openColorPicker() {
        binding.ColorPicker.visibility = View.VISIBLE
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.open_color_picker)
        binding.ColorPicker.startAnimation(openAnim)
    }

    private fun closeColorPicker() {
        binding.ColorPicker.visibility = View.VISIBLE
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.close_color_picker)
        openAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            //запускається після того як анімація закінчилась
            override fun onAnimationEnd(p0: Animation?) {
                binding.ColorPicker.visibility = View.GONE
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
        binding.ColorPicker.startAnimation(openAnim)
    }

    //call back який буде стирати меню action bar яке відривається коли виділяємо текст
    private fun actionBarCallBack(){
       val actionCallBack = object : ActionMode.Callback{
           //ця функція викликається при створенні контекстного меню
           override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
               p1?.clear()
               return true
           }
          //ця функція викликається перед показом контекстного меню
           override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
               p1?.clear()
               return true
           }

           override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
              return true
           }

           override fun onDestroyActionMode(p0: ActionMode?) {

           }
       }
        binding.edDescription.customSelectionActionModeCallback = actionCallBack
    }

    private fun setTextSize() = with(binding){
        editTitle.setTextSize(pref?.getString("title_size_key", "16"))
        edDescription.setTextSize(pref?.getString("content_size_key", "14"))
    }

    private fun EditText.setTextSize(size: String?){
        if(size!=null){
            this.textSize = size.toFloat()
        }
    }
    private fun getSelectedTheme():Int{
        return if(defPref.getString("theme_key", "Green") == "Green"){
            R.style.Theme_ShoppingListGreen
        } else{
            R.style.Theme_ShoppingListBlue
        }
    }

}
