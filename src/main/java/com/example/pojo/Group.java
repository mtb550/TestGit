package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class Group {
    List<GroupType> groupList;
    Color color;
}

enum GroupType {
    Regression,
    Smoke,
    Sanity
}
