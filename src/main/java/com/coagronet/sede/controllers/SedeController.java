package com.coagronet.sede.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.sede.dtos.SedeDTO;
import com.coagronet.sede.services.SedeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sede")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SedeController {

    private final SedeService sedeService;

    @GetMapping("/all")
    public List<SedeDTO> findAll() {
        return sedeService.findAll();
    }
}
