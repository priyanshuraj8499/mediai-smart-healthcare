package com.mediai.repository;

import com.mediai.entity.HospitalResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HospitalResourceRepository extends JpaRepository<HospitalResource, Long> {
    List<HospitalResource> findByCategory(HospitalResource.Category category);
}
