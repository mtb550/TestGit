package com.example.pojo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TestCase {

    private int uid;

    private int sort;

    @JsonAlias("tc_id")
    private String id;

    private String title;

    @JsonAlias("expected_result")
    private String expectedResult;

    private String steps;

    private String priority;

    @JsonAlias("auto_ref")
    private String automationRef;

    @JsonAlias("busi_ref")
    private String businessRef;

    private List<GroupType> groups;

    @JsonAlias("created_by")
    private String createBy;

    @JsonAlias("updated_by")
    private String updateBy;

    @JsonAlias("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    @JsonAlias("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    private String module;

    @JsonAlias("valid_from")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validFrom;

    @JsonAlias("valid_to")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validTo;


    @JsonSetter("groups")
    public void setGroupsFromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.groups = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace(System.out);
            this.groups = new ArrayList<>();
        }
    }

}
