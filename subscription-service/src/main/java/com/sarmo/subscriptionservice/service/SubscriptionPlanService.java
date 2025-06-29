package com.sarmo.subscriptionservice.service;

import com.sarmo.subscriptionservice.dto.CreatePlanFeatureDTO;
import com.sarmo.subscriptionservice.dto.CreateSubscriptionPlanDTO;
import com.sarmo.subscriptionservice.entity.PlanFeature;
import com.sarmo.subscriptionservice.entity.SubscriptionFeature;
import com.sarmo.subscriptionservice.entity.SubscriptionPlan;
import com.sarmo.subscriptionservice.repository.SubscriptionFeatureRepository;
import com.sarmo.subscriptionservice.repository.SubscriptionPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionPlanService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionPlanService.class);

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    private final SubscriptionFeatureRepository subscriptionFeatureRepository;

    public SubscriptionPlanService(SubscriptionPlanRepository subscriptionPlanRepository, SubscriptionFeatureRepository subscriptionFeatureRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.subscriptionFeatureRepository = subscriptionFeatureRepository;
    }

    public List<SubscriptionPlan> getAllSubscriptionPlans() {
        logger.info("Retrieving all subscription plans.");
        return subscriptionPlanRepository.findAll();
    }

    public Optional<SubscriptionPlan> getSubscriptionPlanById(Long id) {
        logger.info("Retrieving subscription plan by ID: {}", id);
        return subscriptionPlanRepository.findById(id);
    }

    public Optional<SubscriptionPlan> getSubscriptionPlanByName(String name) {
        logger.info("Retrieving subscription plan by name: {}", name);
        return subscriptionPlanRepository.findByName(name);
    }

//    public SubscriptionPlan createSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
//        logger.info("Creating new subscription plan: {}", subscriptionPlan.getName());
//        try {
//            return subscriptionPlanRepository.save(subscriptionPlan);
//        } catch (DataAccessException e) {
//            logger.error("Error creating subscription plan {}: {}", subscriptionPlan.getName(), e.getMessage());
//            return null;
//        }
//    }

