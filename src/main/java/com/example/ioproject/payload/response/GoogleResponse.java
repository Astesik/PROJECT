package com.example.ioproject.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GoogleResponse {

    private String username;
    private String email;
    private boolean exists;

}
