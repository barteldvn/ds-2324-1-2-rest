package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import be.kuleuven.foodrestservice.exceptions.MealNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
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

    @GetMapping("/restrpc/cheapestmeal")
    public Meal getCheapestMeal() {
        Double cheapestPrice = 100000.00;
        Meal cheapestMeal = null;
        Collection<Meal> meals = getMeals();
        for (Meal meal : getMeals()){

            if (meal.getPrice() < cheapestPrice ){
                cheapestPrice = meal.getPrice();
                cheapestMeal = meal;
            }
        }
        if (cheapestMeal == null)  throw new MealNotFoundException();
        return cheapestMeal;
    }

    @GetMapping("/restrpc/largestmeal")
    public Meal getLargestMeal() {
        int largestKcal = 0;
        Meal largestMeal = null;
        for (Meal meal : getMeals()){
            if (meal.getKcal() > largestKcal ){
                largestKcal = meal.getKcal();
                largestMeal = meal;
            }
        }
        if (largestMeal == null)  throw new MealNotFoundException();
        return largestMeal;
    }

    @DeleteMapping("/restrpc/meals/delete/{id}")
    public String deleteMeal(@PathVariable String id) {
        if (mealsRepository.getAllMeal().remove(getMealById(id))) return "200";
        else return "404";
    }

    @PutMapping("/restrpc/meals/update/{id}")
    public String updateMeal(@RequestBody Meal meal, @RequestParam String id) {
        if (mealsRepository.getAllMeal().remove(getMealById(id))) {
            mealsRepository.getAllMeal().add(meal);
            return "200";
        }
        else return "404";
    }

    @PostMapping("/restrpc/meals/new/")
    public String newMeal(@RequestBody Meal meal) {
        mealsRepository.getAllMeal().add(meal);
        return "200";
    }



}