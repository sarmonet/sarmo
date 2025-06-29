package com.sarmo.userservice.dto; // A common package for DTOs

// Import necessary Enum types and List
import com.sarmo.userservice.enums.InvestmentGoal;
import com.sarmo.userservice.enums.BudgetRange;
import com.sarmo.userservice.enums.TimeCommitment;
import com.sarmo.userservice.enums.BusinessSector;
import com.sarmo.userservice.enums.InvestmentCategory;

import java.util.List;
import java.util.ArrayList; // For initializing lists

// This DTO is used for data coming IN from the client (e.g., in a POST or PUT request body)
// It does NOT contain the user ID, as the user ID is determined from the authentication token.
public class InvestorFormRequestDto {

    // Fields for the form data - Matches the questionnaire fields
    // NO userId field here

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
    public InvestorFormRequestDto() {}

    // --- Getters and Setters ---
    // (You can generate these automatically in your IDE or use Lombok)

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