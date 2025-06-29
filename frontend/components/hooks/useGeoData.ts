import { useCallback, useEffect, useState } from 'react'

interface GeoDataRow {
	country: string
	users: string
}

interface UseGeoDataResult {
	data: GeoDataRow[]
	loading: boolean
	error: string | null
	refetch: () => void
}

export const useGeoData = (
	startDate = '7daysAgo',
	endDate = 'today',
	limit = 10
): UseGeoDataResult => {
	const [data, setData] = useState<GeoDataRow[]>([])
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState<string | null>(null)

	const fetchData = useCallback(async () => {
		setLoading(true)
		setError(null)
		const apiUrl = `/api/analytics/geo-data?startDate=${startDate}&endDate=${endDate}&limit=${limit}`
		console.log('[useGeoData] Fetching data from:', apiUrl)

		try {
			const res = await fetch(apiUrl)
			const json = await res.json()

			if (res.ok) {
				console.log('[useGeoData] Data fetched successfully:', json)
				setData(json)
			} else {
				const errorMessage =
					json.details || json.error || 'Ошибка получения географических данных'
				console.error('[useGeoData] API returned an error:', errorMessage, json)
				setError(errorMessage)
			}
		} catch (err) {
			const errorMessage = (err as Error).message
			console.error('[useGeoData] Fetch failed:', errorMessage, err)
			setError(errorMessage)
		} finally {
			setLoading(false)
			console.log('[useGeoData] Loading finished.')
		}
	}, [startDate, endDate, limit])

	useEffect(() => {
		console.log('[useGeoData] Effect triggered, calling fetchData...')
		fetchData()
	}, [fetchData])

	const refetch = () => {
		console.log('[useGeoData] Refetch requested.')
		fetchData()
	}

	return { data, loading, error, refetch }
}
