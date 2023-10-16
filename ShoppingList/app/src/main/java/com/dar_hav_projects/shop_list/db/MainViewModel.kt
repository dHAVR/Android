package com.dar_hav_projects.shop_list.db

import android.util.Log
import androidx.lifecycle.*
import com.dar_hav_projects.shop_list.entities.LibraryItem
import com.dar_hav_projects.shop_list.entities.NoteItem
import com.dar_hav_projects.shop_list.entities.ShopListItem
import com.dar_hav_projects.shop_list.entities.ShopListNameItem
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

//посередник між View i Model для того щоб не давати прямого доступу до бізнес логіки
class MainViewModel(dataBase: MainDataBase) : ViewModel() {

    //отримує Dao
    val dao = dataBase.getDao()

    //Весь список заміток який оголошується, як лайф дата
    //ми прослуховуєм чи є якісь зміни в наших замітках
    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData()
    val allShopListNames: LiveData<List<ShopListNameItem>> = dao.getAllShopListNames().asLiveData()
    val libraryItems = MutableLiveData<List<LibraryItem>>()

    fun getAllItemsFromList(listId: Int): LiveData<List<ShopListItem>>{
        return dao.getAllShopListItems(listId).asLiveData()
    }

    fun getAllLibraryItems(name: String)= viewModelScope.launch {
        libraryItems.postValue(dao.getAllLibraryItemsByName(name))
    }

    //функція для заповнення бази данних
    //так як це операція яка може заняти якийсь час ми будемо робити її в курутинах
    //  viewModelScope.launch {  } запускаєм другий потік
    fun insertNote(note: NoteItem) = viewModelScope.launch {
        dao.insertNote(note)
    }

    fun insertShopListName(listName: ShopListNameItem) = viewModelScope.launch {
        dao.insertShopListName(listName)
    }

    fun insertShopItem(shopListItem: ShopListItem) = viewModelScope.launch {
        dao.insertShopItem(shopListItem)
        Log.d("MyLog1", "shop")
        if(!isLibraryItemExists(shopListItem.name)){
            Log.d("MyLog1", "library")
            dao.insertLibraryItem(LibraryItem(null, shopListItem.name ))
        }
    }

    fun updateNote(note: NoteItem) = viewModelScope.launch {
        dao.updateNote(note)
    }

    fun updateListName(shopListName: ShopListNameItem) = viewModelScope.launch {
        dao.updateListName(shopListName)
    }

    fun updateListItem(item: ShopListItem) = viewModelScope.launch {
        dao.updateListItem(item)
    }

    fun updateLibraryItem(item: LibraryItem) = viewModelScope.launch {
        dao.updateLibraryItem(item)
    }

    //видалення елемента
    fun deleteNote(id: Int) = viewModelScope.launch {
        dao.deleteNote(id)
    }

    fun deleteLibraryItem(id: Int) = viewModelScope.launch {
        dao.deleteLibraryItem(id)
    }


    fun deleteShopItem(id: Int) = viewModelScope.launch {
        dao.deleteShopItem(id)
    }

    fun deleteShopList(id: Int, delete_list: Boolean) = viewModelScope.launch {
        if(delete_list){
            dao.deleteShopListName(id)
            dao.deleteShopItemsByListId(id)
        } else{
            dao.deleteShopItemsByListId(id)
        }
    }


    private suspend fun isLibraryItemExists(name: String): Boolean{
        Log.d("MyLog1", "${dao.getAllLibraryItemsByName(name).isNotEmpty()}")
        return dao.getAllLibraryItemsByName(name).isNotEmpty()

    }



    //cтворюєм клас який буду ініціалізувати клас MainViewModel
    class MainViewModelFactory(val dataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UCHECKED_CAST")
                return MainViewModel(dataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
        //просто промовчу....

    }
}