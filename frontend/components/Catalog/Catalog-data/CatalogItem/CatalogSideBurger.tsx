import { getSubCategories } from '@/services/getCategories'
import {
  Filters,
  getCategoryMongoFilter,
  getFieldsById,
  useApplyFilters,
} from "@/services/getListings"
import { colors } from "@/utils"
import { useCallback, useEffect, useState } from "react"
import { IoCloseSharp, IoReturnUpBackOutline } from "react-icons/io5"
import { MdKeyboardArrowRight } from "react-icons/md"
import { useCatalog } from "../../CatalogContext/CatalogContext"
import { CatalogFilter } from "../../CatalogFilter/CatalogFilter"

import { ICatalogSub } from "../CatalogInterface/Catalog.interface"
import {
  CatalogSideBurgerFooter,
  CatalogSideBurgerHeader,
  CatalogSideBurgerList,
  CatalogSideBurgerWrapper
} from "./CatalogSideBurger.styled"

interface CatalogSideBurgerProps {
  isOpen: boolean;
  onClose: () => void;
}

export const CatalogSideBurger: React.FC<CatalogSideBurgerProps> = ({
  isOpen,
  onClose,
}) => {
  const {
    categories,
    subCategories,
    activeCategory,
    selectedSubCategory,
    setSubCategories,
    setFields,
    setFilteredListings,
    setActiveCategory,
    setSelectedSubCategory,
    setMongo,
    filters,
  } = useCatalog();

  const [, setLoading] = useState(false);
  const [additionalFilters, setAdditionalFilters] = useState<Record<string, unknown>>({});
  const [currentStep, setCurrentStep] = useState<'categories' | 'subcategories' | 'filters'>('categories');

  const allFilters: Filters = {
    ...(filters as Filters),
    ...additionalFilters,
  };

  const applyFilters = useApplyFilters(
    activeCategory,
    selectedSubCategory,
    allFilters,
    setFilteredListings,
    setLoading
  );

  const loadCategoryData = useCallback(async (categoryId: number) => {
    try {
      const [subs, mongoFilters, fieldsData] = await Promise.all([
        getSubCategories({ id: categoryId }),
        getCategoryMongoFilter({ id: categoryId }),
        getFieldsById({ id: categoryId }),
      ]);

      setMongo(mongoFilters || { fields: {} });
      setSubCategories(Array.isArray(subs) ? subs : []);
      setFields(fieldsData || {});
    } catch (error) {
      console.error("Ошибка загрузки данных:", error);
    }
  }, [setMongo, setSubCategories, setFields]);

  useEffect(() => {
    if (!isOpen) return;

    if (activeCategory) {
      if (selectedSubCategory) {
        setCurrentStep('filters');
      } else {
        setCurrentStep('subcategories');
        loadCategoryData(activeCategory.id);
      }
    } else {
      setCurrentStep('categories');
    }
  }, [isOpen, activeCategory, selectedSubCategory, loadCategoryData]);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleCategorySelect = (category: any) => {
    setActiveCategory(category);
  };

  const handleSubCategorySelect = (subItem: ICatalogSub) => {
    setSelectedSubCategory(subItem);
  };

  const handleBack = () => {
    if (selectedSubCategory) {
      setSelectedSubCategory(null);
    } else if (activeCategory) {
      setActiveCategory(null);
      setSubCategories([]);
      // setFields(null);
    } else {
      onClose();
    }
  };



  if (!isOpen) return null;

  return (
    <CatalogSideBurgerWrapper className={isOpen ? 'open' : ''}>
      <CatalogSideBurgerHeader>
        <button onClick={handleBack}>
					<IoReturnUpBackOutline size={21} color={colors.btnMainColor}/>
        </button>
        <span>
          {currentStep === "categories" 
            ? "Каталог" 
            : currentStep === "subcategories" 
              ? activeCategory?.name 
              : "Фильтры"}
        </span>
        <button onClick={onClose}>
					<IoCloseSharp size={21} color={colors.errorColor}/>
        </button>
      </CatalogSideBurgerHeader>

      {currentStep === "categories" && (
        <CatalogSideBurgerList>
          {categories.map((category) => (
            <li key={category.id} onClick={() => handleCategorySelect(category)}>
              {category.name}
              <MdKeyboardArrowRight />
            </li>
          ))}
        </CatalogSideBurgerList>
      )}

      {currentStep === "subcategories" && (
        <CatalogSideBurgerList>
          {subCategories.length > 0 ? (
            subCategories.map((subItem) => (
              <li key={subItem.id} onClick={() => handleSubCategorySelect(subItem)}>
                {subItem.name}
                <MdKeyboardArrowRight />
              </li>
            ))
          ) : (
            <p style={{ padding: "16px" }}>Нет подкатегорий</p>
          )}
        </CatalogSideBurgerList>
      )}

      {currentStep === "filters" && activeCategory && (
        <div style={{ padding: "16px" }}>
          <CatalogFilter
            categoryId={activeCategory.id}
            onFilterChange={setAdditionalFilters}
            applyFilters={applyFilters}
          />
        </div>
      )}

      <CatalogSideBurgerFooter>
			<div style={{ padding: "16px" }}>
            Найдено объявлений: {/* ... */}
       </div>
      </CatalogSideBurgerFooter>
    </CatalogSideBurgerWrapper>
  );
};