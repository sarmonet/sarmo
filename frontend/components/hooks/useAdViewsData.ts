import { useCallback, useEffect, useState } from 'react'

interface AdViewDataRow {
	pagePath: string
	views: string
}

interface UseAdViewsDataResult {
	data: AdViewDataRow[]
	loading: boolean
	error: string | null
	refetch: () => void
}

export const useAdViewsData = (
	startDate = '7daysAgo',
	endDate = 'today',
	limit = 10
): UseAdViewsDataResult => {
	const [data, setData] = useState<AdViewDataRow[]>([])
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState<string | null>(null)

	const fetchData = useCallback(async () => {
		setLoading(true)
		setError(null)
		const apiUrl = `/api/analytics/ad-views?startDate=${startDate}&endDate=${endDate}&limit=${limit}`

		try {
			const res = await fetch(apiUrl)
			const json = await res.json()

			if (res.ok) {
				setData(json)
			} else {
				const errorMessage =
					json.details ||
					json.error ||
					'Ошибка получения данных о просмотрах объявлений'
				console.error(
					'[useAdViewsData] API returned an error:',
					errorMessage,
					json
				)
				setError(errorMessage)
			}
		} catch (err) {
			const errorMessage = (err as Error).message
			console.error('[useAdViewsData] Fetch failed:', errorMessage, err)
			setError(errorMessage)
		} finally {
			setLoading(false)
		}
	}, [startDate, endDate, limit])

	useEffect(() => {
		fetchData()
	}, [fetchData])

	const refetch = () => {
		fetchData()
	}

	return { data, loading, error, refetch }
}
