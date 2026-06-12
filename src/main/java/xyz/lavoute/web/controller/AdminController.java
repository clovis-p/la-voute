package xyz.lavoute.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import xyz.lavoute.web.dto.UserGetDTO;
import xyz.lavoute.web.exceptions.*;
import xyz.lavoute.web.exceptions.Error;
import xyz.lavoute.web.services.AdminService;

import java.util.Collection;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Called to delete a user and all his data (files, shares, permissions)
     * @param id the id of the user
     * @return code 200 OK if it worked
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Integer> deleteUser(@PathVariable Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        adminService.deleteUser(id, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Obtain a list of all users
     * @return code 200 OK with a list of all the users and their basic infos
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/obtain")
    public Collection<UserGetDTO> getAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return adminService.getAllUsers(username);
    }

    /**
     * Exceptions handling
     */
    @ExceptionHandler(NotAnAdministratorException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    Error handleNotAnAdministratorException(NotAnAdministratorException exception) {
        return new Error(exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    Error handleUserNotFoundException(UserNotFoundException exception) {
        return new Error(exception.getMessage());
    }

    @ExceptionHandler(DeletionException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    Error handleDeletionException(DeletionException exception) {
        return new Error(exception.getMessage());
    }
}
