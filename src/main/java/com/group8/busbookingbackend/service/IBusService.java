package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.entity.BusEntity;
import com.group8.busbookingbackend.entity.CategoryEntity;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

public interface IBusService {
    List<CategoryEntity> findAllCategory();

    public List<BusEntity> findByCategoryId(ObjectId categoryId);
    public BusEntity findById(ObjectId id);
    public List<BusEntity> findAll();
    public List<BusEntity> findTop10BestSellers();
    public List<BusEntity> findRecentBus(LocalDateTime date);
}
