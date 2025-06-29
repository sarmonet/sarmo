import { axiosInstance } from '@/utils/axiosInstance'



export const getCategories = async () => {
  try {
    const response = await axiosInstance.get('/category');
    return response.data;
  } catch (error) {
    console.error("❌ Ошибка при получении категорий:", error);
    throw error;
  }
};
export const getCategoriesWithFields = async () => {
  try {
    const response = await axiosInstance.get('/categories/with-fields');
    return response.data;
  } catch (error) {
    console.error("❌ Ошибка при получении категорий с полями:", error);
    throw error;
  }
};

export const getSubCategories = async ({ id }: { id: number }) => {
  try {
    const response = await axiosInstance.get(`/subcategory/by-category/${id}`);
    return Array.isArray(response.data) ? response.data : [response.data];
  } catch (error) {
    console.error(`❌ Ошибка при получении подкатегорий для ID ${id}:`, error);
    throw error;
  }
};
export const getCategoriesById = async (id : number) => {
  try {
    const response = await axiosInstance.get(`/subcategory/${id}`);
    return Array.isArray(response.data) ? response.data : [response.data];
  } catch (error) {
    console.error(`❌ Ошибка при получении подкатегорий для ID ${id}:`, error);
    throw error;
  }
};
