package main.vaadinui.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzQ0NzM5ODgyLCJleHAiOjE3NDQ4MjYyODJ9.tB2hN669Tid2AvHTZQCDbxboq-ZpLbEOmBMsGyX7r9o";
    private String tokenType;
    private String username;
    private String role;
}