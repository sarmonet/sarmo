package com.sarmo.userservice.service;

import com.sarmo.userservice.dto.InvestorFormRequestDto;
import com.sarmo.userservice.dto.InvestorFormResponseDto;
import com.sarmo.userservice.entity.InvestorForm;
import com.sarmo.userservice.entity.User; // Need User entity
import com.sarmo.userservice.enums.InvestmentGoal; // Import Enums
import com.sarmo.userservice.enums.BudgetRange;
import com.sarmo.userservice.enums.TimeCommitment;
import com.sarmo.userservice.enums.BusinessSector;
import com.sarmo.userservice.enums.InvestmentCategory;
import com.sarmo.userservice.repository.InvestorFormRepository; // Need InvestorForm repository
import com.sarmo.userservice.repository.UserRepository; // Need User repository
import com.sarmo.userservice.service.interfaces.InvestorFormService;
import jakarta.persistence.EntityNotFoundException; // Example exception
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Recommended for write operations

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // For mapping list

@Service
public class InvestorFormServiceImpl implements InvestorFormService {

    private static final Logger logger = LoggerFactory.getLogger(InvestorFormServiceImpl.class); // Add logger instance

    private final InvestorFormRepository investorFormRepository;
    private final UserRepository userRepository;

    public InvestorFormServiceImpl(InvestorFormRepository investorFormRepository, UserRepository userRepository) {
        this.investorFormRepository = investorFormRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional // Ensures operations are atomic
    public InvestorFormResponseDto saveOrUpdateInvestorForm(Long userId, InvestorFormRequestDto requestDto) {
        logger.info("Attempting to save or update investor form for user ID: {}", userId);

        // 1. Find the User entity
        // If user is not found, EntityNotFoundException is thrown automatically by orElseThrow
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new EntityNotFoundException("User not found with ID: " + userId);
                });
        logger.debug("User found with ID: {}", userId);

        // 2. Check if InvestorForm already exists for this user
        Optional<InvestorForm> existingFormOptional = investorFormRepository.findById(userId); // Find by user_id (which is the form's ID)

        InvestorForm investorForm;
        if (existingFormOptional.isPresent()) {
            // Update existing form
            investorForm = existingFormOptional.get();
            logger.debug("Existing investor form found for user ID: {}", userId);
            // Map fields from requestDto to existing entity
            mapRequestDtoToEntity(requestDto, investorForm);
            logger.debug("Mapped data from request DTO to existing form entity for user ID: {}", userId);

        } else {
            // Create new form
            investorForm = new InvestorForm();
            investorForm.setUser(user); // Set the user relation, @MapsId will handle setting the ID
            mapRequestDtoToEntity(requestDto, investorForm);
            logger.debug("Created new investor form entity for user ID: {}", userId);
        }

        // 3. Save the entity
        InvestorForm savedForm = investorFormRepository.save(investorForm);
        logger.info("Successfully saved/updated investor form for user ID: {}", userId);

