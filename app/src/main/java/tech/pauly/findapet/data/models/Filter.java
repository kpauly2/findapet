package tech.pauly.findapet.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Objects;

import tech.pauly.findapet.data.SexConverter;

@Entity
public class Filter {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @TypeConverters(SexConverter.class)
    private Sex sex = Sex.U;

    public Long getId() {
        return id;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Filter filter = (Filter) o;
        return Objects.equals(id, filter.id) && sex == filter.sex;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, sex);
    }
}
