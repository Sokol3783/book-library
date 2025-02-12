package org.example.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewReaderDTO(
    @Size(min = 5, max = 30, message = "Invalid length. Name should contain more than 5 chars and less than 30 ones")
    @Pattern(regexp = "^[a-zA-Z '-]+$", message = "Name must contain only letters, spaces, dashes, apostrophes!")
    String name) {

}
