package tech.pauly.findapet.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import tech.pauly.findapet.data.models.Converters;
import tech.pauly.findapet.data.models.Filter;

@Database(entities = {Filter.class}, version = 5)
@TypeConverters(Converters.class)
public abstract class FilterDatabase extends RoomDatabase {
    public abstract FilterDao filterDao();
}
