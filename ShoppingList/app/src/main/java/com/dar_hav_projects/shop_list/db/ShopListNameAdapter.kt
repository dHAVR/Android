package com.dar_hav_projects.shop_list.db

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.dar_hav_projects.shop_list.R
import com.dar_hav_projects.shop_list.databinding.ListNameItemBinding
import com.dar_hav_projects.shop_list.entities.ShopListNameItem

//Бере з списку значення елементів, потім бере розмітку і заповнює її значенням, потім друкує її в RecycleView
class ShopListNameAdapter(private val listener: Listener, private val defPres: SharedPreferences) :
    ListAdapter<ShopListNameItem, ShopListNameAdapter.ItemHolder>(ItemComparator()) {
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
    class ItemHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view) {
        private val binding = ListNameItemBinding.bind(view)

        //функція яка заповнює розмітку
        fun setData(shopListNameItem: ShopListNameItem, listener: Listener, defPres: SharedPreferences) = with(binding) {
            tvListName.text = shopListNameItem.name
            tvTime.text = shopListNameItem.time
            pBar.max = shopListNameItem.allItemCount
            pBar.progress = shopListNameItem.checkedItemsCounter
            if(defPres.getString("theme_key", "Green") == "Blue"){
                LinearLayoutCounter.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                imDelete.setImageResource(R.drawable.ic_delete_blue)
                imEdit.setImageResource(R.drawable.ic_edit_blue)
            }
            val colorState = ColorStateList.valueOf(getProgresColorState(shopListNameItem, binding.root.context, defPres))
            pBar.progressTintList = colorState
            val counterText = "${shopListNameItem.checkedItemsCounter}/${shopListNameItem.allItemCount}"
            tvCounter.text = counterText
            imDelete.setOnClickListener {
                listener.deleteItem(shopListNameItem.id!!)
            }
            imEdit.setOnClickListener {
                listener.editItem(shopListNameItem)
            }
            itemView.setOnClickListener {
                listener.onClickItem(shopListNameItem)
            }
        }

        private fun getProgresColorState(item: ShopListNameItem, context: Context, defPres: SharedPreferences): Int{
            return if(item.checkedItemsCounter == item.allItemCount)
                if(defPres.getString("theme_key", "Green") == "Blue"){
                    ContextCompat.getColor(context, R.color.blue)
                }else
                ContextCompat.getColor(context, R.color.green)
            else
                ContextCompat.getColor(context, R.color.light_grey_for_shop_items)
            }

        //потрібна для того щоб ініціалізувати наш клас NoteHolder
        companion object {
            fun create(parent: ViewGroup): ItemHolder {
                //загружаєм в память розмітку і повераєм
                val context = parent.context
                val view =  LayoutInflater.from(parent.context).inflate(R.layout.list_name_item, parent, false)
                //загружаєм в память розмітку і повераєм
                return ItemHolder(view, context)
                 }
             }
        }




    //відповідає за те щоб порівнювати між собою елементи з старого списку
    //і нового, щоб самомму не займатись оновленням списку
    class ItemComparator : DiffUtil.ItemCallback<ShopListNameItem>() {
        //порівнює елементи
        override fun areItemsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean {
            //ми порівнюємо по індетифікатору, бо вoни в нас індивідуальні
            return oldItem.id == newItem.id
        }

        //порівнює весь контент
        override fun areContentsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean {
            return oldItem == newItem
        }
    }


    interface Listener {
        fun deleteItem(id: Int)
        fun editItem(shopListName: ShopListNameItem)
        fun onClickItem(shopListName: ShopListNameItem)
    }

}