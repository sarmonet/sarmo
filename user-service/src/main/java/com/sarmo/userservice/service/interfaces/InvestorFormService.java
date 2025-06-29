package com.sarmo.userservice.service.interfaces;

import com.sarmo.userservice.dto.InvestorFormRequestDto;
import com.sarmo.userservice.dto.InvestorFormResponseDto;
import com.sarmo.userservice.enums.InvestmentGoal; // Импорт Enum
import com.sarmo.userservice.enums.BudgetRange; // Импорт Enum
import com.sarmo.userservice.enums.TimeCommitment; // Импорт Enum
import com.sarmo.userservice.enums.BusinessSector; // Импорт Enum
import com.sarmo.userservice.enums.InvestmentCategory; // Импорт Enum

import java.util.List;

public interface InvestorFormService {


    /**
     * Saves or updates the investor form for a specific user.
     * This method is used by both authenticated user endpoints and admin endpoints.
     *
     * @param userId The ID of the user whose form is being saved/updated.
     * @param requestDto The DTO containing the form data.
     * @return The response DTO of the saved/updated form.
     */
    InvestorFormResponseDto saveOrUpdateInvestorForm(Long userId, InvestorFormRequestDto requestDto);

    /**
     * Retrieves the investor form for a specific user by their ID.
     *
     * @param userId The ID of the user whose form is being retrieved.
     * @return The response DTO of the investor form, or null if not found.
     */
    InvestorFormResponseDto getInvestorFormByUserId(Long userId);

    /**
     * Deletes the investor form for a specific user by their ID.
     *
     * @param userId The ID of the user whose form is being deleted.
     */
    void deleteInvestorFormByUserId(Long userId);

    // --- Методы для работы со всеми анкетами (для Admin) ---

    /**
     * Retrieves all investor forms. Typically for administrative use.
     *
     * @return A list of all investor forms as Response DTOs.
     */
    List<InvestorFormResponseDto> getAllInvestorForms();

    // --- Методы для статистики и анализа ---

    /**
     * Counts investor forms that selected a specific investment goal.
     *
     * @param goal The investment goal to count.
     * @return The count of forms.
     */
    long countByInvestmentGoal(InvestmentGoal goal);

    /**
     * Counts investor forms that selected a specific budget range.
     *
     * @param budgetRange The budget range to count.
     * @return The count of forms.
     */
    long countByBudgetRange(BudgetRange budgetRange);

    /**
     * Counts investor forms that selected a specific time commitment.
     *
     * @param timeCommitment The time commitment to count.
     * @return The count of forms.
     */
    long countByTimeCommitment(TimeCommitment timeCommitment);

    /**
     * Counts investor forms that selected a specific business sector.
     *
     * @param sector The business sector to count.
     * @return The count of forms.
     */
    long countByBusinessSector(BusinessSector sector);

    /**
     * Counts investor forms that selected a specific investment category.
     *
     * @param category The investment category to count.
     * @return The count of forms.
     */
    long countByInvestmentCategory(InvestmentCategory category);


    /**
     * Counts investor forms based on whether the user has business experience.
     *
     * @param businessExperience True for users with experience, False for those without.
     * Pass null to count forms where this field is not set.
     * @return The count of forms.
     */
    long countByBusinessExperience(Boolean businessExperience);
}