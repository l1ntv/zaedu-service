package ru.tbank.zaedu.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.zaedu.DTO.FeedbackRequest;
import ru.tbank.zaedu.models.MasterFeedback;
import ru.tbank.zaedu.service.FeedbackService;

import java.security.Principal;

@RestController
@RequestMapping("/feedback")
public class FeedbackController extends EntityController<MasterFeedback> {

    private final FeedbackService feedbackService;

    public FeedbackController(ModelMapper modelMapper, FeedbackService feedbackService) {
        super(modelMapper);
        this.feedbackService = feedbackService;
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<Void> giveFeedback(@PathVariable Long orderId, @RequestBody FeedbackRequest feedbackRequest, Principal principal) {
        String clientLogin = principal.getName();
        feedbackService.giveFeedback(orderId, feedbackRequest, clientLogin);
        return ResponseEntity.ok().build();
    }
}
