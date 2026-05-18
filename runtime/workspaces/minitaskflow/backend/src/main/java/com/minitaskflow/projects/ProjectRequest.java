package com.minitaskflow.projects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(@NotBlank @Size(max = 120) String name) {
}
