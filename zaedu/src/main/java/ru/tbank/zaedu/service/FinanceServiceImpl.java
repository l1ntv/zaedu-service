package ru.tbank.zaedu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.enums.OperationType;
import ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import ru.tbank.zaedu.models.FinanceBalance;
import ru.tbank.zaedu.models.User;
import ru.tbank.zaedu.repo.FinanceBalanceRepository;
import ru.tbank.zaedu.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final UserRepository userRepository;

    private final FinanceBalanceRepository financeBalanceRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Override
    public void withdrawalMoney(Long countMoney, String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("UserNotFound"));
        FinanceBalance financeBalance = financeBalanceRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("UserBalanceNotFound"));

        String message = this.createMessage(
                user.getId(),
                financeBalance.getBalance(),
                OperationType.WITHDRAWAL,
                countMoney
        );

        kafkaTemplate.send("finance-service-request", message);
    }

    @Override
    public void replenishmentMoney(Long countMoney, String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("UserNotFound"));
        FinanceBalance financeBalance = financeBalanceRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("UserBalanceNotFound"));

        String message = this.createMessage(
                user.getId(),
                financeBalance.getBalance(),
                OperationType.REPLENISHMENT,
                countMoney
        );

        kafkaTemplate.send("finance-service-request", message);
    }

    private String createMessage(Long id, Long balance, OperationType operationType, Long count) {
        return id + ";" + balance + ";" + operationType + ";" + count;
    }
}
