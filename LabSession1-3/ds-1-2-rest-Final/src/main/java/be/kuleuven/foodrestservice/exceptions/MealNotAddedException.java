package be.kuleuven.foodrestservice.exceptions;

public class MealNotAddedException extends RuntimeException {

    public MealNotAddedException() {

        super("Could not add meal");
    }
}
