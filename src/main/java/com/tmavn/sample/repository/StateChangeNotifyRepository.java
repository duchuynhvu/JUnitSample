package com.tmavn.sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tmavn.sample.entity.StateChangeNotify;

@Repository
public interface StateChangeNotifyRepository extends JpaRepository<StateChangeNotify, String> {

}
