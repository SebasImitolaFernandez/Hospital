package com.stucom.Hospital;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NurseRepository extends JpaRepository<Nurse, Integer> {

    List<Nurse> findByNameContainingIgnoreCase(String name);

    Optional<Nurse> findByUserAndPassword(String user, String password);
}