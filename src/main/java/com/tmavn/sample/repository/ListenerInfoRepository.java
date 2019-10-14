package com.tmavn.sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tmavn.sample.entity.ListenerInfo;

@Repository
public interface ListenerInfoRepository extends JpaRepository<ListenerInfo, Long> {

    @Query("SELECT li from ListenerInfo li where li.userId = :userId")
    Iterable<ListenerInfo> findByUserId(@Param(value = "userId") String id);

    Iterable<ListenerInfo> findByQueryContaining(String state);
}
