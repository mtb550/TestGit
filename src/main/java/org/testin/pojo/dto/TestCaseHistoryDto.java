package org.testin.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString()
// todo, use @SuperBuilder
public class TestCaseHistoryDto {
    private String timestamp;

    private String changeSummary;
}
