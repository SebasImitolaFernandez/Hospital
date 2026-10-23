package com.stucom.Hospital;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface NurseRepository extends CrudRepository<Nurse, Integer> {
List<Nurse> findByNameContainingIgnoreCase(String name);
}
