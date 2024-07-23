package org.example.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewBookDTO(
    @Size(min = 5, max = 100, message = "Invalid length. Title should contain more than 5 chars and less than 100 ones")
    @Pattern(regexp = "^[^|/#%=+*_><\\\\]*$", message = "Title contains invalid symbols: |/\\#%=+*_><]")
    String title,
    @Size(min = 5, max = 30, message = "Invalid length. Name should contain more than 5 chars and less than 30 ones")
    @Pattern(regexp = "^[\\w' -]+$", message = "Name must contain only letters, spaces, dashes, apostrophes!")
    String author) {

}