package testGit.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.pojo.Groups;
import testGit.pojo.Priority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCaseDto {
    @Nullable
    private UUID next;

    @Nullable
    private Boolean isHead;

    @NotNull
    private UUID id;

    ///  change to name or description to match the testng
    /// TODO: @NotNull
    private String title;

    /// TODO: @NotNull
    private String expected;

    /// TODO: @NotNull
    private List<String> steps;

    /// TODO: @NotNull
    private Priority priority;

    /// change this to PATH FCQN
    /// TODO: @NotNull
    private String autoRef;

    /// TODO: @NotNull
    private String busiRef;

    /// TODO: @NotNull
    private List<Groups> groups;

    private String createBy;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    private String module;

    @JsonIgnore
    private String tempStatus;

    @JsonIgnore
    private String tempError;

}
