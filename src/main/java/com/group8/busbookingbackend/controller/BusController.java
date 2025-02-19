package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.entity.BusEntity;
import com.group8.busbookingbackend.entity.CategoryEntity;
import com.group8.busbookingbackend.service.IBusService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bus")
public class BusController {
    @Autowired
    private IBusService busService;
    @GetMapping("/categories")
    public ApiResponse<List<CategoryEntity>> getAllCategories() {
        List<CategoryEntity> result = busService.findAllCategory();
        return ApiResponse.success(result, "Fetched all category successfully");
    }

    @GetMapping("/category/{id}")
    public ApiResponse<List<BusEntity>> getCategory(@PathVariable ObjectId id) {
        List<BusEntity> result = busService.findByCategoryId(id);
        return ApiResponse.success(result, "Fetched all bus by category successfully");
    }

    @GetMapping("/recent")
    public ApiResponse<List<BusEntity>> getRecent(@RequestParam LocalDateTime date) {
        List<BusEntity> result = busService.findRecentBus(date);
        return ApiResponse.success(result, "Fetched recent bus successfully");
    }

    @GetMapping("/top-sellers")
    public ApiResponse<List<BusEntity>> getTopSellingBuses() {
        List<BusEntity> result = busService.findTop10BestSellers();
        return ApiResponse.success(result, "Fetched top selling successfully");
    }
}
