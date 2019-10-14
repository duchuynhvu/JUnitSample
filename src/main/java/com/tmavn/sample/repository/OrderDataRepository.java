package com.tmavn.sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tmavn.sample.entity.OrderData;

@Repository
public interface OrderDataRepository extends JpaRepository<OrderData, String> {
    
}
