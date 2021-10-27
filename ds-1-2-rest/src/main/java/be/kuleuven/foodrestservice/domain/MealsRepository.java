package be.kuleuven.foodrestservice.domain;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class MealsRepository {
    // map: id -> meal
    private static final Map<String, Meal> meals = new HashMap<>();

    @PostConstruct
    public void initData() {

        Meal a = new Meal();
        a.setId("5268203c-de76-4921-a3e3-439db69c462a");
        a.setName("Steak");
        a.setDescription("Steak with fries");
        a.setMealType(MealType.MEAT);
        a.setKcal(1100);
        a.setPrice((10.00));

        meals.put(a.getId(), a);

        Meal b = new Meal();
        b.setId("4237681a-441f-47fc-a747-8e0169bacea1");
        b.setName("Portobello");
        b.setDescription("Portobello Mushroom Burger");
        b.setMealType(MealType.VEGAN);
        b.setKcal(637);
        b.setPrice((7.00));

        meals.put(b.getId(), b);

        Meal c = new Meal();
        c.setId("cfd1601f-29a0-485d-8d21-7607ec0340c8");
        c.setName("Fish and Chips");
        c.setDescription("Fried fish with chips");
        c.setMealType(MealType.FISH);
        c.setKcal(950);
        c.setPrice(5.00);

        meals.put(c.getId(), c);
    }

    public Optional<Meal> findMeal(String id) {
        Assert.notNull(id, "The meal id must not be null");
        Meal meal = meals.get(id);
        return Optional.ofNullable(meal);
    }

    public Collection<Meal> getAllMeal() {
        return meals.values();
    }

    public Optional<Meal> findCheapestMeal() {
        Meal cheapestMeal = new Meal();
        cheapestMeal.price = Double.MAX_VALUE;
        Collection<Meal> allMeals = meals.values();
        for (Meal meal : allMeals) {
            if(meal.price < cheapestMeal.price)
                cheapestMeal = meal;
        }
        return Optional.ofNullable(cheapestMeal);
    }

    public Optional<Meal> findBiggestMeal() {
        Meal biggestMeal = new Meal();
        biggestMeal.kcal = 0;
        Collection<Meal> allMeals = meals.values();
        for (Meal meal : allMeals) {
            if(meal.kcal > biggestMeal.kcal)
                biggestMeal = meal;
        }
        return Optional.ofNullable(biggestMeal);
    }

    public Optional<Meal> addNewMeal(Meal meal) {
        String id = "123";
        System.out.println("Here");
        while(meals.containsKey(id)){
            try {
                Integer idInt = Integer.valueOf(id);
                idInt += 1;
                id = idInt.toString();
                System.out.println(id);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                break;
            }
        }
        meals.put(id, meal);
        meal.setId(id);
        System.out.println("Last id " + id);

        return Optional.ofNullable(meal);
    }

    public Optional<Meal> updateMeal(String id, Meal meal) {
        meals.replace(id, meal);

        return Optional.ofNullable(meal);
    }

    public Optional<Meal> deleteMeal(String id) {
        Meal meal = meals.get(id);
        if(meal != null)
            meals.remove(id);

        return Optional.ofNullable(meal);
    }

}
