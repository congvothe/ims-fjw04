package com.fa.ims.controller;

import org.springframework.ui.Model;

import java.util.Optional;

public class BaseController {
     void getPageNumber(Optional<Integer> page, Model model) {
        int pageNumber = page.orElse(0);
        model.addAttribute("page", pageNumber);
    }
}
