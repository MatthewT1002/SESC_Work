package com.library_service.library_service.controller;

import com.library_service.library_service.model.BorrowedBook;
import com.library_service.library_service.model.LibraryAccount;
import com.library_service.library_service.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String studentId,
                        @RequestParam String username,
                        Model model) {
        try {
            libraryService.getAccount(studentId);
            return "redirect:/library/" + studentId;
        } catch (RuntimeException e) {
            model.addAttribute("error", "Account not found. Please contact student services.");
            return "login";
        }
    }

    @GetMapping("/library/{studentId}")
    public String showLibrary(@PathVariable String studentId,
                              @RequestParam(required = false) String query,
                              Model model) {
        try {
            LibraryAccount account = libraryService.getAccount(studentId);
            model.addAttribute("account", account);
            model.addAttribute("studentId", studentId);

            if (query != null && !query.isEmpty()) {
                List<Map<String, String>> books = libraryService.searchBooks(query);
                model.addAttribute("books", books);
                model.addAttribute("query", query);
            }
            return "library";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/library/{studentId}/borrow")
    public String borrowBook(@PathVariable String studentId,
                             @RequestParam String isbn,
                             @RequestParam String title,
                             @RequestParam String author,
                             Model model) {
        try {
            libraryService.borrowBook(studentId, isbn, title, author);
            return "redirect:/library/" + studentId + "/borrowed";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("studentId", studentId);
            return "library";
        }
    }

    @GetMapping("/library/{studentId}/borrowed")
    public String showBorrowedBooks(@PathVariable String studentId, Model model) {
        try {
            List<BorrowedBook> borrowedBooks = libraryService.getBorrowedBooks(studentId);
            model.addAttribute("borrowedBooks", borrowedBooks);
            model.addAttribute("studentId", studentId);
            return "borrowed";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/library/{studentId}/return/{bookId}")
    public String returnBook(@PathVariable String studentId,
                             @PathVariable Long bookId,
                             Model model) {
        try {
            libraryService.returnBook(bookId);
            return "redirect:/library/" + studentId + "/borrowed";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "borrowed";
        }
    }
}
