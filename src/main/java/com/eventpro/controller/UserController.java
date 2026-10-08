package com.eventpro.controller;

import com.eventpro.entity.User;
import com.eventpro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User userDetails) {
        Long numericId = null;
        try {
            numericId = Long.valueOf(id.replaceAll("\\D", ""));
        } catch (Exception e) {}
        if (numericId == null) return ResponseEntity.badRequest().build();

        return userRepository.findById(numericId).map(user -> {
            if (userDetails.getName() != null) user.setName(userDetails.getName());
            if (userDetails.getPhone() != null) user.setPhone(userDetails.getPhone());
            if (userDetails.getRole() != null) user.setRole(userDetails.getRole());
            if (userDetails.getAvatar() != null) user.setAvatar(userDetails.getAvatar());
            if (userDetails.getStatus() != null) user.setStatus(userDetails.getStatus());
            User updated = userRepository.save(user);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchUser(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        Long numericId = null;
        try {
            numericId = Long.valueOf(id.replaceAll("\\D", ""));
        } catch (Exception e) {}
        if (numericId == null) return ResponseEntity.badRequest().build();

        return userRepository.findById(numericId).map(user -> {
            if (updates.containsKey("role")) {
                try {
                    user.setRole(User.Role.valueOf(updates.get("role").toString().toLowerCase()));
                } catch (Exception e) {}
            }
            if (updates.containsKey("status")) {
                user.setStatus(updates.get("status").toString().toLowerCase());
            }
            User updated = userRepository.save(user);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        Long numericId = null;
        try {
            numericId = Long.valueOf(id.replaceAll("\\D", ""));
        } catch (Exception e) {}
        if (numericId == null) return ResponseEntity.badRequest().build();

        if (!userRepository.existsById(numericId)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(numericId);
        return ResponseEntity.ok().build();
    }
}
