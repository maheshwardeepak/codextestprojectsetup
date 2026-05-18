package com.minitaskflow.tasks;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(@NotBlank @Size(max = 180) String title) {
}
