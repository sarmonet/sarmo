import { axiosInstanceStorage } from '@/utils/axiosInstance'


export const postImage = async (file: File) => {
  try {
    const formData = new FormData();
    formData.append("file", file);

    const response = await axiosInstanceStorage.post('image', formData); 

    return response.data;
  } catch (error) {
    console.error("❌ Ошибка при загрузке изображения:", error);
    throw error;
  }
};
export const postImages = async (files: File[]) => {
  const formData = new FormData();
  files.forEach(file => {
    formData.append("files", file); 
  });

  const response = await axiosInstanceStorage.post('image/multiple', formData);
  return response.data; 
};

export const postVideo = async (file: File) => {
  try {
    const formData = new FormData();
    formData.append("file", file);

    const response = await axiosInstanceStorage.post('video', formData); 

    return response.data;
  } catch (error) {
    console.error("❌ Ошибка при загрузке изображения:", error);
    throw error;
  }
};
export const postDoc = async (file: File) => {
  try {
    const formData = new FormData();
    formData.append("file", file);

    const response = await axiosInstanceStorage.post('document', formData);
     
    return response.data;
  } catch (error) {
    console.error("❌ Ошибка при загрузке изображения:", error);
    throw error;
  }
};