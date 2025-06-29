package com.sarmo.userservice.dto; // A common package for DTOs

import com.sarmo.userservice.enums.InvestmentGoal;
import com.sarmo.userservice.enums.BudgetRange;
import com.sarmo.userservice.enums.TimeCommitment;
import com.sarmo.userservice.enums.BusinessSector;
import com.sarmo.userservice.enums.InvestmentCategory;
import com.sarmo.userservice.entity.InvestorForm; // Import the InvestorForm entity

import java.util.List;
import java.util.ArrayList; // For initializing lists

// This DTO is used for data going OUT to the client (e.g., as the response body for a GET request)
// It INCLUDES the user ID so the client knows which user this form belongs to.
public class InvestorFormResponseDto {

    // User ID this form is associated with - Included for responses
    private Long userId;

    // Fields for the form data - Same as RequestDto
    private List<InvestmentGoal> investmentGoals = new ArrayList<>();
    private Boolean businessExperience;
    private String experiencePeriod;
    private String experienceSphere;
    private BudgetRange budget;
    private List<InvestmentCategory> preferredInvestmentCategories = new ArrayList<>();
    private List<BusinessSector> preferredBusinessSectors = new ArrayList<>();
    private String otherSector;
    private String interestsAndHobbies;
    private TimeCommitment timeCommitment;
    private String aboutMe;

    // --- Constructors ---
    // Default no-arg constructor is needed for JSON deserialization
    public InvestorFormResponseDto() {}

    // Constructor for convenient mapping from InvestorForm entity
    public InvestorFormResponseDto(InvestorForm entity) {
        this.userId = entity.getId(); // Map entity's ID (which is the user_id) to DTO's userId
        this.investmentGoals = entity.getInvestmentGoals();
        this.businessExperience = entity.getBusinessExperience();
        this.experiencePeriod = entity.getExperiencePeriod();
        this.experienceSphere = entity.getExperienceSphere();
        this.budget = entity.getBudget();
        this.preferredInvestmentCategories = entity.getPreferredInvestmentCategories();
        this.preferredBusinessSectors = entity.getPreferredBusinessSectors();
        this.otherSector = entity.getOtherSector();
        this.interestsAndHobbies = entity.getInterestsAndHobbies();
        this.timeCommitment = entity.getTimeCommitment();
        this.aboutMe = entity.getAboutMe();
    }

    // --- Getters and Setters ---
    // (You can generate these automatically in your IDE or use Lombok)

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<InvestmentGoal> getInvestmentGoals() { return investmentGoals; }
    public void setInvestmentGoals(List<InvestmentGoal> investmentGoals) { this.investmentGoals = investmentGoals; }

    public Boolean getBusinessExperience() { return businessExperience; }
    public void setBusinessExperience(Boolean businessExperience) { this.businessExperience = businessExperience; }
    public String getExperiencePeriod() { return experiencePeriod; }
    public void setExperiencePeriod(String experiencePeriod) { this.experiencePeriod = experiencePeriod; }
    public String getExperienceSphere() { return experienceSphere; }
    public void setExperienceSphere(String experienceSphere) { this.experienceSphere = experienceSphere; }

    public BudgetRange getBudget() { return budget; }
    public void setBudget(BudgetRange budget) { this.budget = budget; }

    public List<InvestmentCategory> getPreferredInvestmentCategories() { return preferredInvestmentCategories; }
    public void setPreferredInvestmentCategories(List<InvestmentCategory> preferredInvestmentCategories) { this.preferredInvestmentCategories = preferredInvestmentCategories; }

    public List<BusinessSector> getPreferredBusinessSectors() { return preferredBusinessSectors; }
    public void setPreferredBusinessSectors(List<BusinessSector> preferredBusinessSectors) { this.preferredBusinessSectors = preferredBusinessSectors; }
    public String getOtherSector() { return otherSector; }
    public void setOtherSector(String otherSector) { this.otherSector = otherSector; }

    public String getInterestsAndHobbies() { return interestsAndHobbies; }
    public void setInterestsAndHobbies(String interestsAndHobbies) { this.interestsAndHobbies = interestsAndHobbies; }

    public TimeCommitment getTimeCommitment() { return timeCommitment; }
    public void setTimeCommitment(TimeCommitment timeCommitment) { this.timeCommitment = timeCommitment; }

    public String getAboutMe() { return aboutMe; }
    public void setAboutMe(String aboutMe) { this.aboutMe = aboutMe; }
}