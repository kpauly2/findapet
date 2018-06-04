package tech.pauly.findapet.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Objects;

@Entity
public class Filter {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @TypeConverters(Sex.class)
    private Sex sex = Sex.U;

    @TypeConverters(Age.class)
    private Age age = Age.MISSING;
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

    public Age getAge() {
        return age;
    }

    public void setAge(Age age) {
        this.age = age;
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
        return Objects.equals(id, filter.id) && sex == filter.sex && age == filter.age;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, sex, age);
    }
}
