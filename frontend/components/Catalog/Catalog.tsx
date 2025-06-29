import { CatalogWrapper } from './Catalog.styled'
import { CatalogMain } from './CatalogMain/CatalogMain'
import { CatalogSideBar } from './CatalogSideBar/CatalogSideBar'

export const Catalog = () => {
  // const { setActiveCategory, setSelectedSubCategory, setIsNavigating } = useCatalog();
  // const location = usePathname();

  // useEffect(() => {
  //   setIsNavigating(true);
  
  //   const timeoutId = setTimeout(() => {
  //     setActiveCategory(null);
  //     setSelectedSubCategory(null);
  //     setIsNavigating(false);
  //   }, 300);
  
  //   return () => clearTimeout(timeoutId);
  // }, [location, setActiveCategory, setSelectedSubCategory, setIsNavigating]);

  return (
    <CatalogWrapper>
      <CatalogSideBar />
      <CatalogMain />
    </CatalogWrapper>
  );
};