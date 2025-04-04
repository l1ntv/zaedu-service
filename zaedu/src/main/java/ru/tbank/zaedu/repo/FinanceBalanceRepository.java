package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.zaedu.models.FinanceBalance;

@Repository
public interface FinanceBalanceRepository extends JpaRepository<FinanceBalance, Long> {

}
