package org.spring.trackingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportResultResponse {
    private int totalRows;
    private int successCount;
    private int failedCount;
    private List<ImportRowError> errors;
}