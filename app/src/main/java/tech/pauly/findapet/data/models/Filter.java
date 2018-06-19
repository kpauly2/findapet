package tech.pauly.findapet.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Objects;

@Entity
public class Filter {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private Sex sex = Sex.MISSING;

    private Age age = Age.MISSING;

    private AnimalSize size = AnimalSize.MISSING;

    private String breed = "";

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

    public AnimalSize getSize() {
        return size;
    }

    public void setSize(AnimalSize size) {
        this.size = size;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
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
        return Objects.equals(id, filter.id) && sex == filter.sex && age == filter.age && size == filter.size && Objects.equals(breed, filter.breed);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, sex, age, size, breed);
    }
}
