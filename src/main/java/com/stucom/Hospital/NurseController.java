package com.stucom.Hospital;

import java.net.URI;

public class NurseController{
	
	@PostMapping("/login")
    public @ResponseBody ResponseEntity<Boolean> login(@RequestBody Nurse inputNurse) {
    Optional<Nurse> nurseLogin = nurseRepository.findByUserAndPassword(
        inputNurse.getUser(), inputNurse.getPassword()
    );

    if (nurseLogin.isPresent()) {
        return ResponseEntity.ok(true);
    }

    Logger.warn("Error in login");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
}

}