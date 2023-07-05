package com.recipes.demo.repository;

import com.recipes.demo.repository.entity.MeasurementUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MeasurementUnitRepository extends JpaRepository<MeasurementUnit, Long> {

    List<MeasurementUnit> findByIdIn(Set<Long> nameList);
}
