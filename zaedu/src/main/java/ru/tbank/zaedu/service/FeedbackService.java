package ru.tbank.zaedu.service;

import ru.tbank.zaedu.DTO.FeedbackRequest;

public interface FeedbackService {

    void giveFeedback(Long orderId, FeedbackRequest feedbackRequest, String clientLogin);

}
