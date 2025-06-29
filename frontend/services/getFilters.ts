import { axiosInstance } from '@/utils/axiosInstance'

export const getListingsByHigherPrice = async () => {
	try {
		const response = await axiosInstance.get('listings/sorted/price')
		return response.data
	} catch (error) {
		console.warn(error)
	}
}
export const getListingsByLowerPrice = async () => {
	try {
		const response = await axiosInstance.get('listings/sorted/price/asc')
		return response.data
	} catch (error) {
		console.warn(error)
	}
}
