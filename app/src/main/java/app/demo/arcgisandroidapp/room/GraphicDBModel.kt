package app.demo.arcgisandroidapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "graphic_table")
data class GraphicDBModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "name") var name: String? = null,
    @ColumnInfo(name = "category") var category: String? = null,
    @ColumnInfo(name = "sub_category") var subCategory: String? = null,
    @ColumnInfo(name = "tag") var tag: String? = null,
    @ColumnInfo(name = "description") var description: String? = null,
    @ColumnInfo(name = "color") var color: String? = null,
    @ColumnInfo(name = "geometry") var geometry: String? = null,
    @ColumnInfo(name = "attributes") var attributes: String? = null,
    @ColumnInfo(name = "meta") var meta: String? = null,
    @ColumnInfo(name = "created_on") var createdOn: String? = null,
    @ColumnInfo(name = "last_updated") var lastUpdated: String? = null

)