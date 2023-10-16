package com.dar_hav_projects.shop_list.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_list_item")
data class ShopListItem(
    @PrimaryKey( autoGenerate = true)
    val id: Int?,
    @ColumnInfo (name = "name")
    val name: String,
    @ColumnInfo(name = "item_info")
    val info: String?,
    @ColumnInfo(name = "item_cheked")
    val cheched: Boolean = false,
    @ColumnInfo(name = "id_list")
    val id_list: Int,
    @ColumnInfo(name = "item_type")
    val item_type: Int = 0
)
