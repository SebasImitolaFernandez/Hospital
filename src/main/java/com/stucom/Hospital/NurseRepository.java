package com.stucom.Hospital;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface NurseRepository extends CrudRepository<Nurse, Long> {
    boolean existUserPassword(String user,String password);
    Optional<Nurse> findByName(String name);
}