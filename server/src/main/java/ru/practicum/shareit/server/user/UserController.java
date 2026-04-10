package ru.practicum.shareit.server.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.dto.CreateUserDTO;
import ru.practicum.shareit.common.dto.UpdateUserDTO;
import ru.practicum.shareit.common.dto.ResponseUserDTO;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<ResponseUserDTO> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseUserDTO getUser(@PathVariable int id) {
        return userService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseUserDTO create(@Valid @RequestBody CreateUserDTO newUser) {
        return userService.create(newUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseUserDTO update(@Valid @RequestBody UpdateUserDTO userToUpdate, @PathVariable int id) {
        return userService.update(id, userToUpdate);
    }

}
