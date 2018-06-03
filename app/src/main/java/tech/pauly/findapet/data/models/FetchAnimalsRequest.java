package tech.pauly.findapet.data.models;

import java.util.Objects;

public class FetchAnimalsRequest {
    private final String location;
    private final Filter filter;
    private final AnimalType animalType;
    private final int lastOffset;

    public FetchAnimalsRequest(AnimalType animalType, int lastOffset, String location, Filter filter) {
        this.animalType = animalType;
        this.lastOffset = lastOffset;
        this.location = location;
        this.filter = filter;
    }

    public String getLocation() {
        return location;
    }

    public Filter getFilter() {
        return filter;
    }

    public AnimalType getAnimalType() {
        return animalType;
    }

    public int getLastOffset() {
        return lastOffset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FetchAnimalsRequest request = (FetchAnimalsRequest) o;
        return lastOffset == request.lastOffset && Objects.equals(location, request.location) && Objects.equals(filter, request.filter) && animalType == request.animalType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(location, filter, animalType, lastOffset);
    }
}
