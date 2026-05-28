package org.testin.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.testin.pojo.dto.TestCaseDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestRunItems {

    @JsonIgnore
    @NotNull
    private TestCaseDto tc;

    @NotNull
    private UUID id;

    @NotNull
    @Builder.Default
    private List<String> path = new ArrayList<>();

    @NotNull
    @Builder.Default
    private TestStatus status = TestStatus.PENDING;

    @NotNull
    @Builder.Default
    private String actualResult = "";

    @NotNull
    @Builder.Default
    private Duration duration = Duration.ZERO;

    @NotNull
    @Builder.Default
    private String executedBy = "";

    @NotNull
    @Builder.Default
    @JsonFormat(pattern = "EEEE hh:mm a dd.MM.yyyy", locale = "en_US")
    private LocalDateTime executedAt = LocalDateTime.now();

    @NotNull
    @Builder.Default
    private String stacktrace = "";
}