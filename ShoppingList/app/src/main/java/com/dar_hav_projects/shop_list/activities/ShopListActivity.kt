package com.dar_hav_projects.shop_list.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.dar_hav_projects.shop_list.db.MainViewModel
import com.dar_hav_projects.shop_list.db.ShopListItemAdapter
import com.dar_hav_projects.shop_list.entities.LibraryItem
import com.dar_hav_projects.shop_list.entities.ShopListItem
import com.dar_hav_projects.shop_list.entities.ShopListNameItem
import com.dar_hav_projects.shop_list.entities.dialogs.EditListItemDialog
import com.dar_hav_projects.shop_list.utils.ShareHelper
import com.dar_hav_projects.shop_list.R
import com.dar_hav_projects.shop_list.databinding.ActivityShopListBinding

class ShopListActivity : AppCompatActivity(), ShopListItemAdapter.Listener {
    private lateinit var binding: ActivityShopListBinding
    private var shopListName: ShopListNameItem? = null
    private lateinit var saveItem: MenuItem
    private  var edItem: EditText? = null
    private var adapter: ShopListItemAdapter? = null
    private lateinit var textWatcher: TextWatcher
    lateinit private var defPref: SharedPreferences

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).dataBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.list_items)
        init()
        initRcView()
        listItemObserver()
        actionBarSettings()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu, menu)
        saveItem = menu?.findItem(R.id.save_item)!!
        val newItem = menu.findItem(R.id.new_item)
        edItem = newItem.actionView?.findViewById(R.id.edNewShopItem) as EditText
        textWatcher = textWatcher()
        newItem.setOnActionExpandListener(expandActionView())
        saveItem.isVisible = false
        return true
    }
    private fun expandActionView(): MenuItem.OnActionExpandListener{
        return object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                saveItem.isVisible = true
                edItem?.addTextChangedListener(textWatcher)
                libraryItemObserver()
                mainViewModel.getAllItemsFromList(shopListName?.id!!).removeObservers(this@ShopListActivity)
                mainViewModel.getAllLibraryItems("%%")
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                saveItem.isVisible = false
                invalidateOptionsMenu()
                edItem?.removeTextChangedListener(textWatcher)
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity)
                edItem?.setText("")
                listItemObserver()
                return true
            }

        }
    }

    //ініціалізуємо textWatcher
    private fun textWatcher(): TextWatcher{
        return object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
               mainViewModel.getAllLibraryItems("%$p0%")
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_item -> {
                addNewShopItem(edItem?.text.toString())
            }
            android.R.id.home -> {
                saveItemCount()
                finish()
            }
            R.id.delete_list -> {
                mainViewModel.deleteShopList(shopListName?.id!!, true)
                finish()
            }
            R.id.clear_list -> {
                mainViewModel.deleteShopList(shopListName?.id!!, false)
            }
            R.id.share_list ->{
                startActivity(Intent.createChooser(ShareHelper.shareShopList(adapter?.currentList!!, shopListName?.name!!), "Share by"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewShopItem(name:String){
        if(name.isEmpty())
            return
        val item = ShopListItem(
            null,
           name,
            null,
            false,
           shopListName?.id!!,
            0
            )
        edItem?.setText("")
        mainViewModel.insertShopItem(item)
    }

    private fun listItemObserver(){
        mainViewModel.getAllItemsFromList(shopListName?.id!!).observe( this) {
            adapter?.submitList(it)
            binding.tvEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun libraryItemObserver(){
        mainViewModel.libraryItems.observe(this) {
            val tempShopList = ArrayList<ShopListItem>()
            it.forEach { item ->
                val shopItem = ShopListItem(
                    item.id,
                    item.name,
                    null,
                    false,
                    0,
                    1
                )
                tempShopList.add(shopItem)
            }
            adapter?.submitList(tempShopList)
            binding.tvEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

    }

    private fun initRcView() = with(binding){
        rcView.layoutManager = LinearLayoutManager(this@ShopListActivity)
        adapter = ShopListItemAdapter(this@ShopListActivity, defPref)
        rcView.adapter = adapter
    }

    private fun init(){
        shopListName = intent.getSerializableExtra(SHOP_LIST_NAME) as ShopListNameItem

    }
    private fun actionBarSettings() {
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    companion object{
        const val SHOP_LIST_NAME = "shop_list_name"
    }

    override fun onClickItem(shopListItem: ShopListItem, state: Int) {
        when(state){
            ShopListItemAdapter.CHECK_BOX ->{
                mainViewModel.updateListItem(shopListItem)
            }
            ShopListItemAdapter.EDIT->{
                editShopListItem(shopListItem)
            }
            ShopListItemAdapter.EDIT_LIBRARY_ITEM->{
                editLibraryItem(shopListItem)
            }
            ShopListItemAdapter.DELETE_LIBRARY_ITEM ->{
                mainViewModel.deleteLibraryItem(shopListItem.id!!)
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
            ShopListItemAdapter.DELETE_SHOP_ITEM ->{
                mainViewModel.deleteShopItem(shopListItem.id!!)
            }
            ShopListItemAdapter.ADD_LIBRARY_ITEM ->{
                addNewShopItem(shopListItem.name)
            }
        }
    }

    private fun editShopListItem(item: ShopListItem){
        EditListItemDialog.showDialog(this, item, object: EditListItemDialog.Listener{
            override fun onClick(item: ShopListItem) {
                mainViewModel.updateListItem(item)
            }
        })
    }

    private fun editLibraryItem(item: ShopListItem){
        EditListItemDialog.showDialog(this, item, object: EditListItemDialog.Listener{
            override fun onClick(item: ShopListItem) {
                mainViewModel.updateLibraryItem(LibraryItem(item.id, item.name))
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
        })
    }

    private fun saveItemCount(){
        var checkedItemCounter = 0
        adapter?.currentList?.forEach{
            if(it.cheched == true){
                ++checkedItemCounter
            }
        }
        val tempShopListNameItem = shopListName?.copy(
            allItemCount = adapter?.itemCount!!,
            checkedItemsCounter = checkedItemCounter
        )
        mainViewModel.updateListName(tempShopListNameItem!!)
    }

    override fun onBackPressed() {
        saveItemCount()
        super.onBackPressed()
    }
    private fun getSelectedTheme():Int{
        return if(defPref.getString("theme_key", "Green") == "Green"){
            R.style.Theme_ShoppingListGreen
        } else{
            R.style.Theme_ShoppingListBlue
        }
    }
}