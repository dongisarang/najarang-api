package com.najarang.back.controller;

import com.najarang.back.advice.exception.CUnauthorizedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exception")
public class ExceptionController {

    @GetMapping("/entrypoint")
    public void entrypointException() {
        throw new CUnauthorizedException();
    }

    @GetMapping("/accessdenied")
    public void accessdeniedException() {
        throw new AccessDeniedException("");
    }
}