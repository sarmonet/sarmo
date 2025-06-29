import { ISubscribe } from '@/components/Catalog/Catalog-data/CatalogInterface/Subscribe.interface'
import { axiosInstanceSettings, axiosInstanceSubscription } from '@/utils/axiosInstance'
export const getSubs = async () => {
  try {
    const response = await axiosInstanceSubscription.get(
      `/plan`,
    );
    return response.data;
  } catch (error) {
    	console.warn('Error fetching subscription plans:', error); 
    throw error;
  }
};
export const postSubs = async (subscriptionPayload: ISubscribe) => {
  try {
    const response = await axiosInstanceSettings.post(
      `` , subscriptionPayload ,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    	console.warn('Error fetching subscription plans:', error); 
    throw error;
  }
};
export const putSubs = async (subscriptionChanged: ISubscribe) => {
  try {
    const response = await axiosInstanceSettings.put(
      `/${subscriptionChanged.id}` , subscriptionChanged ,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    	console.warn('Error fetching subscription plans:', error); 
    throw error;
  }
};
export const getProfileSubs = async () => {
  try {
    const response = await axiosInstanceSettings.get(
      ``  ,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}
          `,
          "Content-Type": "application/json",
        },
      }
    );
    return response.data;
  } catch (error) {
    	console.warn('Error fetching subscription plans:', error); 
    throw error;
  }
};



export const delProfileSubs = async (id: number) => {
  try {
    const response = await axiosInstanceSettings.delete(
      `/${id}`  ,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}
          `,
        },
      }
    );
    return response.data;
  } catch (error) {
    	console.warn('Error fetching subscription plans:', error); 
    throw error;
  }
};
