package com.sarmo.listingservice.config;

import com.sarmo.listingservice.entity.*;
import com.sarmo.listingservice.repository.CategoryRepository;
import com.sarmo.listingservice.repository.SubCategoryRepository;
import com.sarmo.listingservice.repository.CategoryFieldRepository;
import com.sarmo.listingservice.repository.PackagingServiceInfoRepository;
import com.sarmo.listingservice.service.InvestmentCategoryFieldService; // Импорт нового сервиса

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional; // Импорт для Optional
import java.util.stream.Collectors;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final Long DEFAULT_INVESTMENT_CATEGORY_ID = 0L;

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryFieldRepository categoryFieldRepository;
    private final PackagingServiceInfoRepository packagingServiceInfoRepository;
    private final RestClient restClient;
    private final InvestmentCategoryFieldService investmentCategoryFieldService; // Новая зависимость

    @Value("${sarmo.app.base-url}")
    private String storageServiceBaseUrl;

    private final List<Field> predefinedDefaultInvestmentFields = List.of(
            new Field("Срок окупаемости (мес)", "Integer", false, true),
            new Field("Ожидаемая окупаемость инвестиций (Expected ROI %)", "Double", false, true),
            new Field("Интерес к соинвесторам", "Boolean", false, true),
            new Field("Есть ли у инвестора команда", "Boolean", false, true),
            new Field("Этап развития бизнеса", "List", true, false),
            new Field("Форма участия", "List", true, false),
            new Field("Тип сделки", "List", true, false),
            new Field("Предпочтения по команде основателей", "String", false, false),
            new Field("Стратегия выхода", "String", false, false)
    );

    public DatabaseInitializer(CategoryRepository categoryRepository,
                               SubCategoryRepository subCategoryRepository,
                               CategoryFieldRepository categoryFieldRepository,
                               PackagingServiceInfoRepository packagingServiceInfoRepository,
                               RestClient restClient,
                               InvestmentCategoryFieldService investmentCategoryFieldService) { // Новая зависимость
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.categoryFieldRepository = categoryFieldRepository;
        this.packagingServiceInfoRepository = packagingServiceInfoRepository;
        this.restClient = restClient;
        this.investmentCategoryFieldService = investmentCategoryFieldService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Checking and loading initial data...");

        boolean categoriesWereLoadedInThisRun = false;
        Map<Long, Category> originalIdToSavedCategoryMap = new HashMap<>();

        logger.info("Processing categories (checking for existing and creating new)...");
        long categoryCount = categoryRepository.count();

        if (categoryCount == 0) {
            logger.info("No categories found. Loading initial categories...");
            originalIdToSavedCategoryMap = initializeOrUpdateCategories();
            categoriesWereLoadedInThisRun = true;
            logger.info("Initial categories loaded.");
        } else {
            logger.info("Categories already exist. Skipping initial category loading.");

            List<Category> existingCategories = categoryRepository.findAll();

            originalIdToSavedCategoryMap = new HashMap<>();

            for (Category category : existingCategories) {
                Long originalId = category.getId();

                if (originalId != null) {
                    originalIdToSavedCategoryMap.put(originalId, category);
                } else {
                    logger.warn("Existing category found without an original ID: {}", category.getId());
                }
            }

            logger.info("Mapping of existing categories created (size: {}).", originalIdToSavedCategoryMap.size());

        }
        logger.info("Category processing finished.");

        logger.info("Processing sub-categories...");
        if (subCategoryRepository.count() == 0) {
            logger.info("No sub-categories found.");
            if (categoriesWereLoadedInThisRun) {
                logger.info("Categories were loaded initially, proceeding with sub-category loading...");
                if (!originalIdToSavedCategoryMap.isEmpty()) {
                    loadInitialSubCategories(originalIdToSavedCategoryMap);
                    logger.info("Initial sub-categories loaded.");
                } else {
                    logger.error("Cannot load initial sub-categories: Category mapping is empty despite categories being marked as loaded. This indicates an issue with category loading.");
                }
            } else {
                logger.info("Sub-categories skipped because categories were not loaded initially.");
            }
        } else {
            logger.info("Sub-categories already exist. Skipping initial sub-category loading.");
        }

        logger.info("Processing packaging service info...");
        if (packagingServiceInfoRepository.count() == 0) {
            logger.info("No packaging service info configuration found. Loading initial config...");
            loadInitialPackagingServiceInfo();
            logger.info("Initial packaging service info configuration loaded.");
        } else {
            logger.info("Packaging service info configuration already exists. Skipping initial loading.");
        }

        logger.info("Processing category fields...");
        if (categoryFieldRepository.count() == 0) {
            logger.info("No category field specifications found.");
                logger.info("Categories were loaded initially, proceeding with category field loading...");
                if (!originalIdToSavedCategoryMap.isEmpty()) {
                    loadInitialCategoryFields(originalIdToSavedCategoryMap);
                    logger.info("Initial category field specifications loaded.");

            } else {
                logger.info("Category field specifications skipped because categories were not loaded initially.");
            }
        } else {
            logger.info("Category field specifications already exist. Skipping initial loading.");
        }

        checkAndInitializeDefaultInvestmentFields();
        logger.info("Default investment fields check complete.");

        logger.info("Database initialization check complete.");
    }

    private void checkAndInitializeDefaultInvestmentFields() {
        logger.info("Checking and initializing default investment fields...");

        Optional<InvestmentCategoryField> existingDefaultFieldsOptional =
                investmentCategoryFieldService.getInvestmentCategoryField(DEFAULT_INVESTMENT_CATEGORY_ID);

        if (existingDefaultFieldsOptional.isEmpty()) {
            logger.info("Default investment fields not found. Creating new default configuration.");
            InvestmentCategoryField defaultFields = new InvestmentCategoryField(DEFAULT_INVESTMENT_CATEGORY_ID, predefinedDefaultInvestmentFields);
            investmentCategoryFieldService.createOrUpdateInvestmentCategoryField(defaultFields);
            logger.info("Default investment fields created successfully.");

        } else {
            InvestmentCategoryField existingDefaultFields = existingDefaultFieldsOptional.get();
            List<Field> currentFields = existingDefaultFields.getFields();

            if (!areFieldListsEqual(predefinedDefaultInvestmentFields, currentFields)) {
                logger.info("Existing default investment fields differ from predefined. Updating configuration.");
                existingDefaultFields.setFields(predefinedDefaultInvestmentFields);
                investmentCategoryFieldService.createOrUpdateInvestmentCategoryField(existingDefaultFields);
                logger.info("Default investment fields updated successfully.");
            } else {
                logger.info("Default investment fields found and match predefined configuration.");
            }
        }
    }


    /**
     * Helper method to compare two lists of Field objects based on their content (ignoring order).
     *
     * @param list1 The first list of Field objects.
     * @param list2 The second list of Field objects.
     * @return true if the lists contain the same fields with the same properties, false otherwise.
     */
    private boolean areFieldListsEqual(List<Field> list1, List<Field> list2) {
        if (list1 == list2) return true; // Same list reference
        if (list1 == null || list2 == null) return false; // One is null, the other isn't
        if (list1.size() != list2.size()) return false; // Different number of fields

        // Convert lists to maps for easier comparison by field name
        Map<String, Field> map1 = list1.stream()
                .collect(Collectors.toMap(Field::getName, f -> f));
        Map<String, Field> map2 = list2.stream()
                .collect(Collectors.toMap(Field::getName, f -> f));

        // Check if the sets of field names are the same
        if (!map1.keySet().equals(map2.keySet())) return false;

        // Iterate through one map and compare each field with its counterpart in the other map
        for (Map.Entry<String, Field> entry : map1.entrySet()) {
            String fieldName = entry.getKey();
            Field field1 = entry.getValue();
            Field field2 = map2.get(fieldName);

            // Compare individual Field properties (using equals for objects like Boolean)
            if (!field1.getName().equals(field2.getName()) ||
                    !field1.getType().equals(field2.getType()) ||
                    !field1.getRequired().equals(field2.getRequired()) ||
                    !field1.getFilterable().equals(field2.getFilterable())) {
                logger.debug("Field '{}' properties differ: {} vs {}", fieldName, field1, field2);
                return false;
            }
        }

        return true;
    }

    /**
     * Initializes or updates categories based on a predefined list.
     * If a category with the same name exists, it's used.
     * If a category is new, it's created and its initial image is uploaded.
     *
     * @return A map from original category ID to the saved/updated Category entity.
     */
    private Map<Long, Category> initializeOrUpdateCategories() {
        logger.info("Starting category initialization or update process...");

        List<CategoryData> categoryDataList = Arrays.asList(
                new CategoryData(1L, "Франшизы", "initial-images/categories/franchises.png"),
                new CategoryData(2L, "Готовый бизнес", "initial-images/categories/ready_business.png"),
                new CategoryData(3L, "IT-стартапы", "initial-images/categories/it_startups.png"),
                new CategoryData(4L, "Инвест проекты", "initial-images/categories/investment_projects.png"),
                new CategoryData(5L, "Коммерческая недвижимость", "initial-images/categories/commercial_real_estate.png"),
                new CategoryData(6L, "Бизнес идеи", "initial-images/categories/business_ideas.png"),
                new CategoryData(8L, "Бизнес планы", "initial-images/categories/business_plans.png")
        );

        Map<Long, Category> originalIdToSavedCategoryMap = new HashMap<>();
        String uploadUrl = storageServiceBaseUrl + "/api/v1/storage/image";

        for (CategoryData data : categoryDataList) {
            try {
                Optional<Category> existingCategoryOptional = categoryRepository.findByName(data.name());
                Category categoryToProcess;

                if (existingCategoryOptional.isPresent()) {
                    categoryToProcess = existingCategoryOptional.get();
                    logger.info("Category '{}' already exists (ID: {}). Using existing entity.", data.name(), categoryToProcess.getId());

                } else {
                    logger.info("Category '{}' not found. Creating new category and uploading image.", data.name());


                    String imageUrl = null;

                    try {
                        Resource imageResource = new ClassPathResource(data.localImagePath());
                        if (!imageResource.exists()) {
                            logger.warn("Initial image file not found at classpath: {}. Skipping image upload for category '{}'.", data.localImagePath(), data.name());
                        } else {
                            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                            body.add("file", imageResource);

                            logger.info("Uploading image {} for new category '{}' to {}", data.localImagePath(), data.name(), uploadUrl);

                            ResponseEntity<String> response = restClient.post()
                                    .uri(uploadUrl)
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                                    .body(body)
                                    .retrieve()
                                    .toEntity(String.class);

                            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                                imageUrl = response.getBody(); // Получаем URL из ответа
                                logger.info("Image uploaded successfully for new category '{}'. Received URL: {}", data.name(), imageUrl);
                            } else {
                                logger.error("Failed to upload image for new category '{}'. Storage Service returned status: {}. Body: {}",
                                        data.name(), response.getStatusCode(), response.getBody());
                            }
                        }
                    } catch (Exception uploadException) {
                        logger.error("Exception during image upload for new category '{}': {}", data.name(), uploadException.getMessage(), uploadException);
                    }

                    categoryToProcess = new Category();
                    categoryToProcess.setName(data.name());
                    categoryToProcess.setImageUrl(imageUrl);
                }

                Category savedCategory = categoryRepository.save(categoryToProcess);
                logger.info("Category '{}' saved/updated with ID: {}", savedCategory.getName(), savedCategory.getId());

                originalIdToSavedCategoryMap.put(data.originalId(), savedCategory);

            } catch (Exception e) {
                logger.error("Error processing category '{}': {}", data.name(), e.getMessage(), e);
            }
        }

        logger.info("Category initialization or update process finished.");
        return originalIdToSavedCategoryMap;
    }



    private void loadInitialSubCategories(Map<Long, Category> originalIdToSavedCategoryMap) {
        List<SubCategoryData> subCategoryDataList = Arrays.asList(
                new SubCategoryData(1L, "Авто", 1L),
                new SubCategoryData(2L, "Аптечный бизнес", 1L),
                new SubCategoryData(4L, "Детские франшизы", 1L),
                new SubCategoryData(5L, "Интернет и IT", 1L),
                new SubCategoryData(6L, "Медицины", 1L),
                new SubCategoryData(7L, "Обучение и образование", 1L),
                new SubCategoryData(8L, "Отдых и развлечения", 1L),
                new SubCategoryData(9L, "Красота и здоровье", 1L),
                new SubCategoryData(10L, "Питание", 1L),
                new SubCategoryData(11L, "Производство", 1L),
                new SubCategoryData(12L, "Торговля и магазины", 1L),
                new SubCategoryData(14L, "Спорт, здоровье и красота", 1L),
                new SubCategoryData(15L, "Туризм", 1L),
                new SubCategoryData(16L, "Строительство", 1L),
                new SubCategoryData(17L, "Сельское хозяйство", 1L),
                new SubCategoryData(18L, "Услуги для бизнеса (b2b)", 1L),
                new SubCategoryData(19L, "Услуги для населения", 1L),
                new SubCategoryData(20L, "Финансовые услуги", 1L),

                new SubCategoryData(21L, "Авто", 2L),
                new SubCategoryData(22L, "Автомойка", 2L),
                new SubCategoryData(23L, "Автосервис", 2L),
                new SubCategoryData(24L, "Аптечный бизнес", 2L),
                new SubCategoryData(25L, "Гостиница", 2L),
                new SubCategoryData(26L, "Детские центры и сады", 2L),
                new SubCategoryData(27L, "Здоровье и красота", 2L),
                new SubCategoryData(28L, "Инстаграм, YouTube, Telegram", 2L),
                new SubCategoryData(29L, "Кафе", 2L),
                new SubCategoryData(30L, "Магазин одежды", 2L),
                new SubCategoryData(31L, "Магазин продуктов", 2L),
                new SubCategoryData(32L, "Медцентр", 2L),
                new SubCategoryData(33L, "Обучение", 2L),
                new SubCategoryData(34L, "Общепит", 2L),
                new SubCategoryData(35L, "Производство", 2L),
                new SubCategoryData(36L, "Развлечение и отдых", 2L),
                new SubCategoryData(37L, "Ресторан", 2L),
                new SubCategoryData(38L, "Розничная торговля", 2L),
                new SubCategoryData(39L, "Салон красоты", 2L),
                new SubCategoryData(40L, "Сельхоз", 2L),
                new SubCategoryData(41L, "СМИ", 2L),
                new SubCategoryData(42L, "Строительство", 2L),
                new SubCategoryData(43L, "Транспортные услуги", 2L),
                new SubCategoryData(44L, "Туризм", 2L),
                new SubCategoryData(45L, "Консалтинг", 2L),
                new SubCategoryData(46L, "Услуги для бизнеса", 2L),
                new SubCategoryData(47L, "Услуги для населения", 2L),
                new SubCategoryData(48L, "Финансовые услуги", 2L),

                new SubCategoryData(49L, "Fintech", 3L),
                new SubCategoryData(50L, "Ecommerce", 3L),
                new SubCategoryData(51L, "AI & Big Data", 3L),
                new SubCategoryData(52L, "Healthcare", 3L),
                new SubCategoryData(53L, "Edtech", 3L),
                new SubCategoryData(54L, "Delivery", 3L),
                new SubCategoryData(55L, "Energy", 3L),
                new SubCategoryData(56L, "Blockchain", 3L),
                new SubCategoryData(57L, "Cybersecurity", 3L),
                new SubCategoryData(58L, "Agtech & New Food", 3L),
                new SubCategoryData(59L, "Gaming", 3L),
                new SubCategoryData(60L, "Sport", 3L),
                new SubCategoryData(61L, "B2B software", 3L),
                new SubCategoryData(62L, "Life Science", 3L),
                new SubCategoryData(63L, "Другие", 3L),

                new SubCategoryData(64L, "Авто", 4L),
                new SubCategoryData(65L, "Биотехнологии", 4L),
                new SubCategoryData(66L, "Туризм", 4L),
                new SubCategoryData(67L, "Детский бизнес", 4L),
                new SubCategoryData(68L, "Добыча полезных ископаемых", 4L),
                new SubCategoryData(69L, "Игры и киберспорт", 4L),
                new SubCategoryData(70L, "Логистика и транспортные услуги", 4L),
                new SubCategoryData(71L, "Медицина и здравоохранение", 4L),
                new SubCategoryData(72L, "Наука", 4L),
                new SubCategoryData(73L, "Искусственный интеллект и Робототехника", 4L),
                new SubCategoryData(74L, "Образование и обучение", 4L),
                new SubCategoryData(75L, "Общественное питание", 4L),
                new SubCategoryData(76L, "Производство", 4L),
                new SubCategoryData(77L, "Развлечения", 4L),
                new SubCategoryData(78L, "Сельское хозяйство", 4L),
                new SubCategoryData(79L, "Спорт и красота", 4L),
                new SubCategoryData(80L, "Строительство", 4L),
                new SubCategoryData(81L, "Торговля и коммерция", 4L),
                new SubCategoryData(82L, "Экология", 4L),

                new SubCategoryData(83L, "Свободного назначения", 5L),
                new SubCategoryData(84L, "Торговые площади", 5L),
                new SubCategoryData(85L, "Офисные помещения", 5L),
                new SubCategoryData(86L, "Коммерческие земли", 5L),
                new SubCategoryData(87L, "Производства", 5L),
                new SubCategoryData(88L, "Складские помещения", 5L),

                new SubCategoryData(89L, "В гараже", 6L),
                new SubCategoryData(90L, "В маленьком городе", 6L),
                new SubCategoryData(91L, "В сфере сервиса", 6L),
                new SubCategoryData(92L, "Для начинающих", 6L),
                new SubCategoryData(93L, "Для женщин", 6L),
                new SubCategoryData(94L, "Для мужчин", 6L),
                new SubCategoryData(95L, "Домашний бизнес", 6L),
                new SubCategoryData(96L, "Инновационные идеи", 6L),
                new SubCategoryData(97L, "Иностранные", 6L),
                new SubCategoryData(98L, "Минимальные вложения", 6L),
                new SubCategoryData(99L, "Производство", 6L),

                new SubCategoryData(115L, "Авто", 8L),
                new SubCategoryData(116L, "Дети", 8L),
                new SubCategoryData(117L, "Издательство", 8L),
                new SubCategoryData(118L, "ИТ и интернет", 8L),
                new SubCategoryData(119L, "Магазины", 8L),
                new SubCategoryData(120L, "Медицина", 8L),
                new SubCategoryData(121L, "Недвижимость и строительство", 8L),
                new SubCategoryData(122L, "Образование", 8L),
                new SubCategoryData(123L, "Общественное питание", 8L),
                new SubCategoryData(124L, "Отдых и развлечения", 8L),
                new SubCategoryData(125L, "Производство", 8L),
                new SubCategoryData(126L, "Услуги для населения", 8L),
                new SubCategoryData(127L, "Сельское хозяйство", 8L),
                new SubCategoryData(128L, "Спорт", 8L),
                new SubCategoryData(129L, "Услуги для бизнеса", 8L)
        );

        List<SubCategory> subCategoriesToSave = new ArrayList<>();

        for (SubCategoryData subData : subCategoryDataList) {
            Category parentCategory = originalIdToSavedCategoryMap.get(subData.originalCategoryId());

            if (parentCategory != null) {
                subCategoriesToSave.add(new SubCategory(subData.name(), parentCategory));
            } else {
                logger.warn("Parent category with original ID {} not found for sub-category '{}'. Skipping.", subData.originalCategoryId(), subData.name());
            }
        }

        if (!subCategoriesToSave.isEmpty()) {
            subCategoryRepository.saveAll(subCategoriesToSave);
            logger.info("{} sub-categories saved.", subCategoriesToSave.size());
        } else {
            logger.warn("No sub-categories were prepared for saving (parent category issues?).");
        }
    }

    private void loadInitialCategoryFields(Map<Long, Category> originalIdToSavedCategoryMap) {
        logger.info("Loading initial category field specifications...");

        List<CategoryFieldData> categoryFieldDataList = Arrays.asList(
                new CategoryFieldData(8L, Arrays.asList(
                        new Field("Срок окупаемости(мес)", "Integer", false, true),
                        new Field("Прогнозируемая доходность($/мес)", "Double", false, true),
                        new Field("Резюме проекта(краткий инвестиционный меморандум", "String", false, true),
                        new Field("Анализ и оценка рынка", "String", false, false),
                        new Field("Маркетинговая стратегия", "String", false, false),
                        new Field("План продаж и сбыта", "String", false, false),
                        new Field("Производственный план", "String", false, false),
                        new Field("Организационная структура", "String", false, false),
                        new Field("Кадровый план", "String", false, false),
                        new Field("Финансовый план", "String", false, false),
                        new Field("Инвестиционная программа", "String", false, false),
                        new Field("Источники финансирования", "String", false, false),
                        new Field("Потенциальные риски и способы их минимизации", "String", false, false)
                )),
                new CategoryFieldData(4L, Arrays.asList(
                        new Field("Срок окупаемости(мес)", "Integer", false, true),
                        new Field("Прогнозируемая доходность($/мес)", "Double", false, true),
                        new Field("Стадия проекта", "String", false, true),
                        new Field("Бизнес план", "File", false, false),
                        new Field("Конкурентные преимущества", "String", false, false),
                        new Field("Предлагаемые условия", "String", false, false),
                        new Field("Финансовая модель", "File", false, false),
                        new Field("Презентация", "File", false, false)
                )),
                new CategoryFieldData(3L, Arrays.asList(
                        new Field("Срок окупаемости(мес)", "Integer", false, true),
                        new Field("Прогнозируемая доходность($/мес)", "Double", false, true),
                        new Field("Стадия проекта", "String", false, true),
                        new Field("План развития", "String", false, false),
                        new Field("Конкурентные преимущества", "String", false, false),
                        new Field("Целевой рынок", "String", false, false),
                        new Field("Финансовая модель", "File", false, false),
                        new Field("Презентация", "File", false, false)

                )),

                new CategoryFieldData(5L, Arrays.asList(
                        new Field("Тип недвижимости", "String", true, true),
                        new Field("Вид объекта", "String", true, true),
                        new Field("Площадь(m²)", "Double", true, true),
                        new Field("Год постройки", "Integer", true, true),
                        new Field("Этаж", "Integer", true, false),
                        new Field("Этажность", "Integer", true, false),
                        new Field("Состояние объекта", "String", true, true),
                        new Field("Парковка", "Boolean", true, true),
                        new Field("Возможность ипотеки", "Boolean", true, true),
                        new Field("Инфраструктура рядом", "Boolean", false, false)
                )),

                new CategoryFieldData(6L, Arrays.asList(
                        new Field("Срок окупаемости(мес)", "Integer", false, true),
                        new Field("Прогнозируемая доходность($/мес)", "Double", false, true),
                        new Field("Стадия проекта", "String", false, true),
                        new Field("Необходимые ресурсы для реализации", "String", false, false),
                        new Field("Бизнес модель", "String", false, false),
                        new Field("Конкурентные преимущества", "String", false, false),
                        new Field("Целевой рынок", "String", false, false),
                        new Field("Предварительные финансовые расчёты", "String", false, false),
                        new Field("Потенциальные риски и способы их минимизации", "String", false, false),
                        new Field("Бизнес план", "File", false, false)
                )),

                new CategoryFieldData(2L, Arrays.asList(
                        new Field("Действующий бизнес", "Boolean", true, true),
                        new Field("Срок окупаемости(мес)", "Integer", false, true),
                        new Field("Среднемесячная выручка", "Double", true, true),
                        new Field("Среднемесячные расходы", "Double", true, true),
                        new Field("Возраст бизнеса", "Integer", true, true),
                        new Field("Количество сотрудников", "Integer", true, true),
                        new Field("Причина продажи", "String", false, false),
                        new Field("Доля к продаже(%)", "Double", true, true),
                        new Field("Организационно правовая форма", "String", false, true),
                        new Field("Продукция и услуги", "String", false, false),
                        new Field("Активы предприятия", "String", false, false),
                        new Field("Бизнес план", "File", false, false),
                        new Field("Финансовая модель", "File", false, false),
                        new Field("Презентация", "File", false, false)
                )),

                new CategoryFieldData(1L, Arrays.asList(
                        new Field("Срок окупаемости(мес)", "Integer", false, true),
                        new Field("Средняя прибыль", "Double", true, true),
                        new Field("Роялти", "Double", true, true),
                        new Field("Год основания компании", "Integer", true, true),
                        new Field("Год запуска франчайзинга", "Integer", true, true),
                        new Field("Паушальный взнос", "Double", true, true),
                        new Field("Собственные предприятия", "Integer", true, true),
                        new Field("Франчайзинговые предприятия", "Integer", true, true),
                        new Field("О компании", "String", false, true),
                        new Field("Обучение и поддержка", "String", false, false),
                        new Field("Требования", "String", false, false),
                        new Field("Финансовая модель", "File", false, false),
                        new Field("Презентация", "File", false, false)

                ))
        );


        List<CategoryField> initialCategoryFields = new ArrayList<>();

        for (CategoryFieldData data : categoryFieldDataList) {
            Category savedCategory = originalIdToSavedCategoryMap.get(data.originalCategoryId());

            if (savedCategory != null) {
                initialCategoryFields.add(new CategoryField(savedCategory.getId(), data.fields()));
            } else {
                logger.warn("Parent category with original ID {} not found for category field specification. Skipping.", data.originalCategoryId());
            }
        }

        for (CategoryField categoryField : initialCategoryFields) {
            categoryFieldRepository.save(categoryField); // Using save for each CategoryField
        }

        logger.info("Initial category field specifications saved to MongoDB.");
    }


    private void loadInitialPackagingServiceInfo() {
        PackagingServiceInfo initialConfig = new PackagingServiceInfo();

        initialConfig.setPageDesignName("Оформление страницы");
        initialConfig.setPageDesignPrice(new BigDecimal("500.00"));
        initialConfig.setPageDesignDescription("Профессиональное оформление страницы листинга для улучшения визуального восприятия.");

        initialConfig.setPresentationName("Презентация");
        initialConfig.setPresentationPrice(new BigDecimal("1000.00"));
        initialConfig.setPresentationDescription("Создание привлекательной презентации проекта/бизнеса для потенциальных инвесторов/покупателей.");

        initialConfig.setFinancialModelName("Финансовая модель");
        initialConfig.setFinancialModelPrice(new BigDecimal("1500.00"));
        initialConfig.setFinancialModelDescription("Разработка детальной финансовой модели, прогнозов и анализа инвестиционной привлекательности.");

        // TODO: Add data for other fixed services here (Name, Price, Description)

        initialConfig.setDiscountPercentage(new BigDecimal("10.00"));

        packagingServiceInfoRepository.save(initialConfig);
        logger.info("Initial packaging service info configuration saved.");
    }

    private record CategoryData(Long originalId, String name, String localImagePath) {
    }

    private record SubCategoryData(Long originalId, String name, Long originalCategoryId) {
    }

    private record CategoryFieldData(Long originalCategoryId, List<Field> fields) {
    }
}