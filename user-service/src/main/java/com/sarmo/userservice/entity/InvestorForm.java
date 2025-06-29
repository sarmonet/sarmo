package com.sarmo.userservice.entity;

import com.sarmo.userservice.enums.*;
import jakarta.persistence.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;


@Entity
@Table(name = "investor_form") // Updated table name to match entity
public class InvestorForm { // Renamed class from InvestorProfile to InvestorForm

    // Primary key which is also the foreign key to the User entity.
    // @MapsId indicates the primary key is taken from the associated User.
    @Id
    @Column(name = "user_id")
    private Long id;

    // One-to-one relationship with the User entity.
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user; // The user entity this form belongs to

    // Investment Goals (multiple choice)
    @ElementCollection
    @CollectionTable(name = "investor_goals", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING) // Store Enum name as String
    @Column(name = "goal")
    private List<InvestmentGoal> investmentGoals = new ArrayList<>();

    // Business Experience (Yes/No + details)
    private Boolean businessExperience;
    private String experiencePeriod;
    private String experienceSphere;

    // Budget (single choice)
    @Enumerated(EnumType.STRING) // Store Enum name as String
    @Column(name = "budget")
    private BudgetRange budget;

    // Preferred Investment Categories (multiple choice)
    @ElementCollection
    @CollectionTable(name = "investor_preferred_categories", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING) // Store Enum name as String
    @Column(name = "category")
    private List<InvestmentCategory> preferredInvestmentCategories = new ArrayList<>();

    // Preferred Business Sectors (multiple choice + Other)
    @ElementCollection
    @CollectionTable(name = "investor_sectors", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING) // Store Enum name as String
    @Column(name = "sector")
    private List<BusinessSector> preferredBusinessSectors = new ArrayList<>();
    private String otherSector; // Text field for "Other" if BusinessSector.OTHER is selected

    // Interests and Hobbies (free text)
    private String interestsAndHobbies;

    // Time Commitment (single choice)
    @Enumerated(EnumType.STRING) // Store Enum name as String
    @Column(name = "time_commitment")
    private TimeCommitment timeCommitment;

    // Additional Information / About Me (long text)
    @Column(columnDefinition = "TEXT") // Use a data type for long strings (e.g., TEXT)
    private String aboutMe;


    // --- Constructors ---
    public InvestorForm() {} // Default constructor (required by JPA)

    public InvestorForm(User user) {
        this.user = user;
        // ID will be set via @MapsId when persisted
    }


    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    // equals and hashCode based on id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvestorForm that = (InvestorForm) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}