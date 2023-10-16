package com.dar_hav_projects.shop_list.db

import android.content.SharedPreferences
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.dar_hav_projects.shop_list.R
import com.dar_hav_projects.shop_list.databinding.ShopLibraryListItemBinding
import com.dar_hav_projects.shop_list.databinding.ShopListItemBinding
import com.dar_hav_projects.shop_list.entities.ShopListItem

//Бере з списку значення елементів, потім бере розмітку і заповнює її значенням, потім друкує її в RecycleView
class ShopListItemAdapter(private val listener: Listener, private val defPres: SharedPreferences) :
    ListAdapter<ShopListItem, ShopListItemAdapter.ItemHolder>(ItemComparator()) {

    //вона відповідає за створення і повернення нового екземпляру ViewHolder для відображення даних елементу списку.
    //функцяя створює для кожної замітки свій ItemHolder, який буде сворювати розмітку
    // і також це клас який зберігає в собі розмітку
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if(viewType == 0)
            ItemHolder.createShopItem(parent)
        else
            ItemHolder.createLibraryItem(parent)

    }

    //Функція для заповнення розмітки
    //нам приходить holder, і сама позиція position
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        //визиваємо функцію
        if(getItem(position).item_type == 0) {
            holder.setShopData(getItem(position), listener, defPres)
        }
        else {
            holder.setLibraryData(getItem(position), listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).item_type
    }

    //Цей клас сберігає ссилки на візуальні елементи для того щоб оптимізувати роботу самого  RecycleView
    class ItemHolder(val view: View) : RecyclerView.ViewHolder(view) {


        //функція яка заповнює розмітку
        fun setShopData(shopListItem: ShopListItem, listener: Listener, defPres: SharedPreferences ) {
           val binding = ShopListItemBinding.bind(view)
            binding.apply {
            tvName.text = shopListItem.name
            tvInfo.text = shopListItem.info
            tvInfo.visibility= infoVisibility(shopListItem)
            chBox.isChecked = shopListItem.cheched
            setPaintFlagAndColor(binding)
                if(defPres.getString("theme_key", "Green") == "Blue"){
                   imDelete.setImageResource(R.drawable.ic_delete_blue)
                   imEdit.setImageResource(R.drawable.ic_edit_blue)
                }
              chBox.setOnClickListener {
                listener.onClickItem(shopListItem.copy(cheched = chBox.isChecked),CHECK_BOX)
            }
              imEdit.setOnClickListener {
                  listener.onClickItem(shopListItem, EDIT)
              }
                imDelete.setOnClickListener {
                  listener.onClickItem(shopListItem, DELETE_SHOP_ITEM)
                }
            }
        }
        fun setLibraryData(shopListItem: ShopListItem, listener: Listener ) {
            val binding = ShopLibraryListItemBinding.bind(view)
            binding.apply{
                tvName.text = shopListItem.name
                imEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT_LIBRARY_ITEM)
                }
                imDelete.setOnClickListener {
                    listener.onClickItem(shopListItem, DELETE_LIBRARY_ITEM)
                }
                itemView.setOnClickListener {
                    listener.onClickItem(shopListItem, ADD_LIBRARY_ITEM)
                }
            }
        }

        private fun setPaintFlagAndColor(binding: ShopListItemBinding){
            binding.apply {
                if(chBox.isChecked){
                    tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.light_grey_for_shop_items))
                    tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.light_grey_for_shop_items))
                } else {
                    tvName.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                }
            }
        }

        private fun infoVisibility(shopListItem: ShopListItem): Int {
          return if(shopListItem.info.isNullOrEmpty()){
              View.GONE
          } else{
              View.VISIBLE
          }
        }
        //потрібна для того щоб ініціалізувати наш клас NoteHolder
        companion object {

            fun createShopItem(parent: ViewGroup): ItemHolder {
                //загружаєм в память розмітку і повераєм
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.shop_list_item, parent, false)
                )
            }

            fun createLibraryItem(parent: ViewGroup): ItemHolder {
                //загружаєм в память розмітку і повераєм
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.shop_library_list_item, parent, false)
                )
            }
        }
    }

    //відповідає за те щоб порівнювати між собою елементи з старого списку
    //і нового, щоб самомму не займатись оновленням списку
    class ItemComparator : DiffUtil.ItemCallback<ShopListItem>() {
        //порівнює елементи
        override fun areItemsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
            //ми порівнюємо по індетифікатору, бо вoни в нас індивідуальні
            return oldItem.id == newItem.id
        }

        //порівнює весь контент
        override fun areContentsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
            return oldItem == newItem
        }
    }


    interface Listener {
        fun onClickItem(shopList: ShopListItem, state: Int)
    }

    companion object{
        const val EDIT = 0
        const val CHECK_BOX = 1
        const val EDIT_LIBRARY_ITEM = 2
        const val DELETE_LIBRARY_ITEM = 3
        const val ADD_LIBRARY_ITEM = 4
        const val DELETE_SHOP_ITEM = 5
    }

}