package com.dar_hav_projects.shop_list.db

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.dar_hav_projects.shop_list.R
import com.dar_hav_projects.shop_list.databinding.NoteListItemBinding
import com.dar_hav_projects.shop_list.entities.NoteItem
import com.dar_hav_projects.shop_list.utils.HtmlManager
import com.dar_hav_projects.shop_list.utils.TimeManager


//Бере з списку значення елементів, потім бере розмітку і заповнює її значенням, потім друкує її в RecycleView
class NoteAdapter( private val listener: Listener, private val defPres: SharedPreferences) :
    ListAdapter<NoteItem, NoteAdapter.ItemHolder>(ItemComparator()) {
   lateinit var context: Context

    //вона відповідає за створення і повернення нового екземпляру ViewHolder для відображення даних елементу списку.
    //функцяя створює для кожної замітки свій ItemHolder, який буде сворювати розмітку
    // і також це клас який зберігає в свобі розмітку
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    //Функція для заповнення розмітки
    //нам приходить holder, і сама позиція position
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        //визиваємо функцію
        holder.setData(getItem(position), listener, defPres)
    }


    //Цей клас сберігає ссилки на візуальні елементи для того щоб оптимізувати роботу самого  RecycleView
    class ItemHolder(view: View , private val context: Context) : RecyclerView.ViewHolder(view) {
        private val binding = NoteListItemBinding.bind(view)

        //функція яка заповнює розмітку
        fun setData(note: NoteItem, listener: Listener, defPres: SharedPreferences) = with(binding) {
            tvTitle.text = note.title
            tvDescription.text = HtmlManager.getFromHtml(note.content).trim()
            tvTime.text = TimeManager.getTimeFormat(note.time, defPres)
            if(defPres.getString("theme_key", "Green") == "Blue"){
                Line.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
            }
            if(note.content.isEmpty()){
                tvDescription.visibility = View.GONE
            }
            itemView.setOnClickListener {
                listener.onClickItem(note)
            }
            imDelete.setOnClickListener {
                listener.deleteItem(note.id!!)
            }
        }

        //потрібна для того щоб ініціалізувати наш клас NoteHolder
        companion object {
            fun create(parent: ViewGroup): ItemHolder {
                val context = parent.context
                val view =  LayoutInflater.from(parent.context).inflate(R.layout.note_list_item, parent, false)
                //загружаєм в память розмітку і повераєм
                return ItemHolder(view, context)
            }
        }
    }

    //відповідає за те щоб порівнювати між собою елементи з старого списку
    //і нового, щоб самомму не займатись оновленням списку
    class ItemComparator : DiffUtil.ItemCallback<NoteItem>() {
        //порівнює елементи
        override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
            //ми порівнюємо по індетифікатору, бо вoни в нас індивідуальні
            return oldItem.id == newItem.id
        }

        //порівнює весь контент
        override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
            return oldItem == newItem
        }
    }


    interface Listener {
        fun deleteItem(id: Int)
        fun onClickItem(note: NoteItem)
    }

}