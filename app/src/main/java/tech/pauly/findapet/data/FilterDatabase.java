package tech.pauly.findapet.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import tech.pauly.findapet.data.models.Filter;

@Database(entities = {Filter.class}, version = 2)
public abstract class FilterDatabase extends RoomDatabase {
    public abstract FilterDao filterDao();
}
