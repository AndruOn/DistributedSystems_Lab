package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import be.kuleuven.foodrestservice.exceptions.MealNotAddedException;
import be.kuleuven.foodrestservice.exceptions.MealNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
public class MealsRestRpcStyleController {

    private final MealsRepository mealsRepository;

    @Autowired
    MealsRestRpcStyleController(MealsRepository mealsRepository) {
        this.mealsRepository = mealsRepository;
    }

    @GetMapping("/restrpc/meals/{id}")
    Meal getMealById(@PathVariable String id) {
        Optional<Meal> meal = mealsRepository.findMeal(id);

        return meal.orElseThrow(() -> new MealNotFoundException(id));
    }

    @GetMapping("/restrpc/meals")
    Collection<Meal> getMeals() {
        return mealsRepository.getAllMeal();
    }

    @GetMapping("/restrpc/meals/cheapest")
    Meal getCheapestMeal() {
        Meal meal = mealsRepository.findCheapestMeal().orElseThrow(() -> new MealNotFoundException("CHEAPEST"));
        return meal;
    }

    @GetMapping("/restrpc/meals/biggest")
    Meal getBiggestMeal() {
        Meal meal = mealsRepository.findBiggestMeal().orElseThrow(() -> new MealNotFoundException("BIGGEST"));
        return meal;
    }

    @PostMapping("/restrpc/meals")
    Meal addNewMeal(@RequestBody Meal meal) {
        //System.out.println(meal);

        Meal addedMeal = mealsRepository.addNewMeal(meal).orElseThrow(() -> new MealNotAddedException());
        //InputStream inputStream = TypeReference.class.getResourceAsStream("/json/users.json");

        return addedMeal;
    }

    @PutMapping("/restrpc/meals/{id}")
    Meal updateMeal(@PathVariable String id, @RequestBody Meal meal) {
        meal = mealsRepository.updateMeal(id, meal).orElseThrow(() -> new MealNotFoundException(id));

        return meal;
    }

    @DeleteMapping("/restrpc/meals/{id}")
    Meal deleteMeal(@PathVariable String id) {
        Meal deletedMeal = mealsRepository.deleteMeal(id).orElseThrow(() -> new MealNotFoundException(id));

        return deletedMeal;
    }

}
