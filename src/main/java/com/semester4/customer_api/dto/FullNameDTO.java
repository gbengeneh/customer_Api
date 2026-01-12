package com.semester4.customer_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullNameDTO implements Serializable {
    @NotNull
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First Name must contain only alphabets")
    @Schema(description = "First name value", example = "user1")
    private String firstName;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last Name must contain only alphabets")
    @Schema(description = "Last name value", example = "user1")
    private String lastName;

    private String middleName;
}
