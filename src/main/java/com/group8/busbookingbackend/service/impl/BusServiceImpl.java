package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.entity.BusEntity;
import com.group8.busbookingbackend.entity.CategoryEntity;
import com.group8.busbookingbackend.repository.BookingRepository;
import com.group8.busbookingbackend.repository.BusRepository;
import com.group8.busbookingbackend.repository.CategoryRepository;
import com.group8.busbookingbackend.service.IBusService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BusServiceImpl implements IBusService {
    @Autowired
    BusRepository busRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<CategoryEntity> findAllCategory(){
        return categoryRepository.findAll();
    }

    @Override
    public List<BusEntity> findByCategoryId(ObjectId categoryId) {
        return busRepository.findByCategoryId(categoryId);
    }

    @Override
    public BusEntity findById(ObjectId id) {
        return busRepository.findById(id).orElse(null);
    }

    @Override
    public List<BusEntity> findAll() {
        return busRepository.findAll();
    }

    @Override
    public List<BusEntity> findTop10BestSellers() {
        return bookingRepository.findTop10BestSellers();
    }

    @Override
    public List<BusEntity> findRecentBus(LocalDateTime date) {
        return busRepository.findRecentBus(date);
    }
}
