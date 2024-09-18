package com.example.tasklist.web.dto.task;

import com.example.tasklist.domain.task.Status;
import com.example.tasklist.web.dto.validation.OnCreate;
import com.example.tasklist.web.dto.validation.OnUpdate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskDto {

    @NotNull(message = "Id must be not NULL.", groups = OnUpdate.class)
    private Long id;

    @NotNull(message = "The title must be not NULL.", groups = {OnUpdate.class, OnCreate.class})
    @Length(max = 255,
            message = "The title length must be smaller then 255 symbols.",
            groups = {OnUpdate.class, OnCreate.class}
    )
    private String title;

    @Length(max = 255,
            message = "Description length must be smaller then 255 symbols.",
            groups = {OnUpdate.class, OnCreate.class}
    )
    private String description;

    private Status status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expirationDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> images;

}
