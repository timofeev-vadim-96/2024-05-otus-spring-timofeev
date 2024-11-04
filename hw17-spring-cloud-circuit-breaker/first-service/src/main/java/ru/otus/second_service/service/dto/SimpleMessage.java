package ru.otus.second_service.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleMessage {
    private String infoFromFirstService;

    private String infoFromSecondService;
}
