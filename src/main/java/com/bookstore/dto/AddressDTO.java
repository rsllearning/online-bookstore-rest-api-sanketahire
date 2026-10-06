package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Shipping address payload")
public class AddressDTO {

    @NotBlank(message = "street is required.")
    @Schema(description = "Street line address", example = "456 Tech Park Way, Floor 4")
    private String street;

    @NotBlank(message = "city is required.")
    @Schema(description = "City", example = "Pune")
    private String city;

    @NotBlank(message = "state is required.")
    @Schema(description = "State / Province", example = "Maharashtra")
    private String state;

    @NotBlank(message = "postalCode is required.")
    @Schema(description = "Postal or ZIP code", example = "411057")
    private String postalCode;

    @NotBlank(message = "country is required.")
    @Schema(description = "Country", example = "India")
    private String country;
}
