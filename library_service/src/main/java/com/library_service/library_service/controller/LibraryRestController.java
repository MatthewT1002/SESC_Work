package com.library_service.library_service.controller;

import com.library_service.library_service.model.LibraryAccount;
import com.library_service.library_service.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library")
public class LibraryRestController {

    @Autowired
    private LibraryService libraryService;

    @PostMapping("/account")
    public ResponseEntity<LibraryAccount> createAccount(@RequestParam String studentId,
                                                        @RequestParam String username) {
        try {
            LibraryAccount account = libraryService.createAccount(studentId, username);
            return ResponseEntity.ok(account);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/account/{studentId}")
    public ResponseEntity<LibraryAccount> getAccount(@PathVariable String studentId) {
        try {
            LibraryAccount account = libraryService.getAccount(studentId);
            return ResponseEntity.ok(account);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
