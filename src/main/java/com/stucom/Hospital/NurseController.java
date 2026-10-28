package com.stucom.Hospital;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nurse")
public class NurseController {

	private static final Logger logger = LoggerFactory.getLogger(NurseController.class);
	private final NurseRepository nurseRepository;

	@Autowired
	public NurseController(NurseRepository nurseRepository) {
		this.nurseRepository = nurseRepository;
	}

	@GetMapping("/index")
	public ResponseEntity<Iterable<Nurse>> getAll() {
		return ResponseEntity.ok(nurseRepository.findAll());
	}
	
	@PostMapping("/login")
	public ResponseEntity<Boolean> login(@RequestBody Nurse inputNurse) {
		boolean ok = nurseRepository
				.findByUserAndPassword(inputNurse.getUser(), inputNurse.getPassword())
				.isPresent();
 
		if (ok) {
			return ResponseEntity.ok(true);
		}
 
		logger.warn("Error in login");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
	}

	@GetMapping("/name/{name}")
	public ResponseEntity<List<Nurse>> findByName(@PathVariable String name) {
		List<Nurse> result = nurseRepository.findByNameContainingIgnoreCase(name);
 
		if (!result.isEmpty()) {
			return ResponseEntity.ok(result);
		}
 
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	@PostMapping
	public ResponseEntity<Nurse> create(@RequestBody Nurse nurse) {
		return ResponseEntity.status(HttpStatus.CREATED).body(nurseRepository.save(nurse));
	}
	
	
}