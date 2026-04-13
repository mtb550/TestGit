package testGit.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.pojo.Groups;
import testGit.pojo.Priority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCaseDto {
    @Nullable
    private UUID next;

    @Nullable
    private Boolean isHead;

    @NotNull
    @Builder.Default
    private UUID id = UUID.randomUUID();

    /// TODO: change to name or description to match the testng
    @NotNull
    @Builder.Default
    private String title = "";

    @NotNull
    @Builder.Default
    private String expected = "";

    @NotNull
    @Builder.Default
    private List<String> steps = new ArrayList<>();

    @NotNull
    @Builder.Default
    private Priority priority = Priority.LOW;

    /// TODO: change this to PATH FCQN
    @NotNull
    @Builder.Default
    private String autoRef = "";

    @NotNull
    @Builder.Default
    private String busiRef = "";

    @NotNull
    @Builder.Default
    private List<Groups> groups = new ArrayList<>();

    @NotNull
    @Builder.Default
    private String createBy = "";

    @NotNull
    @Builder.Default
    private String updateBy = "";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @Builder.Default
    private LocalDateTime updateAt = LocalDateTime.now();

    @NotNull
    @Builder.Default
    private String module = "";

    @JsonIgnore
    @NotNull
    @Builder.Default
    private String tempStatus = "";

    @JsonIgnore
    @NotNull
    @Builder.Default
    private String tempError = "";

}
