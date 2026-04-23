package org.example.incomeandexpensebackend.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSelfDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}