//    public SubscriptionPlan createSubscriptionPlan(SubscriptionPlan incomingPlan) {
//        logger.info("Creating new subscription plan: {}", incomingPlan.getName());
//
//        // 1. Сначала сохраняем основной SubscriptionPlan
//        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(incomingPlan);
//
//        // 2. Обрабатываем PlanFeatures, если они есть в запросе
//        if (incomingPlan.getPlanFeatures() != null && !incomingPlan.getPlanFeatures().isEmpty()) {
//            List<PlanFeature> planFeaturesToSave = new ArrayList<>();
//            for (PlanFeature featureData : incomingPlan.getPlanFeatures()) {
//                if (featureData.getSubscriptionFeature() != null && featureData.getSubscriptionFeature().getId() != null) {
//                    // 3. Получаем соответствующий SubscriptionFeature из базы данных
//                    Optional<SubscriptionFeature> foundFeature = subscriptionFeatureRepository.findById(featureData.getSubscriptionFeature().getId());
//                    if (foundFeature.isPresent()) {
//                        // 4. Создаем новый PlanFeature и устанавливаем связи
//                        PlanFeature newPlanFeature = new PlanFeature();
//                        newPlanFeature.setSubscriptionPlan(savedPlan); // <--- Устанавливаем связь с сохраненным планом
//                        newPlanFeature.setSubscriptionFeature(foundFeature.get());
//                        newPlanFeature.setValue(featureData.getValue());
//                        newPlanFeature.setUnit(featureData.getUnit());
//                        planFeaturesToSave.add(newPlanFeature);
//                    } else {
//                        logger.warn("SubscriptionFeature with ID {} not found.", featureData.getSubscriptionFeature().getId());
//                        // Обработка ошибки: можно пропустить эту характеристику, выбросить исключение и т.д.
//                    }
//                }
//            }
//            // 5. Устанавливаем список PlanFeatures для сохраненного SubscriptionPlan
//            savedPlan.setPlanFeatures(planFeaturesToSave);
//            // 6. Еще раз сохраняем SubscriptionPlan, чтобы Hibernate сохранил связанные PlanFeatures (если настроено каскадирование)
//            return subscriptionPlanRepository.save(savedPlan);
//        }
//
//        return savedPlan; // Возвращаем сохраненный план (без характеристик, если их не было в запросе)
//    }

    public SubscriptionPlan createSubscriptionPlan(CreateSubscriptionPlanDTO dto) {
        logger.info("Creating new subscription plan from DTO: {}", dto.getName());

        // 1. Создание нового SubscriptionPlan из DTO
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(dto.getName());
        plan.setPrice(dto.getPrice());
        plan.setBillingCycle(dto.getBillingCycle());
        plan.setDescription(dto.getDescription());

        // 2. Сохраняем SubscriptionPlan без фичей
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);

        // 3. Создание и связывание PlanFeature-ов
        if (dto.getFeatures() != null && !dto.getFeatures().isEmpty()) {
            List<PlanFeature> planFeatures = new ArrayList<>();

            for (CreatePlanFeatureDTO featureDTO : dto.getFeatures()) {
                Long featureId = featureDTO.getFeatureId();
                Optional<SubscriptionFeature> featureOpt = subscriptionFeatureRepository.findById(featureId);

                if (featureOpt.isPresent()) {
                    PlanFeature pf = new PlanFeature();
                    pf.setSubscriptionPlan(savedPlan);
                    pf.setSubscriptionFeature(featureOpt.get());
                    pf.setValue(featureDTO.getValue());
                    pf.setUnit(featureDTO.getUnit());

                    planFeatures.add(pf);
                } else {
                    logger.warn("SubscriptionFeature with ID {} not found. Skipping.", featureId);
                }
            }

            savedPlan.setPlanFeatures(planFeatures);
            savedPlan = subscriptionPlanRepository.save(savedPlan); // пересохраняем, если есть каскадирование
        }

        return savedPlan;
    }

    public SubscriptionPlan updateSubscriptionPlan(Long id, SubscriptionPlan updatedPlan) {
        logger.info("Updating subscription plan with ID: {}", id);
        try {
            return subscriptionPlanRepository.findById(id)
                    .map(existingPlan -> {
                        boolean updated = false;
                        if (updatedPlan.getName() != null && !existingPlan.getName().equals(updatedPlan.getName())) {
                            existingPlan.setName(updatedPlan.getName());
                            updated = true;
                            logger.debug("Updated name to: {}", updatedPlan.getName());
                        }
                        if (updatedPlan.getPrice() != null && !existingPlan.getPrice().equals(updatedPlan.getPrice())) {
                            existingPlan.setPrice(updatedPlan.getPrice());
                            updated = true;
                            logger.debug("Updated price to: {}", updatedPlan.getPrice());
                        }
                        if (updatedPlan.getBillingCycle() != null && !existingPlan.getBillingCycle().equals(updatedPlan.getBillingCycle())) {
                            existingPlan.setBillingCycle(updatedPlan.getBillingCycle());
                            updated = true;
                            logger.debug("Updated billing cycle to: {}", updatedPlan.getBillingCycle());
                        }
                        if (updatedPlan.getDescription() != null && !existingPlan.getDescription().equals(updatedPlan.getDescription())) {
                            existingPlan.setDescription(updatedPlan.getDescription());
                            updated = true;
                            logger.debug("Updated description.");
                        }
                        if (updatedPlan.getPlanFeatures() != null && !existingPlan.getPlanFeatures().equals(updatedPlan.getPlanFeatures())) {
                            existingPlan.setPlanFeatures(updatedPlan.getPlanFeatures());
                            updated = true;
                            logger.debug("Updated plan features.");
                        }
                        if (updated) {
                            return subscriptionPlanRepository.save(existingPlan);
                        } else {
                            logger.info("No changes for subscription plan with ID: {}", id);
                            return existingPlan;
                        }
                    })
                    .orElseGet(() -> {
                        logger.warn("Subscription plan with ID {} not found.", id);
                        return null;
                    });
        } catch (DataAccessException e) {
            logger.error("Error updating subscription plan with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public void deleteSubscriptionPlan(Long id) {
        logger.warn("Deleting subscription plan with ID: {}", id);
        try {
            subscriptionPlanRepository.deleteById(id);
            logger.info("Subscription plan with ID {} successfully deleted.", id);
        } catch (DataAccessException e) {
            logger.error("Error deleting subscription plan with ID {}: {}", id, e.getMessage());
        }
    }
}