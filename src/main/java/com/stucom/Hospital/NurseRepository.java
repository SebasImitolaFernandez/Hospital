package com.stucom.Hospital;

import org.springframework.data.repository.CrudRepository;

public interface NurseRepository extends JpaRepository<Nurse, Long> {
    boolean existUserPassword(String user,String password);
    Optional<Nurse> findByName(String name);
}