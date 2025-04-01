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
        if (feedbackRequest.getEvaluation() < 1 || feedbackRequest.getEvaluation() > 5 || feedbackRequest.getDescription().length() > 255) {
            throw new ResourceNotFoundException("IncorrectFeedbackData");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderNotFound"));

        List<MasterFeedback> potentialDuplicateMasterFeedback = masterFeedbackRepository.findByOrder(order);
        if (!potentialDuplicateMasterFeedback.isEmpty()) {
            throw new ConflictResourceException("DuplicateMasterFeedback");
        }

        ClientProfile clientProfile = order.getClient();

        User client = userRepository.findByLogin(clientLogin)
                .orElseThrow(() -> new ResourceNotFoundException("AuthUserNotFound"));
        ClientProfile authClientProfile = clientProfileRepository.findByUser_Id(client.getId())
                .orElseThrow(() -> new ResourceNotFoundException("AuthClientProfileNotFound"));

        if (!clientProfile.equals(authClientProfile)) {
            throw new ConflictResourceException("OrderNotBelongToClient");
        }

        MasterProfile masterProfile = order.getMaster();
        if (masterProfile == null) {
            throw new ResourceNotFoundException("MasterProfileNotFound");
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
}
