 import axios from "axios"


 export const axiosInstanceDefault = axios.create({
  baseURL: "https://sarmo.net/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});
 export const axiosInstanceDoc = axios.create({
  baseURL: "https://sarmo.net/api/v1",

  withCredentials: true,
});
 export const axiosInstance = axios.create({
  baseURL: "https://sarmo.net/api/v1/listing",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});
 export const axiosInstanceRandom = axios.create({
  baseURL: "https://sarmo.net/api/v1/listing",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});
 export const axiosInstanceContent = axios.create({
  baseURL: "https://sarmo.net/api/v1/content",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});


export const axiosInstanceLogin = axios.create({
  baseURL: "https://sarmo.net/api/v1/auth/",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, 
});

export const axiosInstanceUsers = axios.create({
  baseURL: "https://sarmo.net/api/v1/user",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});
export const axiosInstanceSubscription = axios.create({
  baseURL: "https://sarmo.net/api/v1/subscription",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});
export const axiosInstanceSettings = axios.create({
  baseURL: "https://sarmo.net/api/v1/notice/settings",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});
export const axiosInstanceCommentaries = axios.create({
  baseURL: "https://sarmo.net/api/v1/listing",
  headers: {
    "Content-Type": "text/plain",
  },
  withCredentials: true,
});

export const axiosInstanceFavorite = axios.create({
  baseURL: "https://sarmo.net/api/v1/user/favorite",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});
export const axiosInstanceSupport = axios.create({
  baseURL: "https://sarmo.net/api/v1/user/transaction-support",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

export const axiosInstanceStorage = axios.create({
  baseURL: "https://sarmo.net/api/v1/storage/",
  headers: {
    "Content-Type": "multipart/form-data",
  },
});
export const axiosInstanceChat = axios.create({
  baseURL: "https://sarmo.net/api/v1/chat",
  headers: {
    "Content-Type": "multipart/form-data",
  },
});

axiosInstance.interceptors.request.use((config) => {
  const accessToken = localStorage.getItem("accessToken");
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});
