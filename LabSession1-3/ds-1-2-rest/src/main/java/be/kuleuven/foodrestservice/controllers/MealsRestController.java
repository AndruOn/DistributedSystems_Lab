package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import be.kuleuven.foodrestservice.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class MealsRestController {

    private final MealsRepository mealsRepository;

    @Autowired
    MealsRestController(MealsRepository mealsRepository) {
        this.mealsRepository = mealsRepository;
    }

    @GetMapping("/rest/meals/{id}")
    EntityModel<Meal> getMealById(@PathVariable String id) {
        Meal meal = mealsRepository.findMeal(id).orElseThrow(() -> new MealNotFoundException(id));

        return mealToEntityModel(id, meal);
    }

    @GetMapping("/rest/meals")
    CollectionModel<EntityModel<Meal>> getMeals() {
        Collection<Meal> meals = mealsRepository.getAllMeal();

        List<EntityModel<Meal>> mealEntityModels = new ArrayList<>();
        for (Meal m : meals) {
            EntityModel<Meal> em = mealToEntityModel(m.getId(), m);
            mealEntityModels.add(em);
        }
        System.out.println("info:all meals got");
        return CollectionModel.of(mealEntityModels,
                linkTo(methodOn(MealsRestController.class).getMeals()).withSelfRel());
    }

    private EntityModel<Meal> mealToEntityModel(String id, Meal meal) {
        return EntityModel.of(meal,
                linkTo(methodOn(MealsRestController.class).getMealById(id)).withSelfRel(),
                linkTo(methodOn(MealsRestController.class).getMeals()).withRel("rest/meals"));
    }

    @GetMapping("/rest/meals/cheapest")
    EntityModel<Meal> getCheapestMeal() {
        Meal meal = mealsRepository.findCheapestMeal().orElseThrow(() -> new NullPointerException());
        return mealToEntityModel(meal.getId(), meal);
    }

    @GetMapping("/rest/meals/biggest")
    EntityModel<Meal> getBiggestMeal() {
        Meal meal = mealsRepository.findBiggestMeal().orElseThrow(() -> new NullPointerException());
        return mealToEntityModel(meal.getId(), meal);
    }

    @PostMapping("/rest/meals")
    EntityModel<Meal> addNewMeal(@RequestBody Meal meal) {
        //System.out.println(meal);

        Meal addedMeal = mealsRepository.addNewMeal(meal).orElseThrow(() -> new FormatNotAcceptedException());
        //InputStream inputStream = TypeReference.class.getResourceAsStream("/json/users.json");

        return mealToEntityModel(addedMeal.getId(), addedMeal);
    }

    @PutMapping("/rest/meals/{id}")
    EntityModel<Meal> updateMeal(@PathVariable String id, @RequestBody Meal meal) {
        Meal mealUpdated = mealsRepository.updateMeal(id, meal).orElseThrow(() -> new MealNotFoundException(id));
        System.out.printf("updatemeal:%s%n\n",mealUpdated.getDescription());
        return mealToEntityModel(mealUpdated.getId(), mealUpdated);
    }

    @DeleteMapping("/rest/meals/{id}")
    EntityModel<Meal> deleteMeal(@PathVariable String id) {
        Meal deletedMeal = mealsRepository.deleteMeal(id).orElseThrow(() -> new MealNotFoundException(id));
        System.out.printf("info:%s%n\n",deletedMeal.getDescription());
        return mealToEntityModel(deletedMeal.getId(), deletedMeal);
    }



}
