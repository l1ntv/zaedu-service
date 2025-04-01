package ru.tbank.zaedu.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedbackRequest {
    private Integer evaluation;
    private String description;
}