        // 4. Map the saved entity back to Response DTO
        return mapEntityToResponseDto(savedForm);
    }

    @Override
    // Read operations typically do not require @Transactional unless lazy loading occurs after the session is closed
    public InvestorFormResponseDto getInvestorFormByUserId(Long userId) {
        logger.info("Attempting to get investor form for user ID: {}", userId);

        Optional<InvestorForm> investorFormOptional = investorFormRepository.findById(userId); // Find by user_id (primary key)

        if (investorFormOptional.isPresent()) {
            InvestorForm entity = investorFormOptional.get();
            logger.info("Investor form found for user ID: {}", userId);
            return mapEntityToResponseDto(entity); // Map entity to response DTO
        } else {
            logger.warn("Investor form not found for user ID: {}", userId);
            return null; // Return null if not found
        }
    }

    @Override
    @Transactional // Delete operation
    public void deleteInvestorFormByUserId(Long userId) {
        logger.info("Attempting to delete investor form for user ID: {}", userId);

        // Check if the form exists before attempting to delete to provide clearer feedback if needed
        boolean exists = investorFormRepository.existsById(userId);
        if (!exists) {
            logger.warn("Investor form not found for deletion for user ID: {}", userId);
            // Optional: Throw an exception if deleting a non-existent entity is considered an error
            // throw new EntityNotFoundException("Investor form not found for user ID: " + userId);
            // If no exception is thrown, deleteById on a non-existent ID typically does nothing silently.
        }

        investorFormRepository.deleteById(userId); // Delete by user_id (form's ID)
        logger.info("Successfully deleted investor form for user ID: {}", userId);
    }

    @Override
    // Read operation for all entities
    public List<InvestorFormResponseDto> getAllInvestorForms() {
        logger.info("Attempting to retrieve all investor forms");
        List<InvestorForm> entities = investorFormRepository.findAll();
        logger.info("Retrieved {} investor forms", entities.size());

        return entities.stream()
                .map(this::mapEntityToResponseDto) // Use the mapping helper
                .collect(Collectors.toList());
    }

    // --- Implementations for Statistics and Analysis Methods ---

    @Override
    public long countByInvestmentGoal(InvestmentGoal goal) {
        logger.debug("Counting investor forms by investment goal: {}", goal);
        return investorFormRepository.countByInvestmentGoalsContaining(goal);
    }

    @Override
    public long countByBudgetRange(BudgetRange budgetRange) {
        logger.debug("Counting investor forms by budget range: {}", budgetRange);
        return investorFormRepository.countByBudget(budgetRange);
    }

    @Override
    public long countByTimeCommitment(TimeCommitment timeCommitment) {
        logger.debug("Counting investor forms by time commitment: {}", timeCommitment);
        return investorFormRepository.countByTimeCommitment(timeCommitment);
    }

    @Override
    public long countByBusinessSector(BusinessSector sector) {
        logger.debug("Counting investor forms by business sector: {}", sector);
        return investorFormRepository.countByPreferredBusinessSectorsContaining(sector);
    }

    @Override
    public long countByInvestmentCategory(InvestmentCategory category) {
        logger.debug("Counting investor forms by investment category: {}", category);
        return investorFormRepository.countByPreferredInvestmentCategoriesContaining(category);
    }

    @Override
    public long countByBusinessExperience(Boolean businessExperience) {
        logger.debug("Counting investor forms by business experience: {}", businessExperience);
        // Handle null explicitly if your DB schema allows null and you need to count them
        // If you have a custom repository method like countByBusinessExperienceIsNull(), call it here when businessExperience is null
        return investorFormRepository.countByBusinessExperience(businessExperience); // Counts true or false
    }


    // --- Helper methods for mapping DTO <-> Entity ---

    private InvestorFormResponseDto mapEntityToResponseDto(InvestorForm entity) {
        // logger.trace("Mapping InvestorForm entity to Response DTO for ID: {}", entity != null ? entity.getId() : "null");
        if (entity == null) {
            return null;
        }
        return new InvestorFormResponseDto(entity);
    }

    private void mapRequestDtoToEntity(InvestorFormRequestDto dto, InvestorForm entity) {
        // logger.trace("Mapping Request DTO to InvestorForm entity");
        entity.setInvestmentGoals(dto.getInvestmentGoals());
        entity.setBusinessExperience(dto.getBusinessExperience());
        entity.setExperiencePeriod(dto.getExperiencePeriod());
        entity.setExperienceSphere(dto.getExperienceSphere());
        entity.setBudget(dto.getBudget());
        entity.setPreferredInvestmentCategories(dto.getPreferredInvestmentCategories());
        entity.setPreferredBusinessSectors(dto.getPreferredBusinessSectors());
        entity.setOtherSector(dto.getOtherSector());
        entity.setInterestsAndHobbies(dto.getInterestsAndHobbies());
        entity.setTimeCommitment(dto.getTimeCommitment());
        entity.setAboutMe(dto.getAboutMe());
        // ID and User are not mapped from the request DTO
    }
}