package com.sarmo.userservice.repository;

import com.sarmo.userservice.entity.InvestorForm;
import com.sarmo.userservice.enums.InvestmentGoal;
import com.sarmo.userservice.enums.BudgetRange;
import com.sarmo.userservice.enums.TimeCommitment;
import com.sarmo.userservice.enums.BusinessSector;
import com.sarmo.userservice.enums.InvestmentCategory; // Импорт Enum
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestorFormRepository extends JpaRepository<InvestorForm, Long> {


    long countByInvestmentGoalsContaining(InvestmentGoal goal);
    long countByPreferredBusinessSectorsContaining(BusinessSector sector);
    long countByPreferredInvestmentCategoriesContaining(InvestmentCategory category); // Метод для подсчета по категориям

    // Подсчет по простым полям Enum и Boolean
    long countByBudget(BudgetRange budgetRange);
    long countByTimeCommitment(TimeCommitment timeCommitment);
    long countByBusinessExperience(Boolean businessExperience); // Позволит подсчитать true или false
}