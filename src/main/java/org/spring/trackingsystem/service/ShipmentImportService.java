package org.spring.trackingsystem.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.spring.trackingsystem.dto.ImportResultResponse;
import org.spring.trackingsystem.dto.ImportRowError;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ShipmentImportService {

    private final ShipmentRowPersister rowPersister;

    public ImportResultResponse importFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fajl za uvoz nije prilozen");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Fajl nema naziv");
        }

        String lowerName = filename.toLowerCase(Locale.ROOT);
        if (lowerName.endsWith(".csv")) {
            return importCsv(file);
        } else if (lowerName.endsWith(".xlsx")) {
            return importExcel(file);
        } else {
            throw new IllegalArgumentException(
                    "Nepodrzan format fajla. Podrzani formati su CSV (.csv) i Excel (.xlsx)");
        }
    }

    private ImportResultResponse importCsv(MultipartFile file) {
        int total = 0;
        int success = 0;
        List<ImportRowError> errors = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .setIgnoreEmptyLines(true)
                    .setIgnoreSurroundingSpaces(true)
                    .setIgnoreHeaderCase(true)
                    .build();

            CSVParser parser = format.parse(reader);
            for (CSVRecord record : parser) {
                total++;
                int rowNumber = (int) record.getRecordNumber() + 1;

                try {
                    rowPersister.persistRow(
                            valueOrNull(record, "trackingNumber"),
                            valueOrNull(record, "description"),
                            valueOrNull(record, "userEmail"),
                            valueOrNull(record, "status"),
                            valueOrNull(record, "deliveryAddress")
                    );
                    success++;
                } catch (Exception e) {
                    errors.add(new ImportRowError(rowNumber, e.getMessage()));
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Greska pri citanju CSV fajla: " + e.getMessage());
        }

        return buildResult(total, success, errors);
    }

    private ImportResultResponse importExcel(MultipartFile file) {
        int total = 0;
        int success = 0;
        List<ImportRowError> errors = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getRow(0) == null) {
                throw new IllegalArgumentException("Excel fajl ne sadrzi zaglavlje (header red)");
            }

            Map<String, Integer> columns = mapHeaders(sheet.getRow(0));
            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row, formatter)) {
                    continue;
                }
                total++;
                int rowNumber = i + 1;

                try {
                    rowPersister.persistRow(
                            cellValue(row, columns.get("trackingnumber"), formatter),
                            cellValue(row, columns.get("description"), formatter),
                            cellValue(row, columns.get("useremail"), formatter),
                            cellValue(row, columns.get("status"), formatter),
                            cellValue(row, columns.get("deliveryaddress"), formatter)
                    );
                    success++;
                } catch (Exception e) {
                    errors.add(new ImportRowError(rowNumber, e.getMessage()));
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Greska pri citanju Excel fajla: " + e.getMessage());
        }

        return buildResult(total, success, errors);
    }

    private Map<String, Integer> mapHeaders(Row headerRow) {
        Map<String, Integer> columns = new HashMap<>();
        DataFormatter formatter = new DataFormatter();
        for (Cell cell : headerRow) {
            String header = formatter.formatCellValue(cell).trim().toLowerCase(Locale.ROOT);
            if (!header.isBlank()) {
                columns.put(header, cell.getColumnIndex());
            }
        }
        return columns;
    }

    private boolean isRowEmpty(Row row, DataFormatter formatter) {
        for (Cell cell : row) {
            if (!formatter.formatCellValue(cell).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String cellValue(Row row, Integer columnIndex, DataFormatter formatter) {
        if (columnIndex == null) {
            return null;
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }
        String value = formatter.formatCellValue(cell).trim();
        return value.isBlank() ? null : value;
    }

    private String valueOrNull(CSVRecord record, String header) {
        if (!record.isMapped(header)) {
            return null;
        }
        String value = record.get(header);
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private ImportResultResponse buildResult(int total, int success, List<ImportRowError> errors) {
        return ImportResultResponse.builder()
                .totalRows(total)
                .successCount(success)
                .failedCount(errors.size())
                .errors(errors)
                .build();
    }
}
