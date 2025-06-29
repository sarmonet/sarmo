package com.sarmo.userservice.entity;

import com.sarmo.userservice.entity.compositeKey.TransactionSupportId;
import jakarta.persistence.*;

@Entity
@Table(name = "transaction_support")
@IdClass(TransactionSupportId.class)
public class TransactionSupport {

    @Id
    @Column(name = "user_id")
    private Long userId; // Изменено на Long

    @Id
    @Column(name = "listing_id")
    private Long listingId;

    @Column(name = "negotiations_and_analysis")
    private Boolean negotiationsAndAnalysis;

    @Column(name = "preliminary_contract_conclusion")
    private Boolean preliminaryContractConclusion;

    @Column(name = "business_due_diligence")
    private Boolean businessDueDiligence;

    @Column(name = "financial_analysis")
    private Boolean financialAnalysis;

    @Column(name = "financial_plan_development")
    private Boolean financialPlanDevelopment;

    @Column(name = "main_contract_conclusion")
    private Boolean mainContractConclusion;

    @Column(name = "post_deal_support")
    private Boolean postDealSupport;

    public TransactionSupport(){}

    public TransactionSupport(Long userId, Long listingId, Boolean negotiationsAndAnalysis, Boolean preliminaryContractConclusion, Boolean businessDueDiligence, Boolean financialAnalysis, Boolean financialPlanDevelopment, Boolean mainContractConclusion, Boolean postDealSupport) {
        this.userId = userId;
        this.listingId = listingId;
        this.negotiationsAndAnalysis = negotiationsAndAnalysis;
        this.preliminaryContractConclusion = preliminaryContractConclusion;
        this.businessDueDiligence = businessDueDiligence;
        this.financialAnalysis = financialAnalysis;
        this.financialPlanDevelopment = financialPlanDevelopment;
        this.mainContractConclusion = mainContractConclusion;
        this.postDealSupport = postDealSupport;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public Boolean getNegotiationsAndAnalysis() {
        return negotiationsAndAnalysis;
    }

    public void setNegotiationsAndAnalysis(Boolean negotiationsAndAnalysis) {
        this.negotiationsAndAnalysis = negotiationsAndAnalysis;
    }

    public Boolean getPreliminaryContractConclusion() {
        return preliminaryContractConclusion;
    }

    public void setPreliminaryContractConclusion(Boolean preliminaryContractConclusion) {
        this.preliminaryContractConclusion = preliminaryContractConclusion;
    }

    public Boolean getBusinessDueDiligence() {
        return businessDueDiligence;
    }

    public void setBusinessDueDiligence(Boolean businessDueDiligence) {
        this.businessDueDiligence = businessDueDiligence;
    }

    public Boolean getFinancialAnalysis() {
        return financialAnalysis;
    }

    public void setFinancialAnalysis(Boolean financialAnalysis) {
        this.financialAnalysis = financialAnalysis;
    }

    public Boolean getFinancialPlanDevelopment() {
        return financialPlanDevelopment;
    }

    public void setFinancialPlanDevelopment(Boolean financialPlanDevelopment) {
        this.financialPlanDevelopment = financialPlanDevelopment;
    }

    public Boolean getMainContractConclusion() {
        return mainContractConclusion;
    }

    public void setMainContractConclusion(Boolean mainContractConclusion) {
        this.mainContractConclusion = mainContractConclusion;
    }

    public Boolean getPostDealSupport() {
        return postDealSupport;
    }

    public void setPostDealSupport(Boolean postDealSupport) {
        this.postDealSupport = postDealSupport;
    }
}