package com.dar_hav_projects.shop_list.db
import androidx.room.*
import androidx.room.Dao
import com.dar_hav_projects.shop_list.entities.LibraryItem
import com.dar_hav_projects.shop_list.entities.NoteItem
import com.dar_hav_projects.shop_list.entities.ShopListNameItem
import com.dar_hav_projects.shop_list.entities.ShopListItem
import kotlinx.coroutines.flow.Flow


//це наша бізнес-логіка
@Dao
interface Dao {

    @Query("SELECT * FROM note_list")
    fun getAllNotes(): Flow<List<NoteItem>>

    @Query("SELECT * FROM shopping_list_names")
    fun getAllShopListNames(): Flow<List<ShopListNameItem>>

    @Query("SELECT * FROM shop_list_item WHERE id_list LIKE :listId")
    fun getAllShopListItems(listId: Int): Flow<List<ShopListItem>>

    @Query("SELECT * FROM library WHERE name LIKE :name")
    suspend fun getAllLibraryItemsByName(name: String): List<LibraryItem>

    @Insert
    suspend fun insertNote(note: NoteItem)

    @Insert
    suspend fun insertShopItem(shopListItem: ShopListItem)

    @Insert
    suspend fun insertLibraryItem(libraryItem: LibraryItem)

    @Insert
    suspend fun insertShopListName(name: ShopListNameItem)

    @Update
    suspend fun updateNote(note: NoteItem)

    @Update
    suspend fun updateListName(shopListName: ShopListNameItem)

    @Update
    suspend fun updateListItem(shopListItem: ShopListItem)

    @Update
    suspend fun updateLibraryItem(libraryItem: LibraryItem)

    @Query("DELETE FROM note_list WHERE id is :id")
    suspend fun deleteNote(id: Int)

    @Query("DELETE FROM shopping_list_names WHERE id is :id")
    suspend fun deleteShopListName(id: Int)

    @Query("DELETE FROM library WHERE id is :id")
    suspend fun deleteLibraryItem(id: Int)

    @Query("DELETE FROM shop_list_item WHERE id is :id")
    suspend fun deleteShopItem(id: Int)

    @Query("DELETE FROM shop_list_item WHERE id_list LIKE :listId")
    suspend fun deleteShopItemsByListId(listId: Int)


}