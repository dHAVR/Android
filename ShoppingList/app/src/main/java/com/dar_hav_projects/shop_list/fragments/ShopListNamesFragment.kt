package com.dar_hav_projects.shop_list.fragments


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dar_hav_projects.shop_list.activities.MainApp
import com.dar_hav_projects.shop_list.activities.ShopListActivity
import com.dar_hav_projects.shop_list.databinding.FragmentShopListNamesBinding

import com.dar_hav_projects.shop_list.db.MainViewModel
import com.dar_hav_projects.shop_list.db.ShopListNameAdapter
import com.dar_hav_projects.shop_list.entities.ShopListNameItem
import com.dar_hav_projects.shop_list.entities.dialogs.DeleteDialog
import com.dar_hav_projects.shop_list.entities.dialogs.NewListDialog
import com.dar_hav_projects.shop_list.utils.TimeManager



class ShopListNamesFragment : BaseFragment(), ShopListNameAdapter.Listener {
    private lateinit var binding: FragmentShopListNamesBinding
    private lateinit var adapter: ShopListNameAdapter
    private lateinit var defPref: SharedPreferences

    //У нас MainApp на рівні всього застосунку тому ми його робим як контекст....? і витаскуєм базу данних
    // тут ми запускаємо ініціалізацію MainViewModel через MainViewModelFactory
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).dataBase)
    }

    //Запускаємо через лаунчер актівіті
    override fun onClickNew() {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener {
            override fun onClick(name: String) {
                val shopListName = ShopListNameItem(
                    null,
                    name,
                    TimeManager.getCurrentTime(),
                    0,
                    0,
                    ""
                )
                mainViewModel.insertShopListName(shopListName)
            }
        }, "")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentShopListNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    //observer який буде слідкувати за змінами в баззі данних
    private fun observer() {
        mainViewModel.allShopListNames.observe(viewLifecycleOwner) {
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
        rcView.layoutManager = LinearLayoutManager(activity)
        defPref = PreferenceManager.getDefaultSharedPreferences(activity)
        adapter = ShopListNameAdapter(this@ShopListNamesFragment, defPref)
        rcView.adapter = adapter
    }


    companion object {

        @JvmStatic
        //якщо визиваємо newInstance то видає новий фрагмент,
        // але якщо я намагаюсь другий раз визвати, то буде видавати вже готову інстанцію
        fun newInstance() = ShopListNamesFragment()

    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener {
            override fun onClick() {
                mainViewModel.deleteShopList(id, true)
            }
        })
    }

    override fun editItem(shopListName: ShopListNameItem) {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener {
            override fun onClick(name: String) {
                mainViewModel.updateListName(shopListName.copy(name = name))
            }
        }, shopListName.name)
    }

    override fun onClickItem(shopListName: ShopListNameItem) {
       val i = Intent(activity, ShopListActivity::class.java).apply {
         putExtra(ShopListActivity.SHOP_LIST_NAME, shopListName)
       }
        startActivity(i)
    }


}