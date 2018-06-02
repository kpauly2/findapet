package tech.pauly.findapet.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Filter {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "sex")
    private int sex;

    public Long getId() {
        return id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
