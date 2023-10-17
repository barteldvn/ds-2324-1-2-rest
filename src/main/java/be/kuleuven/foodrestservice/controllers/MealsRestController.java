package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import be.kuleuven.foodrestservice.exceptions.MealNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Get a meal by its id", description = "Get a meal by id description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the meal",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Meal.class))}),
            @ApiResponse(responseCode = "404", description = "Meal not found", content = @Content)})
    @GetMapping("/rest/meals/{id}")
    ResponseEntity<?> getMealById(
            @Parameter(description = "Id of the meal", schema = @Schema(format = "uuid", type = "string"))
            @PathVariable String id) {
        Meal meal = mealsRepository.findMeal(id).orElseThrow(() -> new MealNotFoundException(id));
        EntityModel<Meal> mealEntityModel = mealToEntityModel(id, meal);
        return ResponseEntity.ok(mealEntityModel);
    }

    @GetMapping("/rest/meals")
    CollectionModel<EntityModel<Meal>> getMeals() {
        Collection<Meal> meals = mealsRepository.getAllMeal();

        List<EntityModel<Meal>> mealEntityModels = new ArrayList<>();
        for (Meal m : meals) {
            EntityModel<Meal> em = mealToEntityModel(m.getId(), m);
            mealEntityModels.add(em);
        }
        return CollectionModel.of(mealEntityModels,
                linkTo(methodOn(MealsRestController.class).getMeals()).withSelfRel());
    }

    @Operation(summary = "Get the largest meal", description = "Get the largest meal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the largest meal",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Meal.class))}),
            @ApiResponse(responseCode = "404", description = "Could not find largest meal", content = @Content)})
    @GetMapping("/rest/meals/largest")
    ResponseEntity<?> getLargestMeal() {
        Collection<Meal> meals = mealsRepository.getAllMeal();

        List<EntityModel<Meal>> mealEntityModels = new ArrayList<>();
        for (Meal m : meals) {
            EntityModel<Meal> em = mealToEntityModel(m.getId(), m);
            mealEntityModels.add(em);
        }
        Meal largestMeal = mealEntityModels.get(0).getContent();
        for (EntityModel<Meal> m : mealEntityModels) {
            int oldLargest = largestMeal.getKcal();
            int newlargest = m.getContent().getKcal();
            if(newlargest>oldLargest){
                largestMeal = m.getContent();
            }
        }
        EntityModel<Meal> cheapestMealEntityModel = mealToEntityModel(largestMeal.getId(), largestMeal);
        return ResponseEntity.ok(cheapestMealEntityModel);
    }

    @Operation(summary = "Get the cheapest meal", description = "Get the cheapest meal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the cheapest meal",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Meal.class))}),
            @ApiResponse(responseCode = "404", description = "Could not find cheapest meal", content = @Content)})
    @GetMapping("/rest/meals/cheapest")
    ResponseEntity<?> getCheapestMeal() {
        Collection<Meal> meals = mealsRepository.getAllMeal();

        List<EntityModel<Meal>> mealEntityModels = new ArrayList<>();
        for (Meal m : meals) {
            EntityModel<Meal> em = mealToEntityModel(m.getId(), m);
            mealEntityModels.add(em);
        }
        Meal cheapestMeal = mealEntityModels.get(0).getContent();
        for (EntityModel<Meal> m : mealEntityModels) {
            Double oldPrice = cheapestMeal.getPrice();
            Double newPrice = m.getContent().getPrice();
            if(newPrice<oldPrice){
                cheapestMeal = m.getContent();
            }
        }
        EntityModel<Meal> cheapestMealEntityModel = mealToEntityModel(cheapestMeal.getId(), cheapestMeal);
        return ResponseEntity.ok(cheapestMealEntityModel);
    }

    private EntityModel<Meal> mealToEntityModel(String id, Meal meal) {
        return EntityModel.of(meal,
                linkTo(methodOn(MealsRestController.class).getMealById(id)).withSelfRel(),
                linkTo(methodOn(MealsRestController.class).getMeals()).withRel("All Meals"));
    }
}
