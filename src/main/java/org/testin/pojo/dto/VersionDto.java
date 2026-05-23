package org.testin.pojo.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString()
// todo, use @SuperBuilder
public class VersionDto {
    private int id;

    private double version;

    private String created_at;

    private String created_by;

    private String valid_from;

    private String valid_to;

    private int project_id;

    private int latest;

}
