package tech.pauly.findapet.data;

import java.util.Objects;

import tech.pauly.findapet.data.models.StatusCode;

public class PetfinderException extends Exception {
    private StatusCode statusCode;

    public PetfinderException(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PetfinderException that = (PetfinderException) o;
        return statusCode == that.statusCode;
    }

    @Override
    public int hashCode() {

        return Objects.hash(statusCode);
    }
}
