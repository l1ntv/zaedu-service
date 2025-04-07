package ru.tbank.zaedu.service;

public interface FinanceService {
    void withdrawalMoney(Long countMoney, String login);

    void replenishmentMoney(Long countMoney, String login);

    Long getUserBalanceByLogin(String login);

    Long getUserBalanceById(Long id);
}
