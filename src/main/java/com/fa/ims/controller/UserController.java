package com.fa.ims.controller;

import com.fa.ims.dto.UserDto;
import com.fa.ims.entities.Role;
import com.fa.ims.service.RoleService;
import com.fa.ims.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final RoleService roleService;

    @GetMapping("/user-management")
    public String getViewUser(@RequestParam("search") Optional<String> search,
                              @RequestParam("role") Optional<String> role,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        String searchByKeyword = null;
        if (search.isPresent()) {
            searchByKeyword = search.get().trim();
            model.addAttribute("searchByKeyword", searchByKeyword);
        }
        String searchByRole = null;
        if (role.isPresent()) {
            searchByRole = role.get().trim();
            model.addAttribute("searchByRole", searchByRole);
        }
        Page<UserDto> userDtoPage = userService.findAll(searchByKeyword, searchByRole, page, size);
        model.addAttribute("userDtoPage", userDtoPage);
        return "/user/user-management";
    }

    @GetMapping("/create-user")
    public String getCreateUser(@ModelAttribute(name = "userDTO") UserDto userDTO,
                                Model model) {
        loadListRole(model);
        return "/user/create-user";
    }

    @PostMapping("/create-user")
    public String createUser(@ModelAttribute(name = "userDTO") @Valid UserDto userDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            loadListRole(model);
            return "/user/create-user";
        }
        if (userService.existsByUsername(userDTO.getUsername())) {
            loadListRole(model);
            bindingResult.rejectValue("username", "user.username.exist");
            return "/user/create-user";
        }
        if (userService.existsByEmail(userDTO.getEmail())) {
            loadListRole(model);
            bindingResult.rejectValue("email", "user.email.exist");
            return "/user/create-user";
        }
        userService.create(userDTO);
        redirectAttributes.addFlashAttribute("createUserSuccess","user.create.success");
        return "redirect:/create-user";
    }

    private void loadListRole(Model model) {
        List<Role> roles = roleService.findAll();
        model.addAttribute("roles", roles);
    }
}
