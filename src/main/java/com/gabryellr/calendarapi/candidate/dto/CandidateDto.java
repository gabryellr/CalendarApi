package com.gabryellr.calendarapi.candidate.dto;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDto {

    @Hidden
    private String id;

    @NotBlank(message = "Name cannot be null nor blank")
    private String name;

}