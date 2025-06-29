// components/hooks/useAnalytics.ts
import { useEffect, useState } from 'react'

interface AnalyticsRow {
	date: string
	users: string
	pageViews: string
	deviceCategory?: string
}

export const useAnalytics = (startDate = '7daysAgo', endDate = 'today') => {
	const [data, setData] = useState<AnalyticsRow[]>([])
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState<string | null>(null)

	useEffect(() => {
		const fetchData = async () => {
			setLoading(true)
			try {
				const res = await fetch(
					`/api/analytics?startDate=${startDate}&endDate=${endDate}`
				)
				const json = await res.json()
				if (res.ok) {
					setData(json)
				} else {
					setError(json.error || 'Ошибка получения аналитики')
				}
			} catch (err) {
				setError((err as Error).message)
			} finally {
				setLoading(false)
			}
		}

		fetchData()
	}, [startDate, endDate])

	return { data, loading, error }
}
