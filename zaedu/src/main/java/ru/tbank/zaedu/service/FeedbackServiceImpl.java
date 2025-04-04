package ru.tbank.zaedu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.DTO.FeedbackRequest;
import ru.tbank.zaedu.exceptionhandler.ConflictResourceException;
import ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ClientProfileRepository clientProfileRepository;

    private final MasterFeedbackRepository masterFeedbackRepository;

    @Override
    public void giveFeedback(Long orderId, FeedbackRequest feedbackRequest, String clientLogin) {
        if (!this.isEvaluationCorrect(feedbackRequest.getEvaluation())
                || !this.isDescriptionCorrect(feedbackRequest.getDescription())) {
            throw new ResourceNotFoundException("IncorrectFeedbackData");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderNotFound"));
        MasterProfile masterProfile = order.getMaster();
        if (masterProfile == null) {
            throw new ResourceNotFoundException("MasterProfileNotFound");
        }
        ClientProfile clientProfile = order.getClient();
        List<MasterFeedback> potentialDuplicateMasterFeedback = masterFeedbackRepository.findByOrder(order);

        if (!potentialDuplicateMasterFeedback.isEmpty()) {
            throw new ConflictResourceException("DuplicateMasterFeedback");
        }

        User client = userRepository.findByLogin(clientLogin)
                .orElseThrow(() -> new ResourceNotFoundException("AuthUserNotFound"));
        ClientProfile authClientProfile = clientProfileRepository.findByUser_Id(client.getId())
                .orElseThrow(() -> new ResourceNotFoundException("AuthClientProfileNotFound"));

        if (!clientProfile.equals(authClientProfile)) {
            throw new ConflictResourceException("OrderNotBelongToClient");
        }

        var masterFeedback = MasterFeedback.builder()
                .master(masterProfile)
                .client(clientProfile)
                .order(order)
                .evaluation(feedbackRequest.getEvaluation())
                .description(feedbackRequest.getDescription())
                .build();

        masterFeedbackRepository.save(masterFeedback);
    }

    private boolean isEvaluationCorrect(int evaluation) {
        return evaluation >= 1 && evaluation <= 5;
    }

    private boolean isDescriptionCorrect(String description) {
        return description.length() < 255;
    }

}
