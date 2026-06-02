package xyz.lavoute.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import xyz.lavoute.web.dto.UserGetDTO;
import xyz.lavoute.web.exceptions.DeletionException;
import xyz.lavoute.web.exceptions.NotAnAdministratorException;
import xyz.lavoute.web.exceptions.UserInvalidInformationsException;
import xyz.lavoute.web.services.AdminService;
import xyz.lavoute.web.exceptions.Error;

import java.util.Collection;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Integer> deleteUser(@PathVariable Integer id) {
        //TODO Ajout de la validation admin (authentication)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        adminService.deleteUser(id, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/obtain")
    public Collection<UserGetDTO> getAllUsers() {
        //TODO Ajout de la validation admin (authentication)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return adminService.getAllUsers(username);
    }

    @ExceptionHandler(NotAnAdministratorException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    Error handleNotAnAdministratorException(NotAnAdministratorException exception) {
        return new Error(exception.getMessage());
    }

    //TODO remplacer pour le NotexistingUser machin quand que le master va être merged
    @ExceptionHandler(UserInvalidInformationsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Error handleInvalidInformations(UserInvalidInformationsException exception) {
        return new Error(exception.getMessage());
    }

    @ExceptionHandler(DeletionException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    Error handleDeletionException(DeletionException exception) {
        return new Error(exception.getMessage());
    }
}